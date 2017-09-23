package memes.memonic;

import android.content.res.Resources;

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

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Ocp-Apim-Subscription-Key", Resources.getSystem().getString(R.string.Sub_key))
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()){
            return response.body().string();
        }
    }

    String returnJson(){
        return (Resources.getSystem().getString(R.string.Example));
    }
}
