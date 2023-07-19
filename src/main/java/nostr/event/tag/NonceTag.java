
package nostr.event.tag;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import nostr.base.annotation.Key;
import nostr.base.annotation.Tag;
import nostr.event.BaseTag;

/**
 *
 * @author squirrel
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@Tag(code = "nonce", nip = 13)
public class NonceTag extends BaseTag {

    @Key
    private Integer nonce;

    @Key
    private Integer difficulty;

    public NonceTag(@NonNull Integer nonce, @NonNull Integer difficulty) {
        this.nonce = nonce;
        this.difficulty = difficulty;
    }
}
