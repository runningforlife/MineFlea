package com.github.xzwj87.mineflea.market.internal.di.component;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.github.xzwj87.mineflea.market.internal.di.PerActivity;
import com.github.xzwj87.mineflea.market.internal.di.module.ActivityModule;
import com.github.xzwj87.mineflea.market.internal.di.module.AppModule;
import com.github.xzwj87.mineflea.market.ui.activity.BaseActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jason on 9/29/16.
 */

@PerActivity
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(BaseActivity activity);

    Application application();
    Context context();
}