package com.github.xzwj87.mineflea.market.internal.di.component;

import android.app.Activity;

import com.github.xzwj87.mineflea.market.internal.di.PerActivity;
import com.github.xzwj87.mineflea.market.internal.di.module.ActivityModule;

import dagger.Component;

/**
 * Created by jason on 9/29/16.
 */

@PerActivity
@Component(dependencies = {AppComponent.class},modules = {ActivityModule.class})
public interface ActivityComponent {
    Activity activity();
    //Context context();
}
