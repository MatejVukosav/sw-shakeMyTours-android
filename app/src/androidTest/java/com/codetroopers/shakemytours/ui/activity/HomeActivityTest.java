package com.codetroopers.shakemytours.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.codetroopers.shakemytours.R;
import com.codetroopers.shakemytours.RecreateActivityTest;
import com.codetroopers.shakemytours.TestComponentsRule;
import com.codetroopers.shakemytours.core.components.ApplicationComponent;
import com.codetroopers.shakemytours.core.components.ComponentsFactory;
import com.codetroopers.shakemytours.core.components.HomeActivityComponent;
import com.codetroopers.shakemytours.core.modules.ForApplication;
import com.codetroopers.shakemytours.core.modules.HomeActivityModule;
import com.codetroopers.shakemytours.example.DummyContent;
import com.codetroopers.shakemytours.example.DummyContentFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Date;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.close;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeActivityTest extends RecreateActivityTest<HomeActivity> {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestComponentsRule testComponentsRule = new TestComponentsRule(new ComponentsTestFactory());

    @Mock
    DummyContentFactory mockDummyContentFactory;

    public HomeActivityTest() {
        super(HomeActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        DummyContent stubContent = DummyContent.create("Hello World from test!", new Date());
        when(mockDummyContentFactory.getDummyContent()).thenReturn(stubContent);
    }

    @Test
    public void testHomeActivity_exampleMockInjection() {
        rule.launchActivity(null);

        onView(withText(endsWith("Hello World from test!")))
                .check(matches(isDisplayed()));

        verify(mockDummyContentFactory).getDummyContent();
        verifyNoMoreInteractions(mockDummyContentFactory);
    }

    @Test
    public void testHomeActivity_exampleOpenMenu() {
        rule.launchActivity(null);

        onView(withText("menu 0"))
                .check(matches(not(isDisplayed())));

        onView(withId(R.id.drawer))
                .perform(open());
        onView(withText("menu 0"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.drawer))
                .perform(close());
        onView(withText("menu 0"))
                .check(matches(not(isDisplayed())));
    }


    /**********************************************************************************************/


    private class ComponentsTestFactory extends ComponentsFactory {
        @Override
        public HomeActivityComponent buildHomeActivityComponent(ApplicationComponent applicationComponent, HomeActivity homeActivity) {
            return applicationComponent.homeActivityComponent(new HomeActivityTestModule(homeActivity));
        }
    }

    /********************
     * Mocking Modules  *
     ********************/

    public class HomeActivityTestModule extends HomeActivityModule {
        public HomeActivityTestModule(Activity activity) {
            super(activity);
        }

        @Override
        protected DummyContentFactory provideDummyContentFactory(@ForApplication Context context) {
            return mockDummyContentFactory;
        }
    }
}
