package nostr.event.list;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import nostr.base.GenericTagQuery;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author squirrel
 */
@Builder
@Log
public class GenericTagQueryList extends BaseList<GenericTagQuery> {

    public GenericTagQueryList() {
        this(new ArrayList<>());
    }

    private GenericTagQueryList(@NonNull List<GenericTagQuery> list) {
        super(list);
    }

}
