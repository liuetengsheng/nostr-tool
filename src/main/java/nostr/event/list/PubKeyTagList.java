package nostr.event.list;

import lombok.extern.java.Log;
import nostr.base.annotation.JsonList;
import nostr.event.tag.PubKeyTag;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author squirrel
 */
@Log
@JsonList
public class PubKeyTagList extends nostr.event.list.TagList {

    public PubKeyTagList() {
        this(new ArrayList<>());
    }

    private PubKeyTagList(List<PubKeyTag> list) {
        super(list);
    }
}
