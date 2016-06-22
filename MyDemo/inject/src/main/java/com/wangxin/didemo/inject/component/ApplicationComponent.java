package com.wangxin.didemo.inject.component;

import com.wangxin.didemo.AppApplication;
import com.wangxin.didemo.inject.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by wangxin on 16/6/13.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    AppApplication injectApplication(AppApplication application);

}
