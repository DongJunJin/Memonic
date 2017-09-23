package memes.memonic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
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
    String stringImage;

//    RequestQueue queue = Volley.newRequestQueue(this);
    String url = "https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.imageView = (ImageView) this.findViewById(R.id.imageView1);

        Button pushButton = (Button) this.findViewById(R.id.push);
        pushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posty(url,getResources().getString(R.string.Sub_key), stringImage);
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
            byte[] imageBytes = baos.toByteArray();
            stringImage = Arrays.toString(imageBytes);
            Log.d("Imagebyte", Arrays.toString(imageBytes));

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

    public JSONObject posty(String url,String key, String... strings){
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

            return emotionData;
        } catch (Exception e) {
            JSONObject aa = null;
            return aa;
        }
    }
}
