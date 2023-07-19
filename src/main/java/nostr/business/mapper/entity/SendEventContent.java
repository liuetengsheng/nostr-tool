package nostr.business.mapper.entity;

import lombok.Builder;
import lombok.Data;
import nostr.event.tag.CustomizeTag;

import java.util.List;

@Data
@Builder
public class SendEventContent {
    public SendEventContent() {
    }
    public SendEventContent(String from, int kind, String to, String content, List<CustomizeTag> tagList, String originEventId) {
        this.from = from;
        this.kind = kind;
        this.to = to;
        this.content = content;
        this.tagList = tagList;
        this.originEventId = originEventId;
    }

    private String from;
    private int kind;
    private String to;
    private String content;
    private List<CustomizeTag> tagList;
    private String originEventId;
}
