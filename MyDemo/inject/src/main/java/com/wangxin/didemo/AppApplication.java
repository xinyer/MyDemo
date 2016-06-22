package com.wangxin.didemo;

import android.app.Application;

import com.wangxin.didemo.inject.component.ApplicationComponent;
import com.wangxin.didemo.inject.component.DaggerApplicationComponent;
import com.wangxin.didemo.inject.module.ApplicationModule;

public class AppApplication extends Application {
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
