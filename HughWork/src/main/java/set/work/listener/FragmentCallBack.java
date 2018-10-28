package set.work.listener;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by Hugh on 2017/11/7.
 * fragment
 */

public interface FragmentCallBack {
    /**
     * 设置返回的fragment
     * @param selectedFragment
     */
    void setBackHandledFragment(Fragment selectedFragment);

    /**
     * 传值给activity
     * @param intent
     */
    void onFragmentCallBack(Intent intent);

    /**
     * 直接跳转
     * @param uri
     */
    void onFragmentInteraction(Uri uri);
}
