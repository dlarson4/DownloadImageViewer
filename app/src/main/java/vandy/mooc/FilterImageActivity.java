package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class FilterImageActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    public  static final String URL = "URL";
    public static final String IMAGEPATH = "IMAGEPATH";
    public static final String ASYNCTASK = "ASYNCTASK";

    private RetainedFragmentManager retainedFragmentManager = new RetainedFragmentManager(this, "FilterImageActivityTag");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.filter_activity);
        Log.d(TAG, "onCreate()");

        if(retainedFragmentManager.firstTimeIn()) {
            Log.d(TAG, "First time FilterImageActivity was run");
            retainedFragmentManager.put(URL, getIntent().getData());
        } else {
            Uri pathToImage = retainedFragmentManager.get(IMAGEPATH);
            if(pathToImage != null) {
                Log.d(TAG, "Filter appears complete");
                complete(pathToImage);
            } else {
                Log.d(TAG, "Filter not yet complete, continuing");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        FilterImageAsyncTask filterAsyncTask = (FilterImageAsyncTask)retainedFragmentManager.get(ASYNCTASK);
        if(filterAsyncTask == null) {
            filterAsyncTask = new FilterImageAsyncTask(this, retainedFragmentManager);
            filterAsyncTask.execute( (Uri)retainedFragmentManager.get(URL) );
        }
    }

    public void complete(Uri uri) {
        Intent intent = new Intent();
        intent.setData(uri);
        setResult(RESULT_OK, intent);
        finish();
    }
}
