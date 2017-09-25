package memes.memonic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    Uri photo;
    byte[] imagebytes;
    ArrayList<Integer> notesArr;
    private MediaPlayer mMediaPlayer;
    private Uri outputFileUri;
    Intent cameraIntent;
    JSONArray jsonResponse;
    String url = "https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize/";
    File file;
    Map<String,Integer> emotionToMelodyHashMap;
    TextView output;
    ImageView resultView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

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
        ImageView am = (ImageView) this.findViewById(R.id.am);
        am.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.am);
                    mMediaPlayer.start();
                    mMediaPlayer.setOnCompletionListener(mCompletionListener);
                }
            }
        });

        ImageView cm = (ImageView) this.findViewById(R.id.cm);
        cm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.cm);
                    mMediaPlayer.start();
                    mMediaPlayer.setOnCompletionListener(mCompletionListener);
                }

            }
        });
        ImageView crm = (ImageView) this.findViewById(R.id.crm);
        crm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.crm);
                    mMediaPlayer.start();
                    mMediaPlayer.setOnCompletionListener(mCompletionListener);
                }

            }
        });
        ImageView dm = (ImageView) this.findViewById(R.id.dm);
        dm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.dm);
                    mMediaPlayer.start();
                    mMediaPlayer.setOnCompletionListener(mCompletionListener);
                }

            }
        });
        ImageView em = (ImageView) this.findViewById(R.id.em);
        em.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.em);
                    mMediaPlayer.start();
                    mMediaPlayer.setOnCompletionListener(mCompletionListener);
                }

            }
        });
        ImageView gm = (ImageView) this.findViewById(R.id.gm);
        gm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.gm);
                    mMediaPlayer.start();
                    mMediaPlayer.setOnCompletionListener(mCompletionListener);
                }

            }
        });
        ImageView pushButton = (ImageView) this.findViewById(R.id.push);
        output = (TextView) this.findViewById(R.id.output);

        pushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    new SyncedTask().execute();
                    Log.d("YAY", "SUCCESS");
                } else {
                    Log.d("TRASH", "BAD");
                }
            }
        });

        resultView = (ImageView) this.findViewById(R.id.result);


        ImageView photoButton = (ImageView) this.findViewById(R.id.upload);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                outputFileUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"Pic_" +
                        String.valueOf(time) + ".jpg"));
                file = new File(Environment.getExternalStorageDirectory(),"Pic_" +
                        String.valueOf(time) + ".jpg");
                resultView.setImageURI(outputFileUri);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            }
        });
        emotionToMelodyHashMap = new HashMap<>();
        emotionToMelodyHashMap.put("anger", R.raw.cm);
        emotionToMelodyHashMap.put("disgust", R.raw.em);
        emotionToMelodyHashMap.put("happiness", R.raw.gm);
        emotionToMelodyHashMap.put("neutral", R.raw.am);
        emotionToMelodyHashMap.put("sadness", R.raw.dm);
        emotionToMelodyHashMap.put("surprise", R.raw.crm);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            ByteArrayOutputStream baos;
            ByteArrayInputStream inputStream;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if(bitmap != null){
                baos = new ByteArrayOutputStream();
                inputStream = new ByteArrayInputStream(baos.toByteArray());
                try {
                    imagebytes = FileUtils.readFileToByteArray(file);
                    Log.d("Values", imagebytes.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
        }
    };


    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    private class SyncedTask extends AsyncTask<String, Integer, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            return posty(url, getResources().getString(R.string.Sub_key),imagebytes); //Insert Subscription for Emotion API Key Here
        }

        protected void onPostExecute(JSONArray jsonArray){
            Log.d("Results", jsonArray.toString());
            if(jsonArray != null){
                JSONToMelody jsonToM = new JSONToMelody();
                ArrayList<String> s = jsonToM.findEachMaxEmotion(jsonArray);
                notesArr = jsonToM.createMelodiesArray();
                Log.d("Number", String.valueOf(notesArr.size()) );
                if (notesArr != null){
                    for (int i = 0; i < notesArr.size(); i++) {

                        output.append(i + ": " + s.get(i).toString()+"\n");

                        if (mMediaPlayer == null) {
                            mMediaPlayer = MediaPlayer.create(MainActivity.this, notesArr.get(i));
                            mMediaPlayer.start();
                            mMediaPlayer.setOnCompletionListener(mCompletionListener);
                            while(mMediaPlayer.isPlaying()){
                            }
                            mMediaPlayer = null;
                        }
                    }
                }
            }
        }

        protected void onProgressUpdate(Integer... progress){
            Toast.makeText(getApplicationContext(),progress[0].toString(),Toast.LENGTH_LONG).show();
        }

        public JSONArray posty(String url, String key, byte[] strings) {
            String result = "";

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(url);

            try {
                request.setHeader("Content-Type", "application/octet-stream");
                request.setHeader("Ocp-Apim-Subscription-Key", key);

                ByteArrayEntity reqEntity = new ByteArrayEntity(strings);
                request.setEntity(reqEntity);
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    result = EntityUtils.toString(entity);
                    jsonResponse = new JSONArray(result);
                }
                return jsonResponse;
            } catch (Throwable throwable){
                throwable.printStackTrace();
                return null;
            }
        }
    }

    public class JSONToMelody extends JSONArray{
        public ArrayList<String> arrEmotions = new ArrayList<>();
        public ArrayList<Integer> arrMelodies = new ArrayList<>();

        String findMaxEmotion(JSONObject json) {
            json = json.optJSONObject("scores");
            Iterator<String> jsonKeys = json.keys();

            String maxEmotion = "";
            Double maxVal = Double.MIN_VALUE;
            while (jsonKeys.hasNext()) {
                String key = jsonKeys.next();
                try {
                    Double temp = json.optDouble(key, Double.MIN_VALUE);
                    if (temp > maxVal) {
                        maxEmotion = key;
                        maxVal = temp;
                    }

                } catch(Exception e) {
                    // ignore
                    e.printStackTrace();
                }
            }

            return maxEmotion;
        }

        // wrapper for each face analyzed
        ArrayList<String> findEachMaxEmotion(JSONArray jsonArr) {

            int size = jsonArr.length();
            for (int i = 0; i < size; i++) {
                try {
                    String s = findMaxEmotion(jsonArr.getJSONObject(i));
                    arrEmotions.add(findMaxEmotion(jsonArr.getJSONObject(i)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return arrEmotions;
        }

    ArrayList<Integer> createMelodiesArray() {
        int size = arrEmotions.size();

        for (int i = 0; i < size; i++) {
            arrMelodies.add(emotionToMelodyHashMap.get(arrEmotions.get(i)));
        }

        return arrMelodies;
    }
    }
}
