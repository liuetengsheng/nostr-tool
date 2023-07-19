
package nostr.event.list;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import nostr.base.annotation.JsonList;
import nostr.event.impl.GenericEvent;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author squirrel
 */
@Builder
@Log
@JsonList
public class EventList extends BaseList<GenericEvent> {

    public EventList() {
        this(new ArrayList<>());
    }

    private EventList(@NonNull List<GenericEvent> list) {
        super(list);
    }    
}
