package nostr.event.impl;

import nostr.base.PublicKey;
import nostr.base.annotation.Event;
import nostr.event.list.TagList;
import nostr.event.Kind;

/**
 *
 * @author squirrel
 */
@Event(name = "Encrypted Direct Message", nip = 4)
public class DirectMessageEvent extends GenericEvent {

    public DirectMessageEvent(PublicKey sender, TagList tags, String content) {
        super(sender, Kind.ENCRYPTED_DIRECT_MESSAGE, tags, content);
    }
}
