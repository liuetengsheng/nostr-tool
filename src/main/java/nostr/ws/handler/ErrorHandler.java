
package nostr.ws.handler;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.java.Log;

import java.util.logging.Level;

/**
 *
 * @author squirrel
 */
@Builder
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@Log
public class ErrorHandler extends BaseHandler {

    private final Throwable cause;

    @Override
    public void process() {
        log.log(Level.SEVERE, cause.getMessage());        
    }

}
