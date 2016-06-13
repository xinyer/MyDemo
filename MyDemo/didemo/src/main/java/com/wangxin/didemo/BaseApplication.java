package com.wangxin.didemo;

import android.app.Application;

import com.wangxin.didemo.di.ApplicationComponent;
import com.wangxin.didemo.di.ApplicationModule;
import com.wangxin.didemo.di.DaggerApplicationComponent;

/**
 * Created by wangxin on 16/6/13.
 */
public class BaseApplication extends Application {
    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        this.component = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
        this.component.injectApplication(this);
    }


    public ApplicationComponent getComponent() {
        return this.component;
    }
}
