
package nostr.ws.handler.response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nostr.base.Command;
import java.util.logging.Level;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.java.Log;
import nostr.json.parser.impl.JsonObjectParser;
import nostr.json.unmarshaller.impl.JsonObjectUnmarshaller;
import nostr.json.unmarshaller.impl.JsonStringUnmarshaller;
import nostr.types.values.IValue;
import nostr.types.values.impl.ObjectValue;
import nostr.types.values.impl.StringValue;
import nostr.util.NostrException;

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
    
    private final String subscriptionId;
    private final String jsonEvent;

    private final Gson gson = new Gson();

    public EventResponseHandler(String subscriptionId, String jsonEvent) {
        super(Command.EVENT);
        this.subscriptionId = subscriptionId;
        this.jsonEvent = jsonEvent;
    }

    @Override
    public void process() throws NostrException {
//        StringValue jsonObj = new JsonStringUnmarshaller(jsonEvent).unmarshall();

//        ObjectValue jsonStr = jsonObj.parse();

//        JsonObject obj = gson.fromJson(jsonEvent,JsonObject.class);
//        log.log(Level.INFO, "===========" + obj.get("kind"));
        JsonObject obj = gson.fromJson(jsonEvent, JsonObject.class);
        int kind = obj.get("kind").getAsInt();
        if(kind == 4){
            log.log(Level.INFO, "接收到加密信息======= {0}", this.jsonEvent);
        }else{
            log.log(Level.INFO, "接收到非加密信息======= {0}", this.jsonEvent);

        }
//        log.log(Level.INFO, "{0}", this);
    }
}
