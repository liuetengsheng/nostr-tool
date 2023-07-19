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
@TableName("rps_send_message")
public class SendMessagePO {

    private String id;

    private String eventId;

    private String replyEventId;

    /**
     * 消息
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


}
