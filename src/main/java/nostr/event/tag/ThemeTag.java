package nostr.event.tag;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import nostr.base.annotation.Key;
import nostr.event.BaseTag;

/**
 * @Description 请描述类的业务用途
 * @Author young
 * @Date 2023/3/30 15:16
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public final class ThemeTag extends BaseTag {

    @Key
    private String theme;

    private String code;

    public ThemeTag(@NonNull String theme, String code) {
        this.theme = theme;
        this.code = code;
    }
}
