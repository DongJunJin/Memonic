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

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static memes.memonic.R.id.result;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    Uri photo;
    byte[] imagebytes;
    public ArrayList<Integer> notesArr;
    public static MediaPlayer mMediaPlayer;
    private Uri outputFileUri;
    Intent cameraIntent;
    File file;
    TextView output;
    ImageView resultView;
    public static JSONToMelody jsonToM;

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
                    AnalyzeImage analyze = new AnalyzeImage(MainActivity.this, getResources().getString(R.string.api_url), getResources().getString(R.string.indico_key), file);
                    analyze.output = output;
                    analyze.execute();
                    Log.d("YAY", "SUCCESS");
                } else {
                    Log.d("TRASH", "BAD");
                }
            }
        });

        resultView = (ImageView) this.findViewById(result);


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
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        // Configure emotionToMelodyHashMap HashMap
        jsonToM.emotionToMelodyHashMap.put("Angry", R.raw.cm);
        jsonToM.emotionToMelodyHashMap.put("Fear", R.raw.em);
        jsonToM.emotionToMelodyHashMap.put("Happy", R.raw.gm);
        jsonToM.emotionToMelodyHashMap.put("Neutral", R.raw.am);
        jsonToM.emotionToMelodyHashMap.put("Sad", R.raw.dm);
        jsonToM.emotionToMelodyHashMap.put("Surprise", R.raw.crm);

    }

    public static void playMaxEmotionMelody(String maxEmotion, Context context) {
        Log.d("Melody", maxEmotion);

        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(context, jsonToM.emotionToMelodyHashMap.get(maxEmotion));
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            while(mMediaPlayer.isPlaying()){
            }
            mMediaPlayer = null;
        }
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
            resultView.setImageBitmap(bitmap);
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

    public static MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
        }
    };


    public static void releaseMediaPlayer() {
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

}
