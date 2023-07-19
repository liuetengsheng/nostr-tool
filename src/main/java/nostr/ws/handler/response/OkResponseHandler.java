package nostr.ws.handler.response;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.java.Log;
import nostr.base.Command;
import nostr.ws.handler.IClientMessageService;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author squirrel
 */
//@Builder
//@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@Log
public class OkResponseHandler extends BaseResponseHandler {

    public enum Reason {
        UNDEFINED(""),
        DUPLICATE("duplicate"),
        BLOCKED("blocked"),
        INVALID("invalid"),
        RATE_LIMITED("rate-limited"),
        ERROR("error"),
        POW("pow");

        private final String code;

        Reason(String code) {
            this.code = code;
        }

        public static Optional<Reason> fromCode(String code) {
            return Arrays.stream(values())
                    .filter(reason -> reason.code.equalsIgnoreCase(code))
                    .findFirst();
        }
    }
    
    private final String eventId;
    private final boolean result;
    private final Reason reason;
    private final String message;

    private IClientMessageService clientMessageService;
    private final String relay;

    public OkResponseHandler(String eventId, boolean result, Reason reason, String message, String relay,IClientMessageService clientMessageService) {
        super(Command.OK);
        this.eventId = eventId;
        this.result = result;
        this.reason = reason;
        this.message = message;
        this.relay = relay;
        this.clientMessageService = clientMessageService;
    }

    @Override
    public void process() {
//        Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
//        List<Object> msgList = gson.fromJson(message, ArrayList.class);
//        String eventId = msgList.get(1).toString();// (jsonArr).get(1).toString();
//        boolean result = (Boolean) msgList.get(2); //Boolean.parseBoolean(msgList.get(2).toString());//Boolean.parseBoolean((jsonArr).get(2).toString());
//        String msg = msgList.get(3).toString();//(jsonArr).get(3).toString();
//        final int colonIndex = msg.indexOf(":");
//        Reason reason;
//        String reasonMessage = "";
//        if (colonIndex == -1) {
//            reason = Reason.UNDEFINED;
//            reasonMessage = msg;
//        } else {
//            reason = Reason.fromCode(msg.substring(0, colonIndex)).orElseThrow(RuntimeException::new);
//            reasonMessage = msg.substring(colonIndex + 1);
//        }
//        log.log(Level.INFO, "{0}", this);
        // send ma
        if(Objects.nonNull(clientMessageService)) {
//            ClientMessageService clientMessageService2 = (ClientMessageService) clientMessageService;
            clientMessageService.dealOk(message ,relay);
        }
    }
}
