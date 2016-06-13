package com.wangxin.didemo;

import android.app.Activity;
import android.os.Bundle;

import com.wangxin.didemo.di.ActivityComponent;
import com.wangxin.didemo.di.ActivityModule;
import com.wangxin.didemo.di.DaggerActivityComponent;

import javax.inject.Inject;

public class MainActivity extends Activity {

    ActivityComponent component;

    @Inject
    ToastUtil toastUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.component = DaggerActivityComponent.builder()
                .applicationComponent(((BaseApplication) getApplication()).getComponent())
                .activityModule(new ActivityModule(this))
                .build();


        this.component.injectActivity(this);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toastUtil.show(this, "哈哈");
    }
}
