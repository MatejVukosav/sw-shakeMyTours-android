package com.codetroopers.shakemytours.core.modules;

import android.app.Activity;
import android.content.Context;

import com.codetroopers.shakemytours.core.components.ActivityScope;
import com.codetroopers.shakemytours.example.DummyContentFactory;

import dagger.Module;
import dagger.Provides;

@Module
public class HomeActivityModule {
    private final Activity activity;

    public HomeActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    public Activity provideActivity() {
        return activity;
    }

    // TODO put your application-specific providers here!

    @ActivityScope
    @Provides
    protected DummyContentFactory provideDummyContentFactory(@ForApplication Context context) {
        return new DummyContentFactory(context);
    }
}
