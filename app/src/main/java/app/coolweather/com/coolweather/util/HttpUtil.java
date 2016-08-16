package app.coolweather.com.coolweather.util;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jack on 2016/8/14.
 */
public class HttpUtil {

    static final String TAG = "HttpUtil";

    public interface HttpCallbackListener{
        void onFinish(String response);

        void onError(Exception e);
    }

    public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
        LogUtil.d(TAG, "sendHttpRequest, address=" + address);
        new Thread(new Runnable() {
            @Override
            public void run () {
                HttpURLConnection connection = null;
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(80000);
                    connection.setReadTimeout(80000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    LogUtil.d(TAG, "reading lines...");
                    LogUtil.d(TAG, "in=" + in.toString());
//                    int i = reader.read();
//                    LogUtil.d(TAG, "i=" + i);

//                    int i;
//                    while((i = reader.read()) != -1){
//                        LogUtil.d(TAG, "i=" + i);
//                        LogUtil.d(TAG, "i to string =" + Integer.toString(i));
//                        response.append(Integer.toString(i));
//                    }

                    while((line = reader.readLine()) != null){
                        LogUtil.d(TAG, "line=" + line);
                        response.append(line);
                    }

//                    char[] chars = new char[1];
//                    int len = 0;
//                    while (true) {
//                        try {
//                            len = reader.read(chars);
//                        }catch(EOFException e){
//                            e.printStackTrace();
//                        }
//
//                        if(len <= 0){
//                            break;
//                        }
//
//                        response.append(chars, 0, len);
//                    }

                    LogUtil.d(TAG, "response=" + response);

                    if(listener != null){
                        listener.onFinish(response.toString());
                    }
                }catch (Exception e){
                    if(listener != null){
                        listener.onError(e);
                    }
                }finally {
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
