package nostr.business;

import nostr.business.mapper.entity.ClientMessagePO;

public interface IEventCheck {
    public boolean check(ClientMessagePO clientMessagePO);
}
