package com.kevlarcodes.whocalled;

import static android.app.Instrumentation.ActivityResult;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.kevlarcodes.whocalled.view.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CallLogFragmentPlaceCallTest {

    /*This test demonstrates Espresso Intents using the IntentsTestRule, a class that extends
      ActivityTestRule.*/
    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal())).respondWith(new ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void clickCallButton_SendsPhoneNumber() {

        onView(withId(R.id.call_list)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, MyViewAction.clickChildViewWithId(R.id.call_button)));
        // intended(Matcher<Intent> matcher) asserts the given matcher matches one and only one
        // intent sent by the application.
        intended(allOf(
                hasAction(Intent.ACTION_DIAL),
                hasData(Uri.parse("tel:6505551212"))
                ));
    }

    @Test  //Future test, should fail at this time
    public void clickCallLog_OpensDetailsActivity() {

        onView(withId(R.id.call_list)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, MyViewAction.clickChildViewWithId(R.id.call_contact)));

        Intents.init();
        intended(hasComponent("NumberDetailsActivity"));
        Intents.release();
    }

}
