package nostr.event.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 请描述类的业务用途
 * @Author young
 * @Date 2023/3/30 16:27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomizeTag {

    private Character tagName;

    private String value;
}
