package nostr.ws.handler;

import java.time.LocalDateTime;

public  abstract class IClientMessageService {
    public void dealOk(String message, String relay){

    }

    public void dealEvent(String message, String type, LocalDateTime now){

    }

    public void dealNote(String message,String type, LocalDateTime now){

    }
}
