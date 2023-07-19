/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nostr.event.tag;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nostr.base.Relay;
import nostr.base.annotation.Key;
import nostr.base.annotation.Tag;
import nostr.event.BaseTag;
import nostr.event.Marker;
import nostr.event.impl.GenericEvent;
import nostr.util.NostrException;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author squirrel
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@Tag(code = "e", name = "event")
public class EventTag extends BaseTag {

    @Key
    private GenericEvent relatedEvent;

    @Key
    private String recommendedRelayUrl;

    @Key(nip = 10)
    private Marker marker;

    public EventTag(GenericEvent relatedEvent) {
        this(relatedEvent, null);
    }

    private EventTag(GenericEvent relatedEvent, String recommendedRelayUrl, Marker marker) {
        this.recommendedRelayUrl = recommendedRelayUrl;
        this.relatedEvent = relatedEvent;
        this.marker = marker;
    }

    private EventTag(GenericEvent relatedEvent, String recommendedRelayUrl) {
        this.recommendedRelayUrl = recommendedRelayUrl;
        this.relatedEvent = relatedEvent;
        this.marker = this.relatedEvent == null ? Marker.ROOT : Marker.REPLY;
    }

    @Override
    public String printAttributes(Relay relay, boolean escape) throws NostrException {

        StringBuilder result = new StringBuilder();

        result.append(",");

        final String fieldValue = this.relatedEvent.getId();//super.getFieldValue(f);

        if (!escape) {
            result.append("\"");
        } else {
            result.append("\\\"");
        }

        result.append(fieldValue);

        if (!escape) {
            result.append("\"");
        } else {
            result.append("\\\"");
        }
        return result.toString();
    }

    private String getFieldValue(Field field) throws NostrException {
        try {
            Object f = new PropertyDescriptor(field.getName(), this.getClass()).getReadMethod().invoke(this);
            return f != null ? f.toString() : null;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                 IntrospectionException ex) {
            throw new NostrException(ex);
        }
    }
}
