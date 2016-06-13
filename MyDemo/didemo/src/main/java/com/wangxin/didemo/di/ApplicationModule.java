package com.wangxin.didemo.di;

import android.app.Application;
import android.content.Context;

import com.wangxin.didemo.BaseApplication;
import com.wangxin.didemo.ToastUtil;

import javax.inject.Singleton;

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
