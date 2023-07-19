package nostr.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nostr.business.mapper.entity.SendMessageRelayPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 请描述类的业务用途
 * @Author young
 * @Date 2023/3/10 16:04
 */
@Mapper
public interface SendMessageRelayMapper extends BaseMapper<SendMessageRelayPO> {
}
