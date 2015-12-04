package com.codetroopers.materialAndroidBootstrap;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

public abstract class RecreateActivityTest<T extends Activity> {
    @Rule
    public ActivityTestRule<T> rule;

    protected RecreateActivityTest(Class<T> clazz) {
        rule = new ActivityTestRule<>(clazz, true, false);
    }

    @Test
    public void testRecreate() {
        launchActivity();

        final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        instrumentation.runOnMainSync(() -> rule.getActivity().recreate());
        instrumentation.waitForIdleSync();
    }

    /**
     * To override when a specific intent has to be given to the activity
     */
    protected void launchActivity() {
        rule.launchActivity(null);
    }
}
