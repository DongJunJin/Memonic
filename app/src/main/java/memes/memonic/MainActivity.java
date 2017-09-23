package memes.memonic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private JSONObject jsonObject;
    Bitmap photo;
    byte[] imagebytes;

    JSONObject response;

//    RequestQueue queue = Volley.newRequestQueue(this);
    String url = "https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                    , 1);
        }

        this.imageView = (ImageView) this.findViewById(R.id.imageView1);

        Button pushButton = (Button) this.findViewById(R.id.push);
        pushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(imagebytes != null && isNetworkAvailable()){
                    response = posty(url,getResources().getString(R.string.Sub_key), imagebytes);
                    Log.d("YAY", "SUCCESS");
                    if(response == null){
                        Log.d("Bad", "Garbage");
                    }else{
                        Log.d("O MAI", response.toString());
                    }
                }
                else{
                    Log.d("TRASH", "BAD");
                }
            }
        });
        Button photoButton = (Button) this.findViewById(R.id.button1);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

//        EmotionServiceRestClient

        ConnectivityManager cm =
                (ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Log.d("Network", isConnected ? "works" : "doesnt work" );
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imagebytes = baos.toByteArray();


//            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//            if (data != null) {
//                if (resultCode == RESULT_OK) {
//                    Requester requester = new Requester();
//
//                    String response = null;
//                    try {
//                        response = requester.post(client, , stringImage, getResources().getString(R.string.Sub_key));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Log.d("Memes", response);
//                }
//                if (requestCode == RESULT_CANCELED) {
//                }
//            }
        }
    }

    public JSONObject posty(String url,String key, byte[]... strings){
        String result = "";
        HttpClient httpclient = new DefaultHttpClient();
        try {
            URIBuilder builder = new URIBuilder(url);
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", key);

            StringEntity reqEntity = new StringEntity(Arrays.toString(strings));
            request.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                result = EntityUtils.toString(entity);
            }

            JSONObject emotionData = new JSONObject(result);

            return emotionData;
        } catch (Exception e) {
            JSONObject aa = null;
            return aa;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
