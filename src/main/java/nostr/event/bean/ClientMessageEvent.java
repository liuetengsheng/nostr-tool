package nostr.event.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 请描述类的业务用途
 * @Author young
 * @Date 2023/2/9 16:53
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientMessageEvent {

    /**
     * 类型
     */
    private String type;
    /**
     * 数据id
     */
    private String subId;

    /**
     * eventId
     */
    private String eventId;
    /**
     * 原数据内容
     */
    private String content;
    /**
     * 发送时间
     */
    private String createdAt;
    /**
     * 公钥
     */
    private String publicKey;
    /**
     * 解析数据
     */
    private String plaintextContext;

    /**
     * 指定代理
     */
    private String agent;
}
