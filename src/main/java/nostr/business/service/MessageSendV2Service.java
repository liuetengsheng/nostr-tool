package nostr.business.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import nostr.base.PublicKey;
import nostr.base.Relay;
import nostr.event.list.TagList;
import nostr.business.config.WriteFiltersConfig;
import nostr.business.constant.MessageStatus;
import nostr.business.mapper.SendMessageLogMapper;
import nostr.business.mapper.SendMessageMapper;
import nostr.business.mapper.entity.SendEventContent;
import nostr.business.mapper.entity.SendMessageLogPO;
import nostr.business.mapper.entity.SendMessagePO;
import nostr.event.BaseEvent.ProxyEvent;
import nostr.event.Kind;
import nostr.event.impl.*;
import nostr.event.list.EventList;
import nostr.event.marshaller.impl.MessageMarshaller;
import nostr.event.message.CloseMessage;
import nostr.event.message.EventMessage;
import nostr.event.message.ReqMessage;
import nostr.event.tag.CustomizeTag;
import nostr.event.tag.EventTag;
import nostr.event.tag.PubKeyTag;
import nostr.event.tag.ThemeTag;
import nostr.util.NostrException;
import nostr.util.NostrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Send Nostr Message Service
 * 1. save origin message to db
 * 2. get mssage from db and sign it
 * 3. boardcast message to all relay
 */
@Service
@Slf4j
@DS("master")
public class MessageSendV2Service {
    @Resource
    private SendMessageMapper mapper;
    @Resource
    private SendMessageLogMapper messageLogMapper;

    public void sendPosteMsg(String from, String content) {
        this.sendPosteMsg(
                null,
                from,
                null,
                content,
                null
        );
    }

    //to : Comma separated public key array
    public void sendPosteMsg(String from, String to,String content) {
        this.sendPosteMsg(
                null,
                from,
                to,
                content,
                null
        );
    }

    //to : Comma separated public key array
    public void sendPosteMsg(String originEventId, String from, String to, String content) {
        List<CustomizeTag> customizeTags = new ArrayList<>();
        this.sendPosteMsg(
                originEventId,
                from,
                to,
                content,
                null
        );
    }

    //to : Comma separated public key array
    public void sendPosteMsg(String originEventId, String from, String to, String content, List<CustomizeTag> customizeTags) {
        try {
            SendEventContent sendEventContent = SendEventContent.builder()
                    .kind(1)
                    .originEventId(originEventId)
                    .from(from)
                    .to(to)
                    .content(content)
                    .tagList(customizeTags)
                    .build();

            String eventStr = JSONObject.toJSONString(sendEventContent);

            this.saveEventMsg(eventStr,originEventId, new ArrayList<>(), 12);
        } catch (Exception ex) {
            log.error("Create a private message and reply to the event  Exception : ", ex);
        }
    }

    /**
     * Create listening message event
     *
     * @param subId    subId
     * @param eventIds 事件id
     */
    public void addReferencedEventsReq(String subId, String[] eventIds) {
        if (eventIds == null || eventIds.length == 0) {
            log.error("eventIds is empty");
            return;
        }

        EventList eventList = new EventList();
        for (String eventId : eventIds) {
            eventList.add(new ProxyEvent(eventId));
        }

        Filters filters = Filters.builder().referencedEvents(eventList).build();

        nostr.event.list.FiltersList filtersList = new nostr.event.list.FiltersList();
        filtersList.add(filters);

        GenericMessage message = new ReqMessage(subId, filtersList);
        WriteFiltersConfig.writetClient.send(message);
    }

    /**
     * close listen
     * @param subId
     */
    public void closeEventReq(String subId) {
        if (subId == null || subId.length() == 0) {
            log.error("subId is empty");
            return;
        }

        GenericMessage message = new CloseMessage(subId);
        WriteFiltersConfig.writetClient.send(message);
    }

    public void sendPrivateMsg(String to, String from, String content) {
        this.sendPrivateMsg(
                null,
                from,
                to,
                content,
                null
        );
    }

    public void sendPrivateMsg(String originEventId, String from, String to, String content) {
        List<CustomizeTag> customizeTags = new ArrayList<>();
        this.sendPrivateMsg(
                originEventId,
                from,
                to,
                content,
                null
        );
    }

    public void sendPrivateMsg(String originEventId, String from, String to, String content, List<CustomizeTag> customizeTags) {
        try {
            SendEventContent sendEventContent = SendEventContent.builder()
                    .kind(4)
                    .originEventId(originEventId)
                    .from(from)
                    .to(to)
                    .content(content)
                    .tagList(customizeTags)
                    .build();

            String eventStr = JSONObject.toJSONString(sendEventContent);

            this.saveEventMsg(eventStr,originEventId, new ArrayList<>(), 12);
        } catch (Exception ex) {
            log.error("Create a private message and reply to the event  Exception : ", ex);
        }
    }

    //@TODO 临时代码,因之前发送人使用的 默认公钥, 导致form字段都是空, 兼容历史数据, 待新代码启用 历史数据清空后,删除此代码
    @Value("${nostr.pubKey}")
    private String PUBLICKEY;

