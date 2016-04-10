package io.macromind.fyp_hkust;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, CNNListener {

    public static final String TAG = "MainActivity";
    public static final String EXTRA_RESULT_ARRAY = "io.macromind.fyp_hkust.result_array";
    public static final String EXTRA_IMAGE_PATH = "io.macromind.fyp_hkust.image_path";
    public static final String DEPLOY_PROTOTXT = "/sdcard/caffe_mobile/fyp/deploy.prototxt";
    public static final String MODEL = "/sdcard/caffe_mobile/fyp/fyp_iter_60000.caffemodel";
    public static String[] DIM_SUM_CLASSES;
    public static Integer[] DIM_SUM_INDEXES;
    static final int REQUEST_IMAGE_CAPTURE = 100;
    static final int REQUEST_IMAGE_PICK = 200;
    static final int MEDIA_TYPE_IMAGE = 1;
    static final int REQUEST_STORAGE = 2;

    private String mPath;
    private Uri fileUri;
    private ProgressDialog mDialog;
    private View mLayout;

    private CaffeMobile mCaffeMobile;

    static {
        System.loadLibrary("caffe");
        System.loadLibrary("caffe_jni");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.relative_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (!checkPermission()) {
            requestPermission();
        }

        setSupportActionBar(toolbar);
        FloatingActionButton selectFab = (FloatingActionButton) findViewById(R.id.select_fab);
        if (selectFab != null) {
            selectFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pickPicIntent = new Intent(Intent.ACTION_PICK);
                    pickPicIntent.setType("image/*");
                    startActivityForResult(pickPicIntent, REQUEST_IMAGE_PICK);
                }
            });
        }

        FloatingActionButton takePhotoFab = (FloatingActionButton) findViewById(R.id.take_photo_fab);
        if (takePhotoFab != null) {
            takePhotoFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (!checkPermission()) {
                        requestPermission();
                    } else {
                            takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                            startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            });
        }

        // Caffe setup
        mCaffeMobile = new CaffeMobile();
        mCaffeMobile.setNumThreads(4);
        mCaffeMobile.loadModel(DEPLOY_PROTOTXT, MODEL);

        float[] meanValues = {104, 117, 123};
        mCaffeMobile.setMean(meanValues);

        readClasses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                // Toast.makeText(this, "Image saved to:\n" +
                //         data.getData(), Toast.LENGTH_LONG).show();
                mPath = fileUri.getPath();
                imageProcess(mPath);

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        } else if (requestCode == REQUEST_IMAGE_PICK) {
            if(resultCode == RESULT_OK){
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = MainActivity.this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mPath = cursor.getString(columnIndex);
                cursor.close();
                imageProcess(mPath);
            }
            else {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void imageProcess(String path) {
        mDialog = ProgressDialog.show(MainActivity.this, "Classifying", "Just a sec", true);

        CNNTask cnnTask = new CNNTask(MainActivity.this);
        cnnTask.execute(path);
    }

    private class CNNTask extends AsyncTask<String, Void, int[]> {
        private CNNListener listener;
        private long startTime;

        public CNNTask(CNNListener listener) {
            this.listener = listener;
        }

        @Override
        protected int[] doInBackground(String... strings) {
            startTime = SystemClock.uptimeMillis();
            int[] resultArray = mCaffeMobile.predictImage(strings[0]);
            return resultArray;
        }

        @Override
        protected void onPostExecute(int[] array) {
            Log.i(TAG, String.format("elapsed wall time: %d ms", SystemClock.uptimeMillis() - startTime));
            listener.onTaskCompleted(array);
            super.onPostExecute(array);
        }
    }

    @Override
    public void onTaskCompleted(int[] result){
        if (mDialog != null) {
            mDialog.dismiss();
        }
        Intent i = new Intent(MainActivity.this, ResultActivity.class);
        i.putExtra(EXTRA_RESULT_ARRAY, result);
        i.putExtra(EXTRA_IMAGE_PATH, mPath);
        startActivity(i);
    }

    
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    /*
    *   Create a File for saving an image
    */
    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FYP_HKUST");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("FYP_HKUST", "failed to create a directory");
                return null;
            }
        }

        //To create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" +  timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }


    private void requestPermission() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Log.i(TAG,
                        "Displaying storage permission rationale to provide additional context.");
                Snackbar.make(mLayout, "Storage permission is needed to do classification.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_STORAGE);
                            }
                        })
                        .show();

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORAGE);

                // REQUEST_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "permission granted");

            return true;
        } else {
            Log.i(TAG, "permission not granted");

            return false;
        }
    }

    private void readClasses() {
        AssetManager am = this.getAssets();
        try {
            InputStream is = am.open("fyp_words.txt");
            Scanner sc = new Scanner(is);
            List<String> lines = new ArrayList<String>();
            List<Integer> index = new ArrayList<Integer>();
            while (sc.hasNextLine()) {
                final String temp = sc.nextLine();
                lines.add(temp.substring(temp.indexOf(" ") + 1));
                index.add(new Integer(temp.substring(0, temp.indexOf(" "))));
            }
            DIM_SUM_CLASSES = lines.toArray(new String[0]);
            DIM_SUM_INDEXES = index.toArray(new Integer[0]);
            Log.i("CLASSDD", DIM_SUM_CLASSES.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
