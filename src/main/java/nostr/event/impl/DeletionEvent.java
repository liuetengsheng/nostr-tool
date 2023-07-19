
package nostr.event.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nostr.base.PublicKey;
import nostr.base.annotation.Event;
import nostr.event.list.TagList;
import nostr.event.Kind;

/**
 *
 * @author squirrel
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Event(name = "Event Deletion", nip = 9)
public class DeletionEvent extends GenericEvent {

    public DeletionEvent(PublicKey pubKey, TagList tagList, String content) {        
        super(pubKey, Kind.DELETION, tagList, content);        
    }

    public DeletionEvent(PublicKey pubKey, TagList tagList) {        
        this(pubKey, tagList, "Deletion request");
    }
}
