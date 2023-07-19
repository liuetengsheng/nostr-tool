
package nostr.ws.handler.response;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.java.Log;
import nostr.base.Command;

import java.util.logging.Level;

/**
 *
 * @author squirrel
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@Log
public class EoseResponseHandler extends BaseResponseHandler {

    private final String subscriptionId;

    public EoseResponseHandler(String subscriptionId) {
        super(Command.EOSE);
        this.subscriptionId = subscriptionId;
    }

    @Override
    public void process() {
        log.log(Level.INFO, "{0}", this);
    }
    
}
