package babafeng;

import java.security.SecureRandom;
import java.util.Random;

public class RandomKey {
    public static String getRandomKey(){
        Random random = new SecureRandom();
        byte[] byteKey = new byte[16];
        random.nextBytes(byteKey);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < byteKey.length; i++) {
            String hex = Integer.toHexString(0xff & byteKey[i]);
            if (hex.length() == 1)
                stringBuffer.append('0');
            stringBuffer.append(hex);
        }
        String randomKey = stringBuffer.toString();
        return randomKey;
    }
}
