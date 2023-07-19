
package nostr.event.list;

import lombok.NonNull;
import lombok.extern.java.Log;
import nostr.base.ITag;
import nostr.base.annotation.JsonList;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author squirrel
 */
@SuppressWarnings("rawtypes")
@Log
@JsonList
public class TagList extends nostr.event.list.BaseList {

    @SuppressWarnings("unchecked")
    public TagList() {
        this(new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    protected TagList(@NonNull List<? extends ITag> list) {
        super(list);
    }

}
