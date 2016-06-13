package com.wangxin.didemo.inject.module;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by wangxin on 16/6/13.
 */

@Module
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    Activity providesActivity() {
        return mActivity;
    }
}


