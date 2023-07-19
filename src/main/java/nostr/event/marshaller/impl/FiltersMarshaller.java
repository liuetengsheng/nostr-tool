package nostr.event.marshaller.impl;

import nostr.base.Relay;
import nostr.event.list.GenericTagQueryList;
import nostr.event.impl.Filters;
import nostr.util.NostrException;

import java.lang.reflect.Field;
import java.util.Map;

/**
 *
 * @author squirrel
 */
public class FiltersMarshaller extends EventMarshaller {

    public FiltersMarshaller(Filters filters, Relay relay) {
        this(filters, relay, false);
    }

    public FiltersMarshaller(Filters filters, Relay relay, boolean escape) {
        super(filters, relay, escape);
    }

    @Override
    protected void toJson(Field field, StringBuilder result, Map<Field, Object> keysMap, Relay relay, int i) throws NostrException {
        if (field.getType().equals(GenericTagQueryList.class)) {

            final GenericTagQueryList gtql = (GenericTagQueryList) keysMap.get(field);

            result.append(new GenericTagQueryListMarshaller(gtql, relay, isEscape()).marshall());
            
            if (i < keysMap.size()) {
                result.append(",");
            }

        } else {
            super.toJson(field, result, keysMap, relay, i);
        }
    }
}
