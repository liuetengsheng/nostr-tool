package nostr.business.job;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import nostr.business.config.ReadFiltersConfig;
import nostr.ws.Connection;
import org.eclipse.jetty.websocket.api.Session;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class ReadSocketJob {
    @Resource
    ReadFiltersConfig readFiltersConfig;

    @Scheduled(cron = "0/5 * * * * ?")
    public void listenerAndConnect(){
        //初始化未完成
        if(ReadFiltersConfig.readClient == null || ReadFiltersConfig.readClient.getService() == null || ReadFiltersConfig.filtersList == null){
            return;
        }
        //成功建立连接节点数少于要求节点数
        if(ReadFiltersConfig.readClient.getRelays().size() != ReadFiltersConfig.readClient.getConnectionMap().size()){
            readFiltersConfig.Reconnection();
            return;
        }

        Map<String, Connection> connectionMap = ReadFiltersConfig.readClient.getConnectionMap();
        Set<String> strings = connectionMap.keySet();
        boolean falg = Boolean.FALSE;
        for (String string : strings) {
            Connection connection =  connectionMap.get(string);
            Session session = connection.getSession();
            String data = "ping";
            ByteBuffer payload = ByteBuffer.wrap(data.getBytes());
            try {
                session.getRemote().sendPing(payload);
            } catch (IOException e) {
                log.error("当前读节点连接已断开：{},{}",string, e.fillInStackTrace());
                try {
                    Connection NewConnection = new Connection(connection.getRelay(),connection.getService());

                    NewConnection.getSession().getRemote().sendPing(payload);

                    ReadFiltersConfig.readClient.getConnectionMap().put(string, NewConnection);
                    falg = Boolean.TRUE;
                } catch (Exception ee) {
                    log.error("当前读节点连接重连失败：{},{}",connection.getRelay(), e.fillInStackTrace());
                }
            }
        }

        //读节点重连后,重新订阅节点信息
        if(falg){
            readFiltersConfig.Reconnection();
        }
    }
}
