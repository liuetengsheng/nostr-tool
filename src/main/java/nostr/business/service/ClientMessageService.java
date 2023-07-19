package nostr.business.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Bech32Prefix;
import nostr.business.config.ReadFiltersConfig;
import nostr.business.constant.MessageStatus;
import nostr.business.constant.OperateType;
import nostr.business.mapper.ClientMessageMapper;
import nostr.business.mapper.SendMessageLogMapper;
import nostr.business.mapper.SendMessageMapper;
import nostr.business.mapper.SendMessageRelayMapper;
import nostr.business.mapper.entity.ClientMessagePO;
import nostr.business.mapper.entity.SendMessageLogPO;
import nostr.business.mapper.entity.SendMessageRelayPO;
import nostr.crypto.bech32.Bech32;
import nostr.event.bean.ClientMessageEvent;
import nostr.event.bean.ClientReadMessageEvent;
import nostr.event.bean.ClientWriteMessageEvent;
import nostr.util.NostrException;
import nostr.ws.handler.IClientMessageService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


/**
 * @Description 请描述类的业务用途
 * @Author young
 * @Date 2023/2/6 15:16
 */
@Slf4j
@Service
public class ClientMessageService extends IClientMessageService {

    @Resource
    private ClientMessageMapper messageMapper;

    @Resource
    public ApplicationEventPublisher publisher;

    @Resource
    private SendMessageRelayMapper sendMessageRelayMapper;

    @Resource
    private SendMessageLogMapper sendMessageLogMapper;

    @Resource
    private SendMessageMapper sendMessageMapper;

//    @Value("${env.eth.privateKey}")
//    public String privateKey;

    /**
     * 录入用户消息
     * @param message 消息内容
     * @param type 消息类型
     */
    @Async("postExecutor")
    @Override
    public void dealEvent(String message,String type, LocalDateTime now){

        JSONArray array = JSON.parseArray(message);
        JSONObject jsonObject = array.getJSONObject(2);
        String eventId = jsonObject.getString("id");
        long count = messageMapper.selectCount(new LambdaQueryWrapper<ClientMessagePO>().eq(ClientMessagePO::getEventId, eventId));
        if (count > 0){
            log.info("当前消息已录入：{}",message);
            return;
        }

        // Decode message
        ClientMessagePO po = ClientMessagePO.builder()
                .type(type)
                .subId(array.getString(1))
                .eventId(eventId)
                .content(array.getString(2))
                .status(MessageStatus.CREATE)
                .publicKey(jsonObject.getString("pubkey"))
                .createdAt(jsonObject.getString("created_at"))
                .createTime(now)
                .modifyTime(LocalDateTime.now())
                .build();

        if (!po.verify()){
            log.info("当前消息验签未通过：{}",message);
            return;
        }

        // decode message
        String plaintextContext = "";
        try {
            plaintextContext = ReadFiltersConfig.eventSigner.decode(po);
        }catch (NostrException e){
            log.error("decode() NostrException", e.fillInStackTrace());
            return;
        }

        po.setPlaintextContext(plaintextContext);

        //Abandon Message
        if(Objects.nonNull(ReadFiltersConfig.eventCheck) && !ReadFiltersConfig.eventCheck.check(po)){
            return;
        }

//        String contentString = po.getContentString(NostrUtil.hexToBytes(privateKey));
        // 设置消息类型 读 还是 写
        po.setOperateType(OperateType.getType(po.getPlaintextContext()));
        // 设置用户nonce值信息
        po.analysisNonce();

        // 判断消息时间 是否符合标准
        boolean createdFlag = true; //MessageStatus.checkCreatedAt(po,now);
        try {
            po.setNpubAddress(Bech32.toBech32(Bech32Prefix.NPUB.getCode(), jsonObject.getString("pubkey")));
            // 保存消息
            messageMapper.insert(po);
        } catch (DuplicateKeyException e) {
            // 因为便利的relay节点 导致录入重复 抛弃当前key异常
        }catch (Exception e){
            log.error("保存消息异常：",e);
        }

        //符合标准的消息 才能发送消息监听
//        if (createdFlag) {
//            this.sendMsgEvent(po);
//        }
    }


    /**
     * 根据消息类型 发送不通的消息事件
     * @param po 消息
     */
    private void sendMsgEvent(ClientMessagePO po){
        ClientMessageEvent event;
        // 读写分离区分执行
        if (po.getOperateType() == 0){
             event = new ClientReadMessageEvent();
        }else {
            event = new ClientWriteMessageEvent();
        }
        event.setType(po.getType());
        event.setSubId(po.getSubId());
        event.setEventId(po.getEventId());
        event.setContent(po.getContent());
        event.setCreatedAt(po.getCreatedAt());
        event.setPublicKey(po.getPublicKey());
        event.setPlaintextContext(po.getPlaintextContext());
        event.setAgent(po.agent());
        // 发送事件 谁用到谁监听
        publisher.publishEvent(event);
    }


    public long getSysLastTime(){

        ClientMessagePO clientMessagePO = messageMapper.selectSysLastTime();

        if(clientMessagePO != null){
            return Long.parseLong(messageMapper.selectSysLastTime().getCreatedAt());
        }
        return System.currentTimeMillis()/1000;
    }

    @Async("postExecutor")
    @Override
    public void dealOk(String message, String relay) {
        List<String> lists = JSON.parseArray(message, String.class);
        String command = lists.get(0);
        String eventId = lists.get(1);

        // 获取消息，更新信息状态
        SendMessageLogPO sendMessagePO = sendMessageLogMapper.selectOne(
                new LambdaQueryWrapper<SendMessageLogPO>()
                        .eq(SendMessageLogPO::getEventId, eventId)
        );
        if (Objects.isNull(sendMessagePO)){
            return;
        }

        // 记录发送消息反馈内容
        sendMessageRelayMapper.insert(
                SendMessageRelayPO.builder()
                        .eventId(eventId)
                        .relay(relay)
                        .status(command)
                        .feedback(message)
                        .createTime(LocalDateTime.now())
                        .build()
        );
        // 更新发送记录状态 有一个节点成 就是成功
        if (!sendMessagePO.getStatus().equals("OK")){
            SendMessageLogPO update = SendMessageLogPO.builder()
                    .id(sendMessagePO.getId())
                    .status(command)
                    .modifyTime(LocalDateTime.now())
                    .build();
            sendMessageLogMapper.updateById(update);
        }
        // 发送失败的节点 打印信息
        if (!command.equals("OK")){
            log.error("当前{}节点，消息发送失败：{}",relay,message);
        }

        // 删除主表记录
        sendMessageMapper.deleteByEventId(eventId);
    }


    /**
     * Get new event list
     * @param priority
     * @return
     */
    public List<ClientMessagePO> getNewEvent(int priority){
        List<ClientMessagePO> pos = messageMapper.selectList(
                new LambdaQueryWrapper<ClientMessagePO>()
                        .eq(ClientMessagePO::getStatus, MessageStatus.CREATE)
                        .eq(ClientMessagePO::getOperateType, priority)
                        .orderBy(true,true,ClientMessagePO::getId)
                        .last("limit 100")
        );
        return pos;
    }



}
