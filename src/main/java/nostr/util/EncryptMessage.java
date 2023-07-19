package nostr.util;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * @Description 请描述类的业务用途
 * @Author young
 * @Date 2023/2/7 11:56
 */
public class EncryptMessage {

    private static byte[] getSharedSecret(String privateKeyHex, String publicKeyHex) throws NostrException {

        SecP256K1Curve curve = new SecP256K1Curve();
        ECPoint pubKeyPt = curve.decodePoint(NostrUtil.hexToBytes("02" + publicKeyHex));
        BigInteger tweakVal = new BigInteger(1, NostrUtil.hexToBytes(privateKeyHex));
        return pubKeyPt.multiply(tweakVal).getEncoded(true);
    }


    public static String decodeMessage(byte[] privateKey, String publicKey, String message) {
        // Get IV Message
        String base64Message = message.split("\\?iv=")[0];
        String iv = message.split("\\?iv=")[1];

        String decryptedData = null;
        try {
                final String secKeyHex = NostrUtil.bytesToHex(privateKey);
                final String pubKeyHex = NostrUtil.bytesToHex(NostrUtil.hexToBytes(publicKey));

                byte[] initialVectorBytes = Base64.getDecoder().decode(iv.getBytes(StandardCharsets.UTF_8));//initialVectorString.getBytes(StandardCharsets.UTF_8);
                byte[] encryptedDataBytes = Base64.getDecoder().decode(base64Message.getBytes(StandardCharsets.UTF_8));//encryptedData.getBytes(StandardCharsets.UTF_8);

                var sharedPoint = getSharedSecret(secKeyHex, pubKeyHex);
                var sharedX = Arrays.copyOfRange(sharedPoint, 1, 33);

                var sharedSecretKey = new SecretKeySpec(sharedX, "AES");

                //Decrypt
                IvParameterSpec initialVector = new IvParameterSpec(initialVectorBytes);
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, sharedSecretKey, initialVector);
                byte[] decryptedByteArray = cipher.doFinal(encryptedDataBytes);

        decryptedData = new String(decryptedByteArray, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return decryptedData;
    }

    public static String encryptMessage(byte[] privateKey, String publicKey, String message){

        try {
            final var Base64Encoder = Base64.getEncoder();
            final var msg = message.getBytes(StandardCharsets.UTF_8);

            final String secKeyHex = NostrUtil.bytesToHex(privateKey);
            final String pubKeyHex = NostrUtil.bytesToHex(NostrUtil.hexToBytes(publicKey));

            var sharedPoint = getSharedSecret(secKeyHex, pubKeyHex);
            var sharedX = Arrays.copyOfRange(sharedPoint, 1, 33);

            var iv = NostrUtil.createRandomByteArray(16);
            var ivParamSpec = new IvParameterSpec(iv);

            var sharedSecretKey = new SecretKeySpec(sharedX, "AES");
            var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, sharedSecretKey, ivParamSpec);

            var encryptedMessage = cipher.doFinal(msg);
            var encryptedMessage64 = Base64Encoder.encode(encryptedMessage);

            var iv64 = Base64Encoder.encode(ivParamSpec.getIV());

            return new String(encryptedMessage64) + "?iv=" + new String(iv64);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
