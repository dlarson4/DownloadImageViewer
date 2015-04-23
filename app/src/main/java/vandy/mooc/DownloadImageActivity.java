package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

public class DownloadImageActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    public  static final String URL = "URL";
    public static final String IMAGEPATH = "IMAGEPATH";
    public static final String ASYNCTASK = "ASYNCTASK";

    private RetainedFragmentManager retainedFragmentManager = new RetainedFragmentManager(this, "DownloadImageActivityTag");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.download_activity);
        Log.d(TAG, "onCreate()");

        if(retainedFragmentManager.firstTimeIn()) {
            Log.d(TAG, "First time DownloadImageActivity was run");
            retainedFragmentManager.put(URL, getIntent().getData());
        } else {
            Uri pathToImage = retainedFragmentManager.get(IMAGEPATH);
            if(pathToImage != null) {
                Log.d(TAG, "Download appears complete");
                complete(pathToImage);
            } else {
                Log.d(TAG, "Download not yet complete, continuing");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        DownloadAsyncTask downloadAsyncTask = (DownloadAsyncTask)retainedFragmentManager.get(ASYNCTASK);
        if(downloadAsyncTask == null) {
            downloadAsyncTask = new DownloadAsyncTask(this, retainedFragmentManager);
            downloadAsyncTask.execute( (Uri)retainedFragmentManager.get(URL) );
        }
    }

    public void complete(Uri uri) {
        Intent intent = new Intent();
        intent.setData(uri);
        setResult(RESULT_OK, intent);
        finish();
    }
}
