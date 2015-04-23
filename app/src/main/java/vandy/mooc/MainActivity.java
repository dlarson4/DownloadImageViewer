package vandy.mooc;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnLongClickListener;

/**
 * A main Activity that prompts the user for a URL to an image and
 * then uses Intents and other Activities to download the image and
 * view it.
 */
public class MainActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    /**
     * A value that uniquely identifies the request to download an
     * image.
     */
    private static final int DOWNLOAD_IMAGE_REQUEST = 1;
    private static final int FILTER_IMAGE_REQUEST = 2;

    private boolean running = false;

    /**
     * EditText field for entering the desired URL to an image.
     */
    private EditText mUrlEditText;

    /**
     * URL for the image that's downloaded by default if the user
     * doesn't specify otherwise.
     */
    private Uri mDefaultUrl = Uri.parse("http://www.dre.vanderbilt.edu/~schmidt/robot.png");

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.main_activity);
        mUrlEditText = (EditText)this.findViewById(R.id.url);

        // so you can long-press on the textView and copy that URL to the clipboard
        final TextView textView = (TextView)this.findViewById(R.id.textView1);
        textView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("URL", textView.getText());
                clipboard.setPrimaryClip(clip);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
        });
    }

    /**
     * Called by the Android Activity framework when the user clicks
     * the "Find Address" button.
     *
     * @param view The view.
     */
    public void downloadImage(View view) {
        try {
            if(running) {
                return;
            }
            running = true;
            // Hide the keyboard.
            hideKeyboard(this, mUrlEditText.getWindowToken());

            Uri uri = getUrl();
            if(uri != null) {
                startActivityForResult(makeDownloadImageIntent(uri), DOWNLOAD_IMAGE_REQUEST);
            }
        } catch (Exception e) {
            Log.d(TAG, "Error downloading image", e);
            e.printStackTrace();
        }
    }

    /**
     * Hook method called back by the Android Activity framework when
     * an Activity that's been launched exits, giving the requestCode
     * it was started with, the resultCode it returned, and any
     * additional data from it.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DOWNLOAD_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Intent intent = makeFilterImageIntent(data.getDataString());
                startActivityForResult(intent, FILTER_IMAGE_REQUEST);
            } else {
                Toast.makeText(this, "The image did not download successfully", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == FILTER_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Intent intent = makeGalleryIntent(data.getDataString());
                startActivity(intent);
            } else {
                Toast.makeText(this, "The image did not filter successfully", Toast.LENGTH_SHORT).show();
            }
            running = false;
        }
    }

    /**
     * Factory method that returns an implicit Intent for viewing the
     * downloaded image in the Gallery app.
     */
    private Intent makeGalleryIntent(String pathToImageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + pathToImageFile), "image/*");
        return intent;
    }

    /**
     * Factory method that returns an implicit Intent for downloading
     * an image.
     */
    private Intent makeDownloadImageIntent(Uri uri) {
        Intent intent = new Intent(this, DownloadImageActivity.class);
        intent.setData(uri);
        return intent;
    }

    private Intent makeFilterImageIntent(String pathToImageFile) {
        Intent intent = new Intent(this, FilterImageActivity.class);
        intent.setData(Uri.parse(pathToImageFile));
        return intent;
    }

    /**
     * Get the URL to download based on user input.
     */
    protected Uri getUrl() {
        Uri url = Uri.parse(mUrlEditText.getText().toString());

        if (URLUtil.isValidUrl(url.toString())) {
            // If the user didn't provide a URL then use the default.
            String uri = url.toString();
            if (uri == null || uri.equals("")) {
                url = mDefaultUrl;
            }
            return url;
        } else {
            Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show();
            return null;
        } 
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public void hideKeyboard(Activity activity, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }
}
