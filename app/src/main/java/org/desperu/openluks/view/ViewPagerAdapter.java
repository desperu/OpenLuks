package org.desperu.openluks.view;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.desperu.openluks.R;
import org.desperu.openluks.ui.main.MainFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[] {R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    public ViewPagerAdapter(Context context, FragmentManager fm, int behavior) {
        super(fm, behavior);
        mContext = context;
    }

    @Override
    public int getCount() { return 2; }

    @NonNull
    @Override
    public Fragment getItem(int position) { return MainFragment.newInstance(position); }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }
}
