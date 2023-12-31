
package nostr.event.list;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.java.Log;
import nostr.base.INostrList;
import nostr.base.annotation.JsonList;

import java.util.List;

/**
 *
 * @author squirrel
 * @param <T>
 */
@AllArgsConstructor
@Data
@Log
@JsonList
public abstract class BaseList<T> implements INostrList<T> {

    @NonNull
    private final List<T> list;

    @Override
    public void add(@NonNull T elt) {
        if (!list.contains(elt)) {
            this.list.add(elt);
        }
    }

    @Override
    public void addAll(@NonNull INostrList<T> aList) {
        this.list.addAll(aList.getList());
    }

    @Override
    public int size() {
        return this.list.size();
    }
}
