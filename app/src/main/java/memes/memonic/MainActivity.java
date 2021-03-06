package memes.memonic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
    TextView output;
    ImageView resultView;
    JSONToMelody jsonToM; //= new JSONToMelody();

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

        initializeNotesOnUI();

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

        // Configure emotionToMelodyHashMap HashMap
        jsonToM.emotionToMelodyHashMap.put("anger", R.raw.cm);
        jsonToM.emotionToMelodyHashMap.put("disgust", R.raw.em);
        jsonToM.emotionToMelodyHashMap.put("happiness", R.raw.gm);
        jsonToM.emotionToMelodyHashMap.put("neutral", R.raw.am);
        jsonToM.emotionToMelodyHashMap.put("sadness", R.raw.dm);
        jsonToM.emotionToMelodyHashMap.put("surprise", R.raw.crm);
    }

    private void initializeNotesOnUI() {
        ArrayList<String> notes = new ArrayList<>();
        notes.add("cm");
        notes.add("em");
        notes.add("gm");
        notes.add("am");
        notes.add("dm");
        notes.add("crm");

        for (int i = 0; i < notes.size(); i++) {
            final String note = notes.get(i);

            int resId = getResources().getIdentifier(notes.get(i), "id", getPackageName());
            ImageView imageView = (ImageView) MainActivity.this.findViewById(resId);
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mMediaPlayer == null) {
                        mMediaPlayer = MediaPlayer.create(MainActivity.this,
                                Uri.parse("android.resource://"+getPackageName()+"/raw/"+note));
                        mMediaPlayer.start();
                        mMediaPlayer.setOnCompletionListener(mCompletionListener);
                    }
                }
            });
        }
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
                jsonToM = new JSONToMelody();
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

                    if (notesArr.size() == 0) output.append("No Results but Request successful\n");
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
}
