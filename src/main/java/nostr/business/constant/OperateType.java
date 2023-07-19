package nostr.business.constant;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 请描述类的业务用途
 * @Author young
 * @Date 2023/6/6 17:06
 */
public class OperateType {

    public static final String TOKEN = "token";
    public static final String BALANCE = "balance";
    public static final String TRANSFER = "transfer";
    public static final String APPROVE = "approve";
    public static final String ALLOWANCE = "allowance";
    public static final String NONCE = "nonce";
    public static final String DEPOSIT = "deposit";
    public static final String MINT = "claim";
    public static final String WITHDRAW = "withdraw";
    public static final String ADDRESS = "address";
    public static final String ADD = "add";
    public static final String DELETE = "delete";
    public static final String BIND = "bind";
    public static final String COBO = "cobo";
    public static final String GET = "get";
    public static final String HELP = "help";
    public static final String PING = "ping";
    public static final String QUERY = "query";
    public static final String OPEN = "open";
    public static final String CLOSE = "close";
    public static final String ZAP = "zap";
    public static final String CONFIRM = "confirm";

    public static final String CHECK = "check";

    public static final String TAPROOT = "taproot";

    /**
     * 0读
     */
    public static final List<String> READ = new ArrayList<>();

    /**
     * 1写
     */
    public static final List<String> WRITE = new ArrayList<>();

    static {
        // 读
        READ.add(TOKEN);
        READ.add(BALANCE);
        READ.add(ALLOWANCE);
        READ.add(NONCE);
        READ.add(ADDRESS);
        READ.add(GET);
        READ.add(HELP);
        READ.add(PING);
        READ.add(QUERY);
        READ.add(CHECK);
        READ.add(TAPROOT);


        // 写
        WRITE.add(TRANSFER);
        WRITE.add(APPROVE);
        WRITE.add(MINT);
        WRITE.add(DEPOSIT);
        WRITE.add(WITHDRAW);
        WRITE.add(ADD);
        WRITE.add(DELETE);
        WRITE.add(BIND);
        WRITE.add(OPEN);
        WRITE.add(CLOSE);
        WRITE.add(COBO);
        WRITE.add(ZAP);
        WRITE.add(CONFIRM);

    }

    public static Integer getType(String content){
        if (StringUtils.isEmpty(content)){
            return 0;
        }
        String[] ss = content.trim().toLowerCase().split(" ");
        // 0 读
        if (READ.contains(ss[0])){
            return 0;
        }
        // 1 写
        if (WRITE.contains(ss[0])){
            return 1;
        }
        // 没有匹配到 走读快速处理
        return 0;
    }

}
