package memes.memonic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    Uri photo;
    byte[] imagebytes;
    private Uri outputFileUri;
    Intent cameraIntent;
    JSONObject jsonResponse;
    String url = "https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize";
    File file;
    String tossValue;

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
        //this.imageView = (ImageView) this.findViewById(R.id.imageView1);

        ImageView pushButton = (ImageView) this.findViewById(R.id.push);

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

        ImageView photoButton = (ImageView) this.findViewById(R.id.upload);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                outputFileUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"Pic_" +
                        String.valueOf(System.currentTimeMillis()) + ".jpg"));
                file = new File(Environment.getExternalStorageDirectory(),"Pic_" +
                        String.valueOf(System.currentTimeMillis()) + ".jpg");

                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

//        ConnectivityManager cm =
//                (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        boolean isConnected = activeNetwork != null &&
//                activeNetwork.isConnectedOrConnecting();
//
//        Log.d("Network", isConnected ? "works" : "doesnt work");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            ByteArrayOutputStream baos;
            ByteArrayInputStream inputStream;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if(bitmap != null){
                Log.d("Success" , "Success");
                //Convert to Base64
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                inputStream = new ByteArrayInputStream(baos.toByteArray());
                imagebytes = baos.toByteArray();
                tossValue = Base64.encodeToString(imagebytes, Base64.DEFAULT);
                Log.d("Value", tossValue);
//                if(tossValue != null){
//                        Log.d("Penis Head", tossValue);
//                    String source = file.toURI().toString();
//                    args = new Bundle();
//                    store = new Bundle();
//                    args.putString("Imagefile", source);

//            InputStream iStream = null;
//            try {
//                iStream = getContentResolver().openInputStream(outputFileUri);
//                imagebytes = getBytes(iStream);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

//            RandomAccessFile f = null;
//            try {
//                f = new RandomAccessFile(file.getAbsolutePath(), "r");
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
////                imagebytes = new byte[0];
//            try {
//                imagebytes = new byte[(int)f.length()];
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                f.readFully(imagebytes);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            try {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//            int bytes = bitmap.getByteCount();
//            ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
//            bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

//            imagebytes = buffer.array(); //Get the underlying array containing the data.
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class SyncedTask extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            posty(url,"afac5dce35cb428eabff3e082800df9b",tossValue);
            return null;
        }

        protected void onPostExecute(Double result){
            Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
        }

        protected void onProgressUpdate(Integer... progress){
            Toast.makeText(getApplicationContext(),progress[0].toString(),Toast.LENGTH_LONG).show();
        }

        public JSONObject posty(String url, String key, String... strings) {
            String result = "";

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(url);

            try {
//                URIBuilder builder = new URIBuilder(url);
//                URI uri = builder.build();
                request.setHeader("Content-Type", "application/octet-stream");
                request.setHeader("Ocp-Apim-Subscription-Key", key);


                StringEntity reqEntity = new StringEntity(Arrays.toString(strings));
                request.setEntity(reqEntity);
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    result = EntityUtils.toString(entity);
                }
                if(result != null) {
                    jsonResponse = new JSONObject(result);
                }
                return jsonResponse;
            } catch (Throwable throwable){
                throwable.printStackTrace();
                return null;
            }
        }
    }
}
