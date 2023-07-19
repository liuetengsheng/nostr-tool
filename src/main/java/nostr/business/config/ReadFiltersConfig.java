package nostr.business.config;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Relay;
import nostr.business.IEventCheck;
import nostr.business.IEventSigner;
import nostr.business.service.ClientMessageService;
import nostr.event.impl.GenericMessage;
import nostr.event.message.CloseMessage;
import nostr.event.message.ReqMessage;
import nostr.id.client.ReadClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class ReadFiltersConfig {
    public static ReadClient readClient;

    public static nostr.event.list.FiltersList filtersList;

    public static List<Relay> relayList;

    @Resource
    private ClientMessageService service;

    public static IEventSigner eventSigner;

    public static IEventCheck eventCheck;

    private static Map<String, String> subIds = new HashMap<>();

    public void newConnect(String subId){
        try {
            if(Objects.isNull(filtersList)){
                throw new RuntimeException("ReadFiltersConfig filtersList is null");
            }
            if(CollectionUtils.isEmpty(relayList)){
                throw new RuntimeException("ReadFiltersConfig relayList is null");
            }
            if(Objects.isNull(eventSigner)){
                throw new RuntimeException("ReadFiltersConfig eventSigner is null");
            }
            readClient = new ReadClient("nostr-Read", service, relayList);

            addEventReq(subId, filtersList);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void Reconnection(){
        try {
            // 查询最后更新时间
//            long sysLastTime = service.getSysLastTime();

            subIds.forEach((k, v) ->  addEventReq(k, filtersList));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void addEventReq(String subId, nostr.event.list.FiltersList filters){
        try {
            if(Objects.isNull(readClient)){
                throw new RuntimeException("ReadFiltersConfig readClient is null");
            }
            String sub = subIds.get(subId);
            if (StringUtils.isEmpty(sub)){
                sub = subId + System.currentTimeMillis();
                subIds.put(subId,sub);
            }

            GenericMessage message = new ReqMessage(subId, filters);
            readClient.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void closeEventReq(String subId) {
        String sub = subIds.get(subId);
        if (StringUtils.isBlank(sub)) {
            log.error("subId is empty");
            return;
        }

        GenericMessage message = new CloseMessage(subId);
        readClient.send(message);
    }

}
