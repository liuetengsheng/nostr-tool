package nostr.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import nostr.ws.handler.CloseHandler;
import nostr.ws.handler.ConnectHandler;
import nostr.ws.handler.ErrorHandler;
import nostr.ws.handler.response.BaseResponseHandler;
import nostr.ws.handler.response.EoseResponseHandler;
import nostr.ws.handler.response.EventResponseHandler;
import nostr.ws.handler.response.NoticeResponseHandler;
import nostr.ws.handler.response.OkResponseHandler;
import nostr.json.unmarshaller.impl.JsonArrayUnmarshaller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import nostr.ws.handler.response.OkResponseHandler.Reason;
import nostr.types.values.impl.ArrayValue;
import nostr.util.NostrException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 *
 * @author squirrel
 */
@WebSocket(idleTimeout = Integer.MAX_VALUE)
@NoArgsConstructor
@Log
public class ClientListenerEndPoint {

    @OnWebSocketConnect
    public void onConnect(Session session) {
        log.fine("onConnect");

        session.setMaxTextMessageSize(16 * 1024);

        ConnectHandler.builder().build().process();
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        log.log(Level.FINE, "onClose {0}, {1}", new Object[]{statusCode, reason});

        CloseHandler.builder().reason(reason).statusCode(statusCode).build().process();

        disposeResources();
    }

    @OnWebSocketError
    public void onError(Throwable cause) {
        log.fine("onError");

        log.log(Level.SEVERE, "An error has occurred: {}", cause.getMessage());

        ErrorHandler.builder().cause(cause).build().process();

        disposeResources();
    }

    @OnWebSocketMessage
    public void onTextMessage(Session session, String message) throws IOException, NostrException {
        log.log(Level.INFO, "==============onTextMessage: Message: {0}", message);

        if ("close".equalsIgnoreCase(message)) {
            session.close(StatusCode.NORMAL, "bye");
            return;
        }

        Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
//        Type listType = new TypeToken<List<String>>(){}.getType();
        List<Object> msgList = gson.fromJson(message, ArrayList.class);

        ArrayValue jsonArr = new JsonArrayUnmarshaller(message).unmarshall();
        final String command = msgList.get(0).toString();//jsonArr).get(0).toString();
        String msg;
        BaseResponseHandler responseHandler = null;
        log.log(Level.INFO, "==============command: Message: {0}", command);

        switch (command) {
            case "EOSE" -> {
//                msg = gson.toJson(msgList.get(1));
                msg = msgList.get(1).toString();//(jsonArr).get(1).toString();
                responseHandler = new EoseResponseHandler(msg);
            }
            case "OK" -> {
                String eventId = msgList.get(1).toString();// (jsonArr).get(1).toString();
                boolean result = (Boolean) msgList.get(2); //Boolean.parseBoolean(msgList.get(2).toString());//Boolean.parseBoolean((jsonArr).get(2).toString());
                msg = msgList.get(3).toString();//(jsonArr).get(3).toString();
                final int colonIndex = msg.indexOf(":");
                Reason reason;
                String reasonMessage = "";
                if (colonIndex == -1) {
                    reason = Reason.UNDEFINED;
                    reasonMessage = msg;
                } else {
                    reason = Reason.fromCode(msg.substring(0, colonIndex)).orElseThrow(RuntimeException::new);
                    reasonMessage = msg.substring(colonIndex + 1);
                }
                responseHandler = new OkResponseHandler(eventId, result, reason, reasonMessage);
            }
            case "NOTICE" -> {
                msg = msgList.get(1).toString();//(jsonArr).get(1).toString();
                responseHandler = new NoticeResponseHandler(msg);
            }
            case "EVENT" -> {
                String subId = msgList.get(1).toString();
                String jsonEvent = gson.toJson(msgList.get(2));
                responseHandler = new EventResponseHandler(subId, jsonEvent);
            }
            default -> {
            }
        }

        if (responseHandler != null) {
            responseHandler.process();
        }
    }

    @OnWebSocketMessage
    public void onBinaryMessage(byte[] payload, int offset, int length) {
        log.fine("onBinaryMessage");

        // Save only PNG images.
        byte[] pngBytes = new byte[]{(byte) 0x89, 'P', 'N', 'G'};
        for (int i = 0; i < pngBytes.length; ++i) {
            if (pngBytes[i] != payload[offset + i]) {
                return;
            }
        }
        savePNGImage(payload, offset, length);
    }

    private void disposeResources() {
        log.log(Level.FINE, "disposeResources");
    }

    private void savePNGImage(byte[] payload, int offset, int length) {
        log.log(Level.FINE, "savePNGImage");
    }
}
