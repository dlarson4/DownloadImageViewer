package vandy.mooc;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

public class DownloadAsyncTask extends AsyncTask<Uri, Void, Uri> {
    private final String TAG = getClass().getSimpleName();

    private final WeakReference<DownloadImageActivity> activity;
    private RetainedFragmentManager retainedFragmentManager;

    public DownloadAsyncTask(DownloadImageActivity activity, RetainedFragmentManager retainedFragmentManager) {
        this.activity = new WeakReference<DownloadImageActivity>(activity);
        this.retainedFragmentManager = retainedFragmentManager;
    }

    @Override
    protected Uri doInBackground(Uri... params) {
        Uri uri = Utils.downloadImage(activity.get(), (Uri)retainedFragmentManager.get(DownloadImageActivity.URL));
        retainedFragmentManager.put(DownloadImageActivity.IMAGEPATH);
        return uri;
    }

    @Override
    protected void onPostExecute(Uri result) {
        if(activity.get() != null) {
            activity.get().complete(result);
        }
    }
}