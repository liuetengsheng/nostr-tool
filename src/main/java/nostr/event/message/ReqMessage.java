
package nostr.event.message;

import lombok.*;
import nostr.base.Command;
import nostr.event.list.FiltersList;
import nostr.event.impl.GenericMessage;

/**
 *
 * @author squirrel
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class ReqMessage extends GenericMessage {

    private final String subscriptionId;
    private final FiltersList filtersList;

    public ReqMessage(String subscriptionId, FiltersList filtersList) {
        super(Command.REQ.name());
        this.subscriptionId = subscriptionId;
        this.filtersList = filtersList;
    }
}
