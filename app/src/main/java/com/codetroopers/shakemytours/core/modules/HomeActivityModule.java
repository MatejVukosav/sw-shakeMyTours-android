package com.codetroopers.shakemytours.core.modules;

import android.app.Activity;

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

}
