package com.codetroopers.shakemytours.core.components;

import com.codetroopers.shakemytours.core.modules.HomeActivityModule;
import com.codetroopers.shakemytours.ui.activity.HomeActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(
        modules = {
                HomeActivityModule.class
        }
)
public interface HomeActivityComponent {
    void injectActivity(HomeActivity homeActivity);
}
