package nostr.business.config;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Relay;
import nostr.business.IEventSigner;
import nostr.business.service.ClientMessageService;
import nostr.id.client.WritetClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class WriteFiltersConfig {
    public static WritetClient writetClient;

    public static nostr.event.list.FiltersList filtersList;

    public static List<Relay> relayList;

    public static IEventSigner eventSigner;

    @Resource
    private ClientMessageService service;

    public void newConnect() {
        try {
            if(Objects.isNull(filtersList)){
                throw new RuntimeException("WriteFiltersConfig filtersList is null");
            }
            if(CollectionUtils.isEmpty(relayList)){
                throw new RuntimeException("WriteFiltersConfig relayList is null");
            }
            if(Objects.isNull(eventSigner)){
                throw new RuntimeException("WriteFiltersConfig eventSigner is null");
            }
            writetClient = new WritetClient("nostr-Write", service, relayList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
