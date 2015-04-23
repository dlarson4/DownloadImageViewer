package vandy.mooc;

import android.net.Uri;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class FilterImageAsyncTask extends AsyncTask<Uri, Void, Uri> {
    private final String TAG = getClass().getSimpleName();

    private final WeakReference<FilterImageActivity> activity;
    private RetainedFragmentManager retainedFragmentManager;

    public FilterImageAsyncTask(FilterImageActivity activity, RetainedFragmentManager retainedFragmentManager) {
        this.activity = new WeakReference<FilterImageActivity>(activity);
        this.retainedFragmentManager = retainedFragmentManager;
    }

    @Override
    protected Uri doInBackground(Uri... params) {
        Uri uri = Utils.grayScaleFilter(activity.get(), (Uri)retainedFragmentManager.get(FilterImageActivity.URL));
        retainedFragmentManager.put(FilterImageActivity.IMAGEPATH);
        return uri;
    }

    @Override
    protected void onPostExecute(Uri result) {
        if(activity.get() != null) {
            activity.get().complete(result);
        }
    }
}
