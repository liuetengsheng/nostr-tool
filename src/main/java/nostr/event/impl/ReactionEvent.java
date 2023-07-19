package nostr.event.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;
import nostr.base.PublicKey;
import nostr.base.annotation.Event;
import nostr.event.list.TagList;
import nostr.event.Kind;
import nostr.event.Reaction;

/**
 *
 * @author squirrel
 */
@Data
@Log
@EqualsAndHashCode(callSuper = false)
@Event(name = "Reactions", nip = 25)
public class ReactionEvent extends GenericEvent {

    private final GenericEvent sourceEvent;

    public ReactionEvent(PublicKey pubKey, TagList tags, Reaction content, GenericEvent sourceEvent) {
        super(pubKey, Kind.REACTION, tags, content.getEmoji());
        this.sourceEvent = sourceEvent;
    }

}
