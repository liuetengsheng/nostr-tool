package nostr.business.job;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import nostr.business.config.WriteFiltersConfig;
import nostr.business.service.MessageSendV2Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Description 请描述类的业务用途
 * @Author young
 * @Date 2023/3/10 14:30
 */
@Component
@Order(2)
@Slf4j
public class SendMessageJob implements CommandLineRunner {

    @Resource
    private MessageSendV2Service messageSendV2Service;

    @Override
    public void run(String... args) throws Exception {
        boolean falg = Boolean.FALSE;
        while (true){
            try {
                if(falg){
                    messageSendV2Service.executeSendMsg();
                }else {
                    //初始化完成
                    if(WriteFiltersConfig.writetClient != null && WriteFiltersConfig.writetClient.getService() != null && WriteFiltersConfig.filtersList != null){
                        falg = Boolean.TRUE;
                    }
                }
            } catch (Exception e) {
                log.error("队列发送消息异常：",e.fillInStackTrace());
            }
            Thread.sleep(50);
        }
    }
}
