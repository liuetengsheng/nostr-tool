package nostr.id;

import lombok.NonNull;
import nostr.base.ISignable;
import nostr.base.Profile;
import nostr.base.Signature;
import nostr.event.impl.DirectMessageEvent;
import nostr.util.NostrException;

/**
 * @Description 请描述类的业务用途
 * @Author young
 * @Date 2023/2/28 16:26
 */
public interface IAbstractIdentity {

    Profile getProfile();

    void encryptDirectMessage(@NonNull DirectMessageEvent dmEvent) throws NostrException;

    Signature sign(@NonNull ISignable signable) throws NostrException;
}
