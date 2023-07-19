package nostr.id.client;

import lombok.NonNull;
import nostr.base.Relay;
import nostr.ws.handler.IClientMessageService;

import java.io.IOException;
import java.util.List;

public class ReadClient extends Client{

    public ReadClient(@NonNull String name, IClientMessageService service, @NonNull List<Relay> relayList) throws IOException {
        super(name, service, relayList);
    }

}
