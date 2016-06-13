package com.wangxin.didemo.inject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by wangxin on 16/6/13.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PreActivity {
}
