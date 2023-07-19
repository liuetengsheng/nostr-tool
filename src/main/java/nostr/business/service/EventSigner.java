package nostr.business.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import nostr.base.PrivateKey;
import nostr.base.PublicKey;
import nostr.business.IEventSigner;
import nostr.business.mapper.entity.ClientMessagePO;
import nostr.event.impl.DirectMessageEvent;
import nostr.event.impl.GenericEvent;
import nostr.id.Identity;
import nostr.util.EncryptMessage;
import nostr.util.NostrException;
import nostr.util.NostrUtil;

import java.util.*;

public class EventSigner implements IEventSigner {
    private static Map<String, Identity> identityMap = new HashMap<>();

    public EventSigner(String pubKey, String priKey){
        PublicKey PubKey = new PublicKey(NostrUtil.hexToBytes(pubKey));
        PrivateKey PriKey = new PrivateKey(NostrUtil.hexToBytes(priKey));
        Identity identity = new Identity(PriKey, PubKey);
        identityMap.put(pubKey, identity);
    }

    @Override
    public Map<String, Identity> addIdentity(String pubKey, String priKey){
        PublicKey PubKey = new PublicKey(NostrUtil.hexToBytes(pubKey));
        PrivateKey PriKey = new PrivateKey(NostrUtil.hexToBytes(priKey));
        Identity identity = new Identity(PriKey, PubKey);
        identityMap.put(pubKey, identity);
        return identityMap;
    }

    @Override
    public String decode(ClientMessagePO clientMessagePO)throws NostrException {
        JSONObject jsonObject = JSON.parseObject(clientMessagePO.getContent());
        Integer kind = jsonObject.getInteger("kind");
        String content1 = jsonObject.getString("content");
        if (kind != 4){
            return content1;
        }

        List<String> pubList = new ArrayList<>();
        JSONArray tags = jsonObject.getJSONArray("tags");
        for (Object tag : tags) {
            JSONArray array = JSON.parseArray(tag.toString());
            if (array.getString(0).equals("p") ){
                pubList.add(array.getString(1));
            }
        }
        Identity identity = null;

        for(String pub : pubList){
            if(Objects.nonNull(identityMap.get(pub))){
                identity = identityMap.get(pub);
                break;
            }
        }

        if(Objects.isNull(identity)){
            throw new NostrException("decode() identity is null");
        }
        content1 = EncryptMessage.decodeMessage(identity.getPrivateKey().getRawData(),clientMessagePO.getPublicKey(),content1);
        return content1;
    }

    @Override
    public void encryptDirectMessage(DirectMessageEvent event) throws NostrException {
        Identity identity = identityMap.get(event.getPubKey().toString());
        if(Objects.isNull(identity)){
            throw new NostrException("encryptDirectMessage() identity is null");
        }
        identity.encryptDirectMessage(event);
    }

    @Override
    public void sign(GenericEvent event) throws NostrException {
        Identity identity = identityMap.get(event.getPubKey().toString());
        if(Objects.isNull(identity)){
            throw new NostrException("sign() identity is null");
        }
        identity.sign(event);
    }

}
