
package nostr.event.list;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import nostr.base.PublicKey;
import nostr.base.annotation.JsonList;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author squirrel
 */
@Builder
@Log
@JsonList
public class PublicKeyList extends nostr.event.list.BaseList<PublicKey> {

    public PublicKeyList() {
        this(new ArrayList<>());
    }

    private PublicKeyList(@NonNull List<PublicKey> list) {
        super(list);
    }
}
