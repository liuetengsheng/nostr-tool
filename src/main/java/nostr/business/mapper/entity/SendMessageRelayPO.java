package nostr.business.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description 请描述类的业务用途
 * @Author young
 * @Date 2023/3/10 16:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("rps_send_message_relay")
public class SendMessageRelayPO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 消息id
     */
    private String eventId;

    /**
     *  节点信息
     */
    private String relay;

    /**
     *  OK=发送成功 FAIL=发送失败
     */
    private String status;

    /**
     * 反馈内容
     */
    private String feedback;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
