package vandy.mooc;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import java.util.HashMap;

public class RetainedFragmentManager {
    private RetainedFragment retainedFragment;
    private FragmentManager fragmentManager;
    private String retainedFragmentTag;

    public RetainedFragmentManager(Activity activity, String retainedFragmentTag) {
        this.fragmentManager = activity.getFragmentManager();
        this.retainedFragmentTag = retainedFragmentTag;
    }

    public boolean firstTimeIn() {
        retainedFragment = (RetainedFragment)fragmentManager.findFragmentByTag(retainedFragmentTag);
        if(retainedFragment != null) {
            return false;
        }
        retainedFragment = new RetainedFragment();
        fragmentManager.beginTransaction().add(retainedFragment, retainedFragmentTag).commit();
        return true;
    }

    public void put(String key, Object object) {
        retainedFragment.put(key, object);
    }

    public void put(Object object) {
        retainedFragment.put(object.getClass().toString(), object);
    }

    public <T> T get(String key) {
        return (T)retainedFragment.get(key);
    }

    public class RetainedFragment extends Fragment {
        private final String TAG = getClass().getSimpleName();

        private HashMap<String, Object> map = new HashMap<String, Object>();

        public RetainedFragment() {
        }

        // this method is only called once for this fragment
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public void put(String key, Object data) {
            map.put(key, data);
        }

        public Object get(String key) {
            return map.get(key);
        }
    }
}
