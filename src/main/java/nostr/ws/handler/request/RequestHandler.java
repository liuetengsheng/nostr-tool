package nostr.ws.handler.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;
import nostr.base.IHandler;
import nostr.base.Relay;
import nostr.event.impl.GenericMessage;
import nostr.event.marshaller.impl.MessageMarshaller;
import nostr.util.NostrException;
import nostr.ws.Connection;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

/**
 *
 * @author squirrel
 */
@Builder
@Log
@Data
@AllArgsConstructor
public class RequestHandler implements IHandler {

    private final GenericMessage message;

    private final String messageJson;

    private final Connection connection;

    @Override
    public void process() throws NostrException {
        try {
            if (Objects.nonNull(messageJson)){
                sendMessageJson();
            }else {
                sendMessage();
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
            throw new NostrException(ex);
        }
    }


    private void sendMessageJson() throws IOException, NostrException {

        final Relay relay = connection.getRelay();

        final Session session = this.connection.getSession();
        if (session != null) {
            RemoteEndpoint remote = session.getRemote();

            log.log(Level.INFO, " ==========================Sending Message Str "+ messageJson);
            remote.sendString(messageJson);

            return;
        }
        throw new NostrException("Could not get a session");
    }


    private void sendMessage() throws IOException, NostrException {

        final Relay relay = connection.getRelay();

//        if (!relay.getSupportedNips().contains(message.getNip())) {
//            throw new UnsupportedNIPException(String.format("NIP-%d is not supported by relay %s. Supported NIPS: %s", new Object[]{message.getNip(), relay, relay.printSupportedNips()}));
//        }

        final Session session = this.connection.getSession();
        if (session != null) {
            RemoteEndpoint remote = session.getRemote();

//            log.log(Level.INFO, "Sending Message: {0}", message);

            final String msg = new MessageMarshaller(message, relay).marshall();
//            String newMsg = "[\"REQ\",\"subId1675490234998\",{\"limit\":10,\"kinds\":[1]}]";
            log.log(Level.INFO, " ==========================Sending Message Str "+ msg);

            remote.sendString(msg);

            return;
        }
        throw new NostrException("Could not get a session");
    }
}
