
package nostr.event.list;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import nostr.base.annotation.JsonList;
import nostr.event.impl.Filters;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author squirrel
 */
@Builder
@Log
@JsonList
public class FiltersList extends nostr.event.list.BaseList<Filters> {

    public FiltersList() {
        this(new ArrayList<>());
    }

    private FiltersList(@NonNull List<Filters> list) {
        super(list);
    }
}
