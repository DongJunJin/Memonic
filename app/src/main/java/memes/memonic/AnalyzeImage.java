package memes.memonic;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tarunkhanna on 09/10/17.
 */

public class AnalyzeImage  extends AsyncTask<String, Integer, JSONArray> {

    JSONArray jsonResponse = new JSONArray();
    String maxEmotion = "";
    String url;
    String key;
    File fileName;
    TextView output;
    Context ctx;

    AnalyzeImage(Context ctx, String url, String key, File fileName) {
        this.url = url;
        this.key = key;
        this.fileName = fileName;
        this.ctx = ctx;
    }
    @Override
    protected JSONArray doInBackground(String... params) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);//You can get an inputStream using any IO API
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);


        HttpClient hc = new DefaultHttpClient();
        String message;

        HttpPost p = new HttpPost(url + "fer");
        JSONObject object = new JSONObject();
        try {

            object.put("api_key", key);
            object.put("data", encodedString);

        } catch (Exception ex) {
            Log.d("TK-ERROR", "Unable to put json keys");
        }

        try {
            message = object.toString();

            p.setEntity(new StringEntity(message, "UTF8"));
            p.setHeader("Content-type", "application/json");
            HttpResponse resp = hc.execute(p);
            if (resp != null) {
                jsonResponse.put(new JSONObject(EntityUtils.toString(resp.getEntity())));
                if (resp.getStatusLine().getStatusCode() == 204) {
                    Log.d("YAY", "no response");
                } else if (resp.getStatusLine().getStatusCode() == 200) {
                    Log.d("YAY", "yeah!");

                } else {
                    Log.d("ERROR Status code", "" + resp.getStatusLine().getStatusCode());
                }
            }
            Log.d("YAY", resp.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }

    protected void onPostExecute(JSONArray json) {
        if (json == null) {
            Log.d("Results", "NUll Array");
        }
        else {
            Log.d("Results", json.toString());
            output.setText(json.toString());
            try {
                JSONObject eResults = (JSONObject) json.get(0);

                maxEmotion = JSONToMelody.findMaxEmotionFromEmotionsArrayObject((JSONObject) eResults.get("results"));
                Log.d("Results", maxEmotion);
                MainActivity.playMaxEmotionMelody(maxEmotion, ctx);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
