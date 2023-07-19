package nostr.business.mapper.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description 请描述类的业务用途
 * @Author young
 * @Date 2023/3/9 17:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("rps_send_message_log")
public class SendMessageLogPO {

    private String id;

    private String eventId;

    private String replyEventId;

    /**
     * 消息内容
     */
    private String event;

    /**
     * 发送节点集合 未指定所有节点发送
     */
    private String relay;

    /**
     * 消息nip
     */
    private Integer nip;

    /**
     *  CREATE=创建  SEND=发送中 OK=发送成功 FAIL=发送失败
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    public SendMessageLogPO(SendMessagePO messagePO){
        this.id = messagePO.getId();
        this.event = messagePO.getEvent();
        this.nip = messagePO.getNip();
        this.relay = messagePO.getRelay();
        this.status = messagePO.getStatus();
        this.createTime = messagePO.getCreateTime();
        this.sendTime = messagePO.getSendTime();
        this.modifyTime = messagePO.getModifyTime();
        this.eventId = messagePO.getEventId();
        this.replyEventId = messagePO.getReplyEventId();
    }

}
