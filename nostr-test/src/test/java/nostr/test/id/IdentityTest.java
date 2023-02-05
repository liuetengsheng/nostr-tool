package nostr.test.id;

import nostr.base.PrivateKey;
import nostr.base.PublicKey;
import nostr.crypto.bech32.Bech32;
import nostr.event.tag.DelegationTag;
import nostr.event.impl.GenericEvent;
import nostr.id.Identity;
import nostr.test.EntityFactory;
import java.io.IOException;
import nostr.util.NostrException;
import nostr.util.NostrUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author squirrel
 */
public class IdentityTest {

    private final Identity identity;

    public IdentityTest() throws IOException, NostrException {
        this.identity = new Identity();
    }

    @Test
    public void testKey() {
        try {
            System.out.println("testKey");

            String privateBech32 = "nsec1xf9e3m2ujfa2pkl3mwm4kx6f4qnzvlugpy9y4tx8wudtyryp4n0qmfh6qz";
            PrivateKey privateKey = new PrivateKey(NostrUtil.hexToBytes(Bech32.fromBech32(privateBech32)));
            System.out.println("privateKey : "+privateKey.toString()+"=>"+privateKey.toBech32());

            String publicKeyBech32 = "npub13mfrwv69255fk06p92y08yd35vlfpuq6xd0ux8zppd9zhj4qfscqd6pkf7";
            PublicKey publicKey = new PublicKey(NostrUtil.hexToBytes(Bech32.fromBech32(publicKeyBech32)));
            System.out.println("publicKey : "+publicKey.toString()+"=>"+publicKey.toBech32());

            Assertions.assertNotNull(publicKey.toString());
        } catch (NostrException ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void testSignEvent() {
        try {
            System.out.println("testSignEvent");
            PublicKey publicKey = this.identity.getProfile().getPublicKey();
//            log.log(NostrUtil.hexToBytes("01739eae78ef308acb9e7a8a85f7d03484e0d338a7fae1ef2a8fa18e9b5915c5"));
            System.out.println("publicKey.toBech32():"+ publicKey.toBech32());
            System.out.println("publicKey.toString():"+ publicKey.toString());
            System.out.println("Bech32.fromBech32(publicKey.toBech32()):"+ Bech32.fromBech32(publicKey.toBech32()));

            String lukePubBech32 = Bech32.fromBech32("npub170cp6drukm2ugl7l72huvkemzmcwy2upcerncqtujktjja54wqfqzkn3hs");
            PublicKey lukePublicKey = new PublicKey(NostrUtil.hexToBytes(lukePubBech32));
            System.out.println(lukePublicKey.toBech32());
//            System.out.println(Bech32.toBech32(lukePublicKey.toString()));


            GenericEvent instance = EntityFactory.Events.createTextNoteEvent(publicKey);
            this.identity.sign(instance);
            Assertions.assertNotNull(instance.getSignature());
        } catch (NostrException ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void testSignDelegationTag() {
        try {
            System.out.println("testSignDelegationTag");
            PublicKey publicKey = this.identity.getProfile().getPublicKey();
            DelegationTag delegationTag = new DelegationTag(publicKey, null);
            this.identity.sign(delegationTag);
            Assertions.assertNotNull(delegationTag.getSignature());
        } catch (NostrException ex) {
            Assertions.fail(ex);
        }
    }
    
}
