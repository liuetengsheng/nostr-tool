package nostr.event.unmarshaller.impl;

import nostr.base.ElementAttribute;
import nostr.base.IEvent;
import nostr.base.PublicKey;
import nostr.event.Kind;
import nostr.event.impl.DirectMessageEvent;
import nostr.event.impl.GenericTag;
import nostr.event.tag.PubKeyTag;
import nostr.event.unmarshaller.BaseElementUnmarshaller;
import nostr.json.unmarshaller.impl.JsonObjectUnmarshaller;
import nostr.util.NostrUtil;

/**
 *
 * @author squirrel
 */
public class DirectEventUnmarshaller extends BaseElementUnmarshaller {

    public DirectEventUnmarshaller(String event) {
        this(event, false);
    }

    public DirectEventUnmarshaller(String event, boolean escape) {
        super(event, escape);
    }

    @Override
    public IEvent unmarshall() {
        var value = new JsonObjectUnmarshaller(this.getJson()).unmarshall();

        // Public Key
        var strPubKey = value.get("\"pubkey\"").get().getValue().toString();
        var pubKey = new PublicKey(NostrUtil.hexToBytes(strPubKey));

        // Kind 
        var ikind = ((Number) value.get("\"kind\"").get().getValue()).intValue();
        Kind kind = Kind.valueOf(ikind);

        // TagList
        var strTagList = value.get("\"tags\"").get().toString();
        var tags = new TagListUnmarshaller(strTagList, isEscape()).unmarshall();

        if(tags.size() > 0){
            GenericTag genericTag = (GenericTag) tags.getList().get(0);
            // get the first attribute of the first tag
//            genericTag.getAttributes().get(0);
//            ElementAttribute[] array1 = genericTag.getAttributes().toArray();
            ElementAttribute[] array = genericTag.getAttributes().toArray(new ElementAttribute[0]);//.toArray(new String[0]);
            String firstValue = array[0].getValue().getValue().toString();
            PublicKey publicKey = new PublicKey(NostrUtil.hexToBytes(firstValue));
            PubKeyTag pubKeyTag = PubKeyTag.builder().publicKey(publicKey).build();
            tags.getList().set(0, pubKeyTag);
        }

        // Content 
        var content = value.get("\"content\"").get().getValue().toString();

        return new DirectMessageEvent(pubKey, tags, content);
    }

}
