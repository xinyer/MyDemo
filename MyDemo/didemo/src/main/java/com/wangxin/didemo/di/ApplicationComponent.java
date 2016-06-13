package com.wangxin.didemo.di;

import android.app.Activity;
import android.content.Context;

import com.wangxin.didemo.BaseApplication;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by wangxin on 16/6/13.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    BaseApplication injectApplication(BaseApplication application);


}
