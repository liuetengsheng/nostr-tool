package nostr.id.client;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import nostr.base.BaseConfiguration;
import nostr.base.PublicKey;
import nostr.base.Relay;
import nostr.event.impl.GenericMessage;
import nostr.json.unmarshaller.impl.JsonObjectUnmarshaller;
import nostr.types.values.IValue;
import nostr.types.values.impl.ArrayValue;
import nostr.types.values.impl.NumberValue;
import nostr.types.values.impl.ObjectValue;
import nostr.util.NostrUtil;
import nostr.ws.Connection;
import nostr.ws.handler.IClientMessageService;
import nostr.ws.handler.request.RequestHandler;

import java.io.IOException;
import java.util.*;

/**
 *
 * @author squirrel
 */
@Data
@Slf4j
@ToString
public class Client {

    @ToString.Exclude
    private final Set<Relay> relays;

    private final String name;

    private IClientMessageService service;

    private String pubKey;

    private Map<String, Connection> connectionMap = new HashMap<>();

    // 不需要发送的relay节点
    private final static List<String> IGNORE_RELAYS = new ArrayList<>();
    static {
        IGNORE_RELAYS.add("wss://arnostr.permadao.io");
    }

    public Client(@NonNull String name, String relayConfFile, IClientMessageService service,String pubKey) throws IOException {
        this.relays = new HashSet<>();
        this.name = name;
        this.service = service;
        this.pubKey = pubKey;
        this.init(relayConfFile);
    }

    public Client(@NonNull String name, IClientMessageService service,String pubKey, @NonNull List<Relay> relayList) throws IOException {
        this.relays = new HashSet<>();
        this.name = name;
        this.service = service;
        this.pubKey = pubKey;
        this.init(relayList);
    }

    public Client(@NonNull String name, IClientMessageService service, @NonNull List<Relay> relayList) throws IOException {
        this.relays = new HashSet<>();
        this.name = name;
        this.service = service;
        this.init(relayList);
    }

    public Client(@NonNull String name, IClientMessageService service,String pubKey) throws IOException {
        this(name, "/relays.properties",service,pubKey);
    }

    public Client(@NonNull String name) throws IOException {
        this(name, "/relays.properties",null,null);
    }


    /**
     * 字符串消息指定relay专用接口
     * @param message event json 消息
     * @param relay 未被发送的relay 节点
     */
    public void sendMsgJson(@NonNull String message,Integer nip,String relay) {
        Optional<Relay> first = relays.parallelStream()
                .filter(r -> {
                            if ((nip == 1 || nip == 4 || r.getSupportedNips().contains(nip)) && r.getUri().equals(relay)) {
                                return true;
                            } else {
                                log.info("Client.sene Relay {} {} not support nip {},{}", r.getUri(), r.getSupportedNips(), nip,relay);
                                return false;
                            }
                        }
                ).findFirst();

        try {
            if (first.isPresent()){
                Relay r = first.get();
                if(connectionMap.get(r.getUri()) != null){
                    RequestHandler rh = RequestHandler.builder().connection(connectionMap.get(r.getUri())).messageJson(message).build();
                    rh.process();
                }else {
                    RequestHandler rh = RequestHandler.builder().connection(new Connection(r, service)).messageJson(message).build();
                    connectionMap.put(r.getUri(), new Connection(r,service));
                    rh.process();
                }
            }
        } catch (Exception e) {
            log.error("发送消息异常：", e);
        }
    }

    /**
     * 字符串消息不知道节点专用接口
     * @param message event json 消息
     */
    public void sendMsgJson(@NonNull String message,Integer nip) {
        relays.parallelStream()
            .filter(r ->{
                        if(nip == 1 ||nip == 4 || r.getSupportedNips().contains(nip)){
                            return true;
                        }else{
                            log.info("Client.sene Relay {} {} not support nip {}",r.getUri(), r.getSupportedNips(), nip);
                            return false;
                        }
                    }
            )
            .forEach(r -> {
                    try {
                        if (!IGNORE_RELAYS.contains(r.getUri())) {
                            if(connectionMap.get(r.getUri()) != null){
                                RequestHandler rh = RequestHandler.builder().connection(connectionMap.get(r.getUri())).messageJson(message).build();
                                rh.process();
                            }else {
                                RequestHandler rh = RequestHandler.builder().connection(new Connection(r, service)).messageJson(message).build();
                                connectionMap.put(r.getUri(), new Connection(r,service));
                                rh.process();
                            }
                            log.info("Client {} sending message to: {}", r.getUri(),message);
                        }
                    } catch (Exception ex) {
                        try {
                            connectionMap.remove(r.getUri());
                            RequestHandler rh = RequestHandler.builder().connection(new Connection(r, service)).messageJson(message).build();
                            connectionMap.put(r.getUri(), new Connection(r,service));
                            rh.process();
                        } catch (Exception e) {
                            log.error("发送消息异常", ex);
                        }
                    }
        });

    }

