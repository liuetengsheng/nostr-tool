package nostr.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nostr.util.NostrException;
import nostr.ws.handler.CloseHandler;
import nostr.ws.handler.ConnectHandler;
import nostr.ws.handler.ErrorHandler;
import nostr.ws.handler.IClientMessageService;
import nostr.ws.handler.response.*;
import nostr.ws.handler.response.OkResponseHandler.Reason;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author squirrel
 */
@WebSocket(idleTimeout = Integer.MAX_VALUE,maxTextMessageSize = 128 * 1024, maxBinaryMessageSize = 128 * 1024)
@NoArgsConstructor
@AllArgsConstructor
@Component
@Scope("ClientListenerEndPoint")
@Slf4j
public class ClientListenerEndPoint {

    private IClientMessageService clientMessageService;

    @OnWebSocketConnect
    public void onConnect(Session session) {
        log.warn("onConnect");

//        session.setMaxTextMessageSize(16 * 1024);

        ConnectHandler.builder().build().process();
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        log.info("onClose {}, {}", statusCode, reason);

        CloseHandler.builder().reason(reason).statusCode(statusCode).build().process();

        disposeResources();
    }

    @OnWebSocketError
    public void onError(Throwable cause) {
        log.error("onError");

        log.error( "An error has occurred: {}", cause.getMessage());

        ErrorHandler.builder().cause(cause).build().process();

        disposeResources();
    }

    @OnWebSocketMessage
    public void onTextMessage(Session session, String message) throws IOException, NostrException {
        LocalDateTime now = LocalDateTime.now();
        log.info("{}==============onTextMessage: {},relay:{}",System.currentTimeMillis(), message,session.getRemoteAddress().toString());

        if ("close".equalsIgnoreCase(message)) {
            session.close(StatusCode.NORMAL, "bye");
            return;
        }

        Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
        List<Object> msgList = gson.fromJson(message, ArrayList.class);

        final String command = msgList.get(0).toString();//jsonArr).get(0).toString();
        String msg;
        BaseResponseHandler responseHandler = null;


        switch (command) {
            case "EOSE" -> {
                msg = msgList.get(1).toString();
                responseHandler = new EoseResponseHandler(msg);
            }
            case "OK" -> {
                String eventId = msgList.get(1).toString();// (jsonArr).get(1).toString();
                boolean result = (Boolean) msgList.get(2); //Boolean.parseBoolean(msgList.get(2).toString());//Boolean.parseBoolean((jsonArr).get(2).toString());
                msg = msgList.get(3).toString();//(jsonArr).get(3).toString();
                final int colonIndex = msg.indexOf(":");
                Reason reason;
                if (colonIndex == -1) {
                    reason = Reason.UNDEFINED;
                } else {
                    reason = Reason.fromCode(msg.substring(0, colonIndex)).orElseThrow(RuntimeException::new);
                }
                responseHandler = new OkResponseHandler(eventId, result, reason, message,session.getRemoteAddress().toString(),clientMessageService);
            }
            case "NOTICE" -> {
                msg = msgList.get(1).toString();//(jsonArr).get(1).toString();
                responseHandler = new NoticeResponseHandler(msg);
            }
            case "EVENT" -> {
                responseHandler = new EventResponseHandler(message,clientMessageService,now);
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
        log.info("onBinaryMessage");

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
        log.info("disposeResources");
    }

    private void savePNGImage(byte[] payload, int offset, int length) {
        log.info("savePNGImage");
    }
}
