package com.codetroopers.shakemytours.core.components;

import android.content.Context;

import com.codetroopers.shakemytours.core.modules.ApplicationModule;
import com.codetroopers.shakemytours.core.modules.HomeActivityModule;
import com.codetroopers.shakemytours.ui.activity.HomeActivity;

public class ComponentsFactory {
    public static ComponentsFactory INSTANCE = null;

    public static ComponentsFactory get() {
        if (INSTANCE == null) {
            INSTANCE = new ComponentsFactory();
        }
        return INSTANCE;
    }

    /**
     * Visible for testing
     */
    public static void register(ComponentsFactory instance) {
        INSTANCE = instance;
    }

    /**********************************************************************************************/

    protected ComponentsFactory() {
    }

    public ApplicationComponent buildApplicationComponent(Context applicationContext) {
        return DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(applicationContext))
                .build();
    }

    public HomeActivityComponent buildHomeActivityComponent(ApplicationComponent applicationComponent, HomeActivity homeActivity) {
        return applicationComponent.homeActivityComponent(new HomeActivityModule(homeActivity));
    }
}
