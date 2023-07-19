package nostr.business.job;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import nostr.business.config.WriteFiltersConfig;
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
public class WriteSocketJob {

    @Resource
    WriteFiltersConfig writeFiltersConfig;

    @Scheduled(cron = "0/5 * * * * ?")
    public void listenerAndConnect(){
        //初始化未完成
        if(WriteFiltersConfig.writetClient == null || WriteFiltersConfig.writetClient.getService() == null || WriteFiltersConfig.filtersList == null){
            return;
        }

        //成功建立连接节点数少于要求节点数
        if(WriteFiltersConfig.writetClient.getRelays().size() != WriteFiltersConfig.writetClient.getConnectionMap().size()){
            writeFiltersConfig.newConnect();
            return;
        }

        Map<String, Connection> connectionMap = WriteFiltersConfig.writetClient.getConnectionMap();
        Set<String> strings = connectionMap.keySet();
        for (String string : strings) {
            Connection connection =  connectionMap.get(string);
            Session session = connection.getSession();
            String data = "ping";
            ByteBuffer payload = ByteBuffer.wrap(data.getBytes());
            try {
                session.getRemote().sendPing(payload);
            } catch (IOException e) {
                log.error("当前写节点连接已断开：{},{}",string, e.fillInStackTrace());
                try {
                    Connection NewConnection = new Connection(connection.getRelay(),connection.getService());

                    NewConnection.getSession().getRemote().sendPing(payload);

                    WriteFiltersConfig.writetClient.getConnectionMap().put(string, NewConnection);
                } catch (Exception ee) {
                    log.error("当前写节点连接重连失败：{},{}",connection.getRelay(), e.fillInStackTrace());
                }
            }
        }
    }
}
