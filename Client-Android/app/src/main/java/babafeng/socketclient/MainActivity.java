package babafeng.socketclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class MainActivity extends AppCompatActivity {
    private static final String RSA_MODE = "RSA";
    private static final String HOST = "192.168.0.254";
    private static final int PORT = 65534;

    private static final String MODULUS = "123922356621773892093290279002883432724294869090195479756888583193788713815017178049839844595149749802113856496772314283819876699596170658659825021554476288066087009817109373394420242349885777384281101354176289954207605455724337655644518451156223477355327320779930137961364056252146197709014891936102776300129";
    private static final String PUBLICEXPONENT = "65537";

    private static final int SHOW_RESPONSE = 0;
    private static final String TAG = "BABAFENG2016";
    public static String allMessage = "";

    private static String key = "";
    EditText mexit;
    TextView textview;

    // 主线程接收子线程消息的方法
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    allMessage += response + "\n";
                    textview.setText(allMessage);
            }
        }
    };


    // 向服务端请求一个随机密钥
    private static String getRandomKey() throws Exception {
        String key = SocketClient.send(HOST, PORT, "getRandomKey");
        return key;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mexit = (EditText) findViewById(R.id.edit1);
        mexit.setSingleLine();
        textview = (TextView) findViewById(R.id.textview);
        clickButton();
    }

    private void clickButton() {
        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                change();
            }
        });
    }

    // 这就是随机密钥交换实现的方法
    private void change() {
        String message = mexit.getText().toString();
        if (true) {
            final String sendMessage = message;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        if (!key.equals(null) && !key.equals("")){
                            key = key;
                        }
                        else{
                            // 向服务端请求一个随机密钥，并使用rsa解密
                            key = RSADecrypt(getRandomKey());
                            Log.d(TAG, "请求一个随机密钥: "+ key);
                        }

                        Log.d(TAG, "本轮加解密随机密钥: "+ key);

                        // 使用得到的随机密钥加密123456发送到服务端
                        String enMessage = AESEncrypt(key, "123456");
                        Log.d(TAG, "加密后发往服务端的消息: " + enMessage);
                        String reMessage = SocketClient.send(HOST, PORT, enMessage);
                        Log.d(TAG, "接收到来自服务端的消息: " + reMessage);

                        String deCallMessage = AESDecrypt(key, reMessage);
                        Log.d(TAG, "来自服务端的消息解密后: " + deCallMessage);

                        // 使用rsa解密消息中携带的随机密钥
                        String newRandomKey = RSADecrypt(deCallMessage.split("&")[1].split(":")[1]);
                        Log.d(TAG, "消息中携带的随机密钥: " + newRandomKey);

                        // 将新得到的随机密钥作为下一轮加解密密钥
                        key = newRandomKey;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            mexit.setText(null);
        } else {
            Toast.makeText(getApplicationContext(), "Null Input!", Toast.LENGTH_LONG).show();
        }
    }

    private String RSADecrypt(String message) {
        String deMessage = null;
        try {
            RSAPublicKey publickey = RSACrypto.getPublicKey(MODULUS, PUBLICEXPONENT);

            deMessage = RSACrypto.decrypt(publickey, message);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return deMessage;
    }


    private String AESEncrypt(String key, String message) {
        Log.d(TAG, "AESEncrypt key: " + key);
        String enMessage = AESCrypto.encrypt(key, message);
        return enMessage;
    }

    private String AESDecrypt(String key, String message) {
        Log.d(TAG, "AESDecrypt key: " + key);
        String deMessage = AESCrypto.decrypt(key, message);
        return deMessage;
    }
}
