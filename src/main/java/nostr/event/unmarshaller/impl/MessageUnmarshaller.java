package nostr.event.unmarshaller.impl;

import nostr.base.ElementAttribute;
import nostr.base.IEvent;
import nostr.event.list.FiltersList;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericMessage;
import nostr.event.message.*;
import nostr.event.unmarshaller.BaseElementUnmarshaller;
import nostr.json.unmarshaller.impl.JsonArrayUnmarshaller;
import nostr.types.values.impl.StringValue;

/**
 *
 * @author squirrel
 */
public class MessageUnmarshaller extends BaseElementUnmarshaller {

    public MessageUnmarshaller(String event) {
        this(event, false);
    }

    public MessageUnmarshaller(String event, boolean escape) {
        super(event, escape);
    }

    @Override
    public GenericMessage unmarshall() {

        var value = new JsonArrayUnmarshaller(this.getJson()).unmarshall();
        final String cmd = value.get(0).get().getValue().toString();
        GenericMessage msg;

        switch (cmd) {
            case "CLOSE" -> {
                String subId = value.get(1).get().getValue().toString();
                msg = new CloseMessage(subId);
            }
            case "EVENT" -> {
                IEvent event = new EventUnmarshaller(value.get(1).get().getValue().toString()).unmarshall();
                msg = new EventMessage((GenericEvent) event);
            }
            case "EOSE" -> {
                String subId = value.get(1).get().getValue().toString();
                msg = new EoseMessage(subId);
            }
            case "NOTICE" -> {
                String message = value.get(1).get().getValue().toString();
                msg = new NoticeMessage(message);
            }
            case "OK" -> {
                String eventId = value.get(1).get().getValue().toString();
                Boolean flag = (Boolean) value.get(2).get().getValue();
                String message = value.get(3).get().getValue().toString();
                msg = new OkMessage(eventId, flag, message);
            }
            case "REQ" -> {
                String subId = value.get(1).get().getValue().toString();
                FiltersList filtersList = new FiltersList();
                for (var i = 1; i < value.length(); i++) {
                    var filters = value.get(i).get();
                    filtersList.add((Filters) new FiltersUnmarshaller(filters.toString(), isEscape()).unmarshall());

                }
                msg = new ReqMessage(subId, filtersList);
            }
            default -> {
                msg = new GenericMessage(cmd);
                for (var i = 1; i < value.length(); i++) {
                    StringValue v = (StringValue) value.get(i).get();
                    msg.addAttribute(new ElementAttribute(v.getValue().toString()));
                }
            }
        }
        
        return msg;
    }

}
