package nostr.business.constant;

import lombok.extern.slf4j.Slf4j;
import nostr.business.mapper.entity.ClientMessagePO;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @Description 请描述类的业务用途
 * @Author young
 * @Date 2023/6/9 15:26
 */
@Slf4j
public class MessageStatus {


    // 初始化
    public static final String CREATE = "CREATE";

    // 执行中
    public static final String EXECUTE = "EXECUTE";

    // 处理完成
    public static final String SUCCESS = "SUCCESS";

    // 处理失败
    public static final String FAIL = "FAIL";

    // 请求频繁 不处理
    public static final String TOO_MANY = "TOO_MANY";

    // 过期消息 不处理
    public static final String EXPIRE = "EXPIRE";

    // 未来消息 以后处理
    public static final String FUTURE = "FUTURE";


    /**
     * 判断当前发送消息是否在时间允许范围之内
     * 写消息 一天前消息数据不处理，当前消息状态改为过期
     * 读取消息 一秒之前消息不处理，设置过期，超出当前时间，以后执行
     * @param clientMessagePO 消息
     * @return 结果
     */
    public static boolean checkCreatedAt(ClientMessagePO clientMessagePO,LocalDateTime time){
        // 先设置可执行状态
        clientMessagePO.setStatus(CREATE);

        // 写消息 一天前消息数据不处理，当前消息状态改为过期
        if (clientMessagePO.getOperateType() == 1){
            // 获取 24 小时前时间戳
            long beforeTime = time.minusDays(1).toEpochSecond(ZoneOffset.ofHours(8));
            log.info("收到写入消息时间过滤：{},{},{}",clientMessagePO.getEventId(),clientMessagePO.getCreatedAt(),beforeTime);

            if (Long.parseLong(clientMessagePO.getCreatedAt()) >= beforeTime){
                return true;
            }
            clientMessagePO.setModifyTime(LocalDateTime.now());
            clientMessagePO.setStatus(EXPIRE);
            return false;
        }else {
            // 一秒钟前消息数据不处理，改为过期，同时created_at 是否在当前秒内, 不是不处理
            // 获取当前秒级时间戳
            long thisTime = time.toEpochSecond(ZoneOffset.ofHours(8));
            log.info("收到读取消息时间过滤：{},{},{}",clientMessagePO.getEventId(),clientMessagePO.getCreatedAt(),thisTime);

            // 当前秒内消息 开始处理（ -1秒 容许0秒999毫秒这种可以执行）因为消息发送时间到和当前服务器时间 有毫秒级延迟
            if (Long.parseLong(clientMessagePO.getCreatedAt()) == thisTime || Long.parseLong(clientMessagePO.getCreatedAt()) == (thisTime -1)){
                return true;
            }

            // 用户设置消息时间超前 不先处理 等待到时间定时任务扫描处理
            if (Long.parseLong(clientMessagePO.getCreatedAt()) > thisTime){
                clientMessagePO.setModifyTime(LocalDateTime.now());
//                clientMessagePO.setStatus(FUTURE);
                clientMessagePO.setStatus(EXPIRE);
                return false;
            }

            // 剩下的都是过期消息 直接设置过期
            clientMessagePO.setModifyTime(LocalDateTime.now());
            clientMessagePO.setStatus(EXPIRE);
            return false;
        }
    }

}
