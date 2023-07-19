package nostr.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nostr.business.mapper.entity.ClientMessagePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description 请描述类的业务用途
 * @Author young
 * @Date 2023/2/6 15:28
 */
@Mapper
public interface ClientMessageMapper extends BaseMapper<ClientMessagePO> {

    //获取全局消息,过滤机器人自身消息
    @Select("select * from rps_client_message where create_time >= #{createTime} and public_key != #{pubKey}  order by modify_time asc")
    List<ClientMessagePO> selectLastByTimestamp(@Param("createTime") LocalDateTime createTime, @Param("pubKey") String pubKey);

    @Select("select * from rps_client_message where event_id= #{eventId}")
    ClientMessagePO selectByEventId(String eventId);

    @Select("select * from rps_client_message where event_id= #{eventId} and status = 'CREATE' limit 1")
    ClientMessagePO selectByEventIdAndInit(String eventId);

    @Select("select * from rps_client_message where sub_id= #{subId}")
    ClientMessagePO selectBySubId(String subId);

    @Select("select * from rps_client_message order by created_at desc limit 1")
    ClientMessagePO selectSysLastTime();

    @Update("update rps_client_message set status = #{status}, modify_time = #{modifyTime} where event_id = #{eventId}")
    int updateStatus(@Param("status") String status, @Param("modifyTime") LocalDateTime modifyTime, @Param("eventId") String eventId);

}
