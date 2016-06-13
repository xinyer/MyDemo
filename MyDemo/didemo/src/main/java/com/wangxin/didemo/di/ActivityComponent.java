package com.wangxin.didemo.di;

import com.wangxin.didemo.MainActivity;
import com.wangxin.didemo.ToastUtil;

import dagger.Component;

/**
 * Created by wangxin on 16/6/13.
 */

@PreActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    MainActivity injectActivity(MainActivity activity);

    ToastUtil toastUtil();
}
