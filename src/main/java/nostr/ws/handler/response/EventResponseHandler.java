
package nostr.ws.handler.response;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.java.Log;
import nostr.base.Command;
import nostr.util.NostrException;
import nostr.ws.handler.IClientMessageService;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author squirrel
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Log
public class EventResponseHandler extends BaseResponseHandler {

    private final String message;
    private IClientMessageService clientMessageService;
    private LocalDateTime now;

    private final Gson gson = new Gson();

    public EventResponseHandler(String message, IClientMessageService clientMessageService,LocalDateTime now) {
        super(Command.EVENT);
        this.message = message;
        this.clientMessageService = clientMessageService;;
        this.now = now;
    }

    @Override
    public void process() throws NostrException {
        if(Objects.nonNull(clientMessageService)) {
            clientMessageService.dealEvent(message, "EVENT", now);
        }
    }
}
