
package nostr.ws.handler;

import lombok.extern.java.Log;
import nostr.base.IHandler;

import java.util.logging.Level;

/**
 *
 * @author squirrel
 */
@Log
public abstract class BaseHandler implements IHandler {

    @Override
    public void process() {
        log.log(Level.INFO, "process");
    }
    
}
