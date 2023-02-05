package nostr.id;

import nostr.base.BaseConfiguration;
import nostr.util.NostrUtil;
import nostr.base.Relay;
import nostr.ws.Connection;
import nostr.ws.handler.request.RequestHandler;
import nostr.json.unmarshaller.impl.JsonObjectUnmarshaller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.java.Log;
import nostr.base.PublicKey;
import nostr.event.impl.GenericMessage;
import nostr.types.values.IValue;
import nostr.types.values.impl.ArrayValue;
import nostr.types.values.impl.NumberValue;
import nostr.types.values.impl.ObjectValue;

/**
 *
 * @author squirrel
 */
@Log
@Data
@ToString
public class Client {

    @ToString.Exclude
    private final Set<Relay> relays;

    private final String name;

    @ToString.Include
    private final Identity identity;

    public Client(@NonNull String name, String relayConfFile, @NonNull Identity identity) throws IOException {
        this.relays = new HashSet<>();
        this.name = name;
        this.identity = identity;

        this.init(relayConfFile);
    }

    public Client(@NonNull String name, @NonNull Identity identity) throws IOException {
        this(name, "/relays.properties", identity);
    }

    public void send(@NonNull GenericMessage message) {

        relays.parallelStream()
                .filter(r ->{
                            if(message.getNip()==4 || r.getSupportedNips().contains(message.getNip())){
//                                log.log(Level.WARNING, "Relay {0} not support nip {1}", new Object[]{r.getUri(), message.getNip()});
                                return true;
                            }else{
                                log.log(Level.WARNING, "Client.sene Relay {0} {1} not support nip {2}", new Object[]{r.getUri(), r.getSupportedNips(), message.getNip()});
                                return false;
                            }
                        }
                )
                .forEach(r -> {
                    try {
                        var rh = RequestHandler.builder().connection(new Connection(r)).message(message).build();
                        log.log(Level.INFO, "Client {0} sending message to {1}", new Object[]{this, r});
                        rh.process();
                    } catch (Exception ex) {
                        log.log(Level.SEVERE, null, ex);
                    }
                });
    }

    private void addRelay(@NonNull Relay relay) {
        this.relays.add(relay);
        updateRelayInformation(relay);
        log.log(Level.FINE, "Added relay {0}", relay);
    }

    private void init(String file) throws IOException {
        List<Relay> relayList = new RelayConfiguration(file).getRelays();
        relayList.stream().forEach(r -> this.addRelay(r));
    }

    private void updateRelayInformation(@NonNull Relay relay) {
        try {
            var connection = new Connection(relay);
            String strInfo = connection.getRelayInformation();
            log.log(Level.INFO, "Relay information: {0}", strInfo);
            ObjectValue info = new JsonObjectUnmarshaller(strInfo).unmarshall();

            if (((ObjectValue) info).get("\"contact\"").isPresent()) {
                final IValue contact = ((ObjectValue) info).get("\"contact\"").get();
                var strContact = contact == null ? "" : contact.toString();
                relay.setContact(strContact);
            }

            if (((ObjectValue) info).get("\"description\"").isPresent()) {
                final IValue desc = ((ObjectValue) info).get("\"description\"").get();
                var strDesc = desc == null ? "" : desc.toString();
                relay.setDescription(strDesc);
            }

            if (((ObjectValue) info).get("\"name\"").isPresent()) {
                final IValue relayName = ((ObjectValue) info).get("\"name\"").get();
                var strRelayName = relayName == null ? "" : relayName.toString();
                relay.setName(strRelayName);
            }

            if (((ObjectValue) info).get("\"software\"").isPresent()) {
                final IValue software = ((ObjectValue) info).get("\"software\"").get();
                var strSoftware = software == null ? "" : software.toString();
                relay.setSoftware(strSoftware);
            }

            if (((ObjectValue) info).get("\"version\"").isPresent()) {
                final IValue version = ((ObjectValue) info).get("\"version\"").get();
                var strVersion = version == null ? "" : version.toString();
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
                var strPubKey = pubKey == null  ? "" : pubKey.toString();
                if(strPubKey.length() >= 65){
                    relay.setPubKey(new PublicKey(NostrUtil.hexToBytes(strPubKey)));
                }
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, "relay={0} exception=${1}", new Object[]{relay.getUri(), ex});
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

            //for (Object r : relays) 
            relays.stream().forEach(r -> {
                Relay relay = Relay.builder().name(r.toString()).uri(this.getProperty(r.toString())).build();
                result.add(relay);
            });
            return result;
        }
    }
}
