package nostr.event.marshaller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;
import nostr.base.*;
import nostr.event.list.TagList;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericMessage;
import nostr.event.marshaller.impl.*;
import nostr.util.NostrException;

import java.lang.reflect.Field;

/**
 *
 * @author squirrel
 */
@AllArgsConstructor
@Data
@Log
public abstract class BaseElementMarshaller implements IMarshaller {

    private final IElement element;
    private final Relay relay;
    private boolean escape;

    public BaseElementMarshaller(IElement element, Relay relay) {
        this(element, relay, false);
    }

    protected boolean nipFieldSupport(Field field) {
        
        if (relay == null) {
            return true;
        }

        return NipUtil.checkSupport(relay, field);
    }

    @Builder
    @AllArgsConstructor
    @Data
    public static class Factory {

        private final IElement element;

        public IMarshaller create(Relay relay, boolean escape) throws NostrException {
            if (element instanceof IEvent iEvent) {
                return new EventMarshaller(iEvent, relay, escape);
            } else if (element instanceof ITag iTag) {
                return new TagMarshaller(iTag, relay, escape);
            } else if (element instanceof GenericMessage genericMessage) {
                return new MessageMarshaller(genericMessage, relay, escape);
            } else if (element instanceof TagList tagList) {
                return new TagListMarshaller(tagList, relay, escape);
            } else if (element instanceof INostrList iNostrList) {
                return new BaseListMarhsaller(iNostrList, relay, escape) {
                };
            } else if (element instanceof Filters filters) {
                return new FiltersMarshaller(filters, relay, escape);
            } else {
                throw new NostrException("Invalid Element type");
            }
        }
    }
}