    public void send(@NonNull GenericMessage message,String relay) {
        relays.parallelStream()
                .filter(r ->{
                            if(message.getNip()==4 || r.getSupportedNips().contains(message.getNip())){
                                return true;
                            }else{
                                log.info("Client.sene Relay {} {} not support nip {},{}",r.getUri(), r.getSupportedNips(), message.getNip(),relay);
                                return false;
                            }
                        }
                )
                .forEach(r -> {
                    try {
                        if (Objects.isNull(relay)){
                            // 该节点不发送普通消息 只针对Graph消息发送
                            if (!IGNORE_RELAYS.contains(r.getUri())) {
                                sendTest(r, message);
                            }
                        }else {
                            if (r.getUri().equals(relay)) {
                                sendTest(r, message);
                            }
                        }
                    } catch (Exception ex) {
                        connectionMap.remove(r.getUri());
                        try {
                            sendTest(r, message);
                        } catch (Exception e) {
                            log.error("重试发送消息异常", e);
                        }
                        log.error("发送消息异常", ex);
                    }
                });
    }

    public void send(@NonNull GenericMessage message) {
        this.send(message,null);
    }

    private void sendTest(Relay r, GenericMessage message) throws Exception {
        if(connectionMap.get(r.getUri()) == null){
            connectionMap.put(r.getUri(), new Connection(r,service));
        }
        var rh = RequestHandler.builder().connection(connectionMap.get(r.getUri())).message(message).build();
        log.info("Client {} sending message to {}", r.getUri(),message.toString());
        rh.process();
    }

    private void addRelay(@NonNull Relay relay) {
        this.relays.add(relay);
        updateRelayInformation(relay);
        log.info( "Added relay {}", relay);
    }

    private void init(String file) throws IOException {
        List<Relay> relayList = new RelayConfiguration(file).getRelays();
        relayList.forEach(this::addRelay);
    }

    private void init( List<Relay> relayList) throws IOException {
        relayList.forEach(this::addRelay);
    }

    public void updateRelayInformation(@NonNull Relay relay) {
        try {
            var connection = new Connection(relay,service);
            String strInfo = connection.getRelayInformation();
            log.info("Relay information: {}", strInfo);
            connectionMap.put(relay.getUri(), connection);
            ObjectValue info = new JsonObjectUnmarshaller(strInfo).unmarshall();

            if (((ObjectValue) info).get("\"contact\"").isPresent()) {
                final IValue contact = ((ObjectValue) info).get("\"contact\"").get();
                var strContact = contact.toString();
                relay.setContact(strContact);
            }

            if (((ObjectValue) info).get("\"description\"").isPresent()) {
                final IValue desc = ((ObjectValue) info).get("\"description\"").get();
                var strDesc = desc.toString();
                relay.setDescription(strDesc);
            }

            if (((ObjectValue) info).get("\"name\"").isPresent()) {
                final IValue relayName = ((ObjectValue) info).get("\"name\"").get();
                var strRelayName = relayName.toString();
                relay.setName(strRelayName);
            }

            if (((ObjectValue) info).get("\"software\"").isPresent()) {
                final IValue software = ((ObjectValue) info).get("\"software\"").get();
                var strSoftware = software.toString();
                relay.setSoftware(strSoftware);
            }

            if (((ObjectValue) info).get("\"version\"").isPresent()) {
                final IValue version = ((ObjectValue) info).get("\"version\"").get();
                var strVersion = version.toString();
                relay.setVersion(strVersion);
            }

            if (((ObjectValue) info).get("\"supported_nips\"").isPresent()) {
                List<Integer> snipList = new ArrayList<>();
                ArrayValue snips = (ArrayValue) ((ObjectValue) info).get("\"supported_nips\"").get();
                int len = snips.length();
                for (int i = 0; i < len; i++) {
                    snipList.add(((NumberValue) snips.get(i).get()).intValue().get());
                }
                relay.setSupportedNips(snipList);
            }

            if (((ObjectValue) info).get("\"pubkey\"").isPresent()) {
                final IValue pubKey = ((ObjectValue) info).get("\"pubkey\"").get();
                var strPubKey = pubKey.toString();
                if(strPubKey.length() >= 65){
                    relay.setPubKey(new PublicKey(NostrUtil.hexToBytes(strPubKey)));
                }
            }
        } catch (Exception ex) {
            log.error("relay={} exception:", relay.getUri(), ex);
            ex.printStackTrace();
        }
    }

    static class RelayConfiguration extends BaseConfiguration {

        RelayConfiguration() throws IOException {
            this("/relays.properties");
        }

        RelayConfiguration(String file) throws IOException {
            super(file);
        }

        List<Relay> getRelays() {
            Set<Object> relays = this.properties.keySet();
            List<Relay> result = new ArrayList<>();
            relays.forEach(r -> {
                Relay relay = Relay.builder().name(r.toString()).uri(this.getProperty(r.toString())).build();
                result.add(relay);
            });
            return result;
        }
    }

    public void setService(IClientMessageService service){
        this.service = service;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }
}
