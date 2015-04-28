package com.homich.android.criminalintent;

import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;

/**
 * Created by root on 09.03.15.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