    private GenericEvent buildEventFromContent(SendEventContent eventContent) throws NostrException {
        if(StringUtils.isBlank(eventContent.getFrom())){
            eventContent.setFrom(PUBLICKEY);
        }

        if(StringUtils.isBlank(eventContent.getFrom())){
            throw new NostrException("buildEventFromContent() identity is null");
        }

        PublicKey publicKeySender = new PublicKey(NostrUtil.hexToBytes(eventContent.getFrom()));

        TagList tagList = new TagList();

        if(StringUtils.isNotBlank(eventContent.getTo())){
            String [] toPublicKeyS = eventContent.getTo().split(",");
            for (String to : toPublicKeyS){
                PublicKey publicKeyRcpt = new PublicKey(NostrUtil.hexToBytes(to));
                tagList.add(PubKeyTag.builder().publicKey(publicKeyRcpt).build());
            }
        }

        // Event Need here
        if (StringUtils.isNotBlank(eventContent.getOriginEventId())) {
            GenericEvent originEvent = new ProxyEvent(eventContent.getOriginEventId());
            tagList.add(EventTag.builder().relatedEvent(originEvent).build());
        }

        // NIP 12 消息回复
        if (CollectionUtils.isNotEmpty(eventContent.getTagList())) {
            for (CustomizeTag c : eventContent.getTagList()) {
                ThemeTag themeTag = new ThemeTag(c.getValue(), c.getTagName().toString());
                tagList.add(themeTag);
            }
        }
        GenericEvent genericEvent = null;
        if(eventContent.getKind() == Kind.ENCRYPTED_DIRECT_MESSAGE.getValue()){
            DirectMessageEvent directMessageEvent = new DirectMessageEvent(publicKeySender, tagList, eventContent.getContent());
            WriteFiltersConfig.eventSigner.encryptDirectMessage(directMessageEvent);
            genericEvent = directMessageEvent;
        }else {
            genericEvent = new TextNoteEvent(publicKeySender, tagList, eventContent.getContent());
        }

        WriteFiltersConfig.eventSigner.sign(genericEvent);
        return genericEvent;
    }

    private void buildMention(TagList tagList, List<String> mentionList) {
        if (CollectionUtils.isNotEmpty(mentionList)) {
            for (String s : mentionList) {
                PublicKey publicKey = new PublicKey(NostrUtil.hexToBytes(s));
                tagList.add(PubKeyTag.builder().publicKey(publicKey).build());
            }
        }
    }

    private void buildCustomizeTags(TagList tagList, List<CustomizeTag> customizeTags) {
        if (CollectionUtils.isNotEmpty(customizeTags)) {
            for (CustomizeTag c : customizeTags) {
                ThemeTag themeTag = new ThemeTag(c.getValue(), c.getTagName().toString());
                tagList.add(themeTag);
            }
        }
    }

    /**
     * 保存发送消息
     *
     * @param msg    消息内容
     * @param relays 指定发送节点
     */
    private void saveEventMsg(String msg,String originEventId, List<String> relays, Integer nip) {
        String id = System.currentTimeMillis() + "" + new Random().nextInt(9000000, 9999999);
        int count = mapper.insert(
                SendMessagePO.builder()
                        .id(id)
                        .event(msg)
                        .nip(nip)
                        .replyEventId(originEventId)
                        .relay(JSON.toJSONString(relays))
                        .status("CREATE")
                        .createTime(LocalDateTime.now())
                        .modifyTime(LocalDateTime.now())
                        .build()
        );
        if (count == 0) {
            log.error("save faild：{}", msg);
        }
    }

    public void executeSendMsg() {
        List<SendMessagePO> pos = mapper.selectList(new LambdaQueryWrapper<SendMessagePO>()
                .eq(SendMessagePO::getStatus, "CREATE")
                .orderByAsc(SendMessagePO::getCreateTime));
        if (CollectionUtils.isEmpty(pos)) {
            return;
        }
        pos.forEach(this::sendMsg);
    }

    @Async
    public void sendMsg(SendMessagePO po) {
        // Determine if there is a specified sending node
        try {
            List<String> relays = JSON.parseArray(po.getRelay(), String.class);
            Relay relay = WriteFiltersConfig.writetClient.getRelays().stream().findFirst().get();

            GenericEvent event = null;
            try {
                if(StringUtils.isBlank(po.getEvent())){
                    throw new RuntimeException("The message content cannot be empty");
                }
                SendEventContent content = JSONObject.parseObject(po.getEvent(), SendEventContent.class);
                event = buildEventFromContent(content);

            }catch (NostrException e){
                log.error("sendMsg() NostrException ", e.fillInStackTrace());
                SendMessagePO updatePo = SendMessagePO.builder().id(po.getId()).status(MessageStatus.FAIL).modifyTime(LocalDateTime.now()).sendTime(LocalDateTime.now()).build();

                mapper.updateById(updatePo);
                return;
            }

            // update send data
            SendMessagePO updatePo = SendMessagePO.builder()
                    .id(po.getId())
                    .eventId(event.getId())
                    .status("SEND")
                    .modifyTime(LocalDateTime.now())
                    .sendTime(LocalDateTime.now())
                    .build();

            mapper.updateById(updatePo);

            // insert send log
            po.setStatus("SEND");
            po.setSendTime(LocalDateTime.now());
            po.setModifyTime(LocalDateTime.now());
            po.setEventId(event.getId());
            // Start recording messages
            messageLogMapper.insert(new SendMessageLogPO(po));

            GenericMessage message = new EventMessage(event);
            String msg = new MessageMarshaller(message, relay).marshall();

            if (CollectionUtils.isEmpty(relays)) {
                WriteFiltersConfig.writetClient.sendMsgJson(msg, po.getNip());
            } else {
                relays.forEach(r -> WriteFiltersConfig.writetClient.sendMsgJson(msg, po.getNip(), r));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Current message sending failed：", e);
        }
    }
}
