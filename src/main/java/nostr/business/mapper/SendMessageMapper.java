package nostr.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nostr.business.mapper.entity.SendMessagePO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description 请描述类的业务用途
 * @Author young
 * @Date 2023/3/9 17:32
 */
@Mapper
public interface SendMessageMapper extends BaseMapper<SendMessagePO> {

    @Delete("delete from rps_send_message where event_id= #{eventId}")
    void deleteByEventId(String eventId);

    @Select("select event_id from rps_send_message where reply_event_id= #{replyEventId} limit 1")
    String getEventIdByReplyEventId(String replyEventId);
}
