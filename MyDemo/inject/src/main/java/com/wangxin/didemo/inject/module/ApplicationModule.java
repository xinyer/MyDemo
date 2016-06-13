package com.wangxin.didemo.inject.module;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

/**
 * Created by wangxin on 16/6/13.
 */

@Module
public class ApplicationModule {

    Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application providesApplication() {
        return mApplication;
    }

}
