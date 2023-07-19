
package nostr.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import nostr.base.IElement;

/**
 *
 * @author squirrel
 */
@Data
@AllArgsConstructor
@ToString
@Deprecated
public abstract class BaseMessage implements IElement {

    private final String command;
}
