package nostr.business.mapper.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nostr.crypto.schnorr.Schnorr;
import nostr.event.impl.GenericEvent;
import nostr.event.unmarshaller.impl.EventUnmarshaller;
import nostr.util.EncryptMessage;
import nostr.util.NostrUtil;

import java.time.LocalDateTime;

/**
 * @Description 请描述类的业务用途
 * @Author young
 * @Date 2023/2/6 15:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("rps_client_message")
public class ClientMessagePO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 类型
     */
    private String type;

    /**
     * 数据id
     */
    private String subId;
    /**
     * eventId
     */
    private String eventId;
    /**
     * 原数据内容
     */
    private String content;
    /**
     * 解析数据
     */
    private String plaintextContext;
    /**
     * 发送时间
     */
    private String createdAt;
    /**
     * 公钥
     */
    private String publicKey;
    /**
     * noub格式nostr地址
     */
    private String npubAddress;
    /**
     * 状态：CREATE
     */
    private String status;

    /**
     * 消息nonce值维护
     */
    private Integer nonce;

    /**
     * 消息类型：0读，1写
     */
    private Integer operateType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

//    /**
//     * 获取具体的content信息
//     * @param privateKey 当前机器人私钥
//     * @return 返回明文content信息
//     */
//    public String getContentString(byte[] privateKey) {
//        JSONObject jsonObject = JSON.parseObject(plaintextContext);
//        Integer kind = jsonObject.getInteger("kind");
//        String content1 = jsonObject.getString("content");
//
//        if (kind == 4){
//            content1 = EncryptMessage.decodeMessage(privateKey,publicKey,content1);
//        }
//
//        if (StringUtils.isBlank(content1)){
//            return Strings.EMPTY;
//        }
//        return content1;
//    }

    private void init(byte[] privateKey){

    }

    public void decode(byte[] privateKey) {
        JSONObject jsonObject = JSON.parseObject(content);
        Integer kind = jsonObject.getInteger("kind");
        String content1 = jsonObject.getString("content");

        if (kind == 4){
            content1 = EncryptMessage.decodeMessage(privateKey,publicKey,content1);
        }

        this.plaintextContext = content1;

//        if (StringUtils.isBlank(content1)){
//            return Strings.EMPTY;
//        }
//        return content1;
    }

    /**
     * nip 12  r 返回格式是否要json
     * @return 返回格式
     */
    public boolean returnFormat(){
        JSONObject jsonObject = JSON.parseObject(this.getContent());
        JSONArray tags = jsonObject.getJSONArray("tags");
        for (Object t : tags) {
            JSONArray tag = JSON.parseArray(t.toString());
            if (tag.get(0).equals("r") && tag.getString(1).toLowerCase().equals("json")) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * nip 12  a 指定私信代理
     * @return 代理人
     */
    public String agent(){
        JSONObject jsonObject = JSON.parseObject(this.getContent());
        JSONArray tags = jsonObject.getJSONArray("tags");
        for (Object t : tags) {
            JSONArray tag = JSON.parseArray(t.toString());
            if (tag.get(0).equals("a")) {
                return tag.getString(1);
            }
        }
        return null;
    }

    /**
     * nip 12  n 专业用户指定nonce值
//     * @param content 消息
     */
    public void analysisNonce(){
        JSONObject jsonObject = JSON.parseObject(this.getContent());
        JSONArray tags = jsonObject.getJSONArray("tags");
        for (Object t : tags) {
            JSONArray tag = JSON.parseArray(t.toString());
            if (tag.get(0).equals("n")) {
                this.nonce = tag.getInteger(1);
                return;
            }
        }
        // 其他用户默认0设置
        this.nonce = 1;
    }


    /**
     * 验签
     * @return 验签是否通过
     */
    public boolean verify(){
        try {
            JSONObject body = JSON.parseObject(this.getContent());
            String sign = body.getString("sig");
            GenericEvent event = (GenericEvent) new EventUnmarshaller(body.toJSONString(),true).unmarshall();
            event.setId(body.getString("id"));
            event.setCreatedAt(body.getLong("created_at"));
            event.updateSerialized();
            return Schnorr.verify(NostrUtil.sha256(event.get_serializedEvent()),event.getPubKey().getRawData(),NostrUtil.hexToBytes(sign));
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {

//        String str = " balanceOf      tokenAddress   owner ";
//        String[] ss = str.trim().split(" ");
//
//        List<String> collect = Arrays.stream(ss).filter(s -> !s.equals("")).collect(Collectors.toList());

        byte[]  privateKey = NostrUtil.hexToBytes("4a6f826ecc20896ab990841a241a58930ff009fd4ef88ffa80e295c6b5741b80");
        String pubKey = "2efe026bc9cde7d153b690ac33106777948d97a9d263966cca6d1fda88e5f35b";
        String context = "QwTVLqZQNHIRYJZM9CUCAg==?iv=Haemv8cIRck0G9Z9bJN+QA==";
        String content1 = EncryptMessage.decodeMessage(privateKey, pubKey, context);
        System.out.println(content1);
    }
}
