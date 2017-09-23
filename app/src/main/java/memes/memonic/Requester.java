package memes.memonic;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.android.volley.RequestQueue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Andrew on 2017-09-23.
 */

public class Requester {

//    public static final MediaType contentType
//            = MediaType.parse("Content-Type: application/octet-stream");
//
////    OkHttpClient client = new OkHttpClient();
//
//    String post(OkHttpClient client, String url, String bytes, String key) throws IOException {
//        RequestBody body = RequestBody.create(contentType, bytes);
//        Request request = new Request.Builder()
//                .url(url)
//                .addHeader("Content-Type", "application/octet-stream")
//                .addHeader("Ocp-Apim-Subscription-Key", key) //Put subcription key here
//                .post(body)
//                .build();
//
//
//        try {
//            if (request != null) {
//                Response response = client.newCall(request).execute();
//                if (!response.isSuccessful()) {
//                    throw new IOException("Unexpected code " + response);
//                }
//                return response.body().string();
//            }}catch(IOException e){
//                e.printStackTrace();
//            }
//        return "1";

//
//        }catch (IOException e){
//            e.printStackTrace();
//            return "1";
//        }
//    }
//
//
//    String returnJson(){
//        return (Resources.getSystem().getString(R.string.Example));
//    }

    public String posty(String url,String key, String... strings) {
        String result = "";
        String personId = strings[0];
        HttpClient httpclient = new DefaultHttpClient();
        try {
            URIBuilder builder = new URIBuilder(url);
            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            request.setHeader("Ocp-Apim-Subscription-Key", key);
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                result = EntityUtils.toString(entity);
            }

            JSONObject emotionData = new JSONObject(result);
            String emotion = emotionData.getString("name");

            return emotion;
        } catch (Exception e) {
            return e.toString();
        }
    }
}
