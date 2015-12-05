package com.codetroopers.shakemytours.core.components;

import com.codetroopers.shakemytours.core.modules.AndroidModule;
import com.codetroopers.shakemytours.core.modules.ApplicationModule;
import com.codetroopers.shakemytours.core.modules.HomeActivityModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                ApplicationModule.class,
                AndroidModule.class
        }
)
public interface ApplicationComponent {
    HomeActivityComponent homeActivityComponent(HomeActivityModule homeActivityModule);
}