package nostr.business;

import nostr.business.mapper.entity.ClientMessagePO;
import nostr.event.impl.DirectMessageEvent;
import nostr.event.impl.GenericEvent;
import nostr.id.Identity;
import nostr.util.NostrException;

import java.util.Map;

public interface IEventSigner {
    String decode(ClientMessagePO clientMessagePO) throws NostrException;

    void encryptDirectMessage(DirectMessageEvent event) throws NostrException;

    void sign(GenericEvent event) throws NostrException;

    Map<String, Identity> addIdentity(String pubKey, String priKey);
}
