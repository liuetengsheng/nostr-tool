package nostr.business.config;

import jakarta.annotation.Resource;
import nostr.base.Relay;
import nostr.business.IEventCheck;
import nostr.business.service.EventSigner;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Component
public class NostrConfig {
    @Resource
    private ReadFiltersConfig readFiltersConfig;

    @Resource
    private WriteFiltersConfig writeFiltersConfig;

    public void newConnect(nostr.event.list.FiltersList filtersList, String relays, @NonNull String subId, EventSigner eventSigner, IEventCheck eventCheck){
        ReadFiltersConfig.filtersList = filtersList;
        ReadFiltersConfig.relayList = intRelayList(relays);
        ReadFiltersConfig.eventSigner = eventSigner;
        ReadFiltersConfig.eventCheck = eventCheck;

        readFiltersConfig.newConnect(subId);

        WriteFiltersConfig.filtersList = filtersList;
        WriteFiltersConfig.relayList = intRelayList(relays);
        WriteFiltersConfig.eventSigner = eventSigner;

        writeFiltersConfig.newConnect();
    }

    private List<Relay> intRelayList( String relays){
        List<Relay> relayList = new ArrayList<>();
        String[] relayUri = relays.split(",");
        for(String uri : relayUri){
            Relay relay = Relay.builder().name(uri).uri(uri).build();
            relayList.add(relay);
        }
        return relayList;
    }
}
