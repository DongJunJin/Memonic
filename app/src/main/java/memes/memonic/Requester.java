package memes.memonic;

import android.content.res.Resources;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Andrew on 2017-09-23.
 */

public class Requester {

    public static final MediaType contentType
            = MediaType.parse("application/octet-stream");

    OkHttpClient client = new OkHttpClient();

    String post(String url, byte[] json) throws IOException {
        RequestBody body = RequestBody.create(contentType, json);
        Request request = new Request.Builder()
                .url(url)
                //.addHeader("Content-Type", "application/octet-stream")
                .addHeader("Ocp-Apim-Subscription-Key", Resources.getSystem().getString(R.string.Sub_key)) //Put subcription key here
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()){
            return response.body().string();

        }catch (IOException e){
            e.printStackTrace();
            return "1";
        }
    }


    String returnJson(){
        return (Resources.getSystem().getString(R.string.Example));
    }
}
