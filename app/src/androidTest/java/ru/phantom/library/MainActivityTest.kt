package ru.phantom.library

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.phantom.library.presentation.main.MainActivity

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun mainActivity_is_displayed_correctly() {
        onView(withId(R.id.mainAddButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        onView(withId(R.id.showLibraryButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        onView(withId(R.id.showGoogleBooksButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clicking_addButton_opens_BottomSheet() {
        onView(withId(R.id.mainAddButton)).perform(ViewActions.click())

        onView(withId(R.id.addBook))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        onView(withId(R.id.addDisk))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        onView(withId(R.id.addNewspaper))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        pressBack()
    }

    @Test
    fun click_google_books_button_and_search_is_displayed_correctly() {
        onView(withId(R.id.showGoogleBooksButton))
            .perform(ViewActions.click())

        onView(withId(R.id.buttonClearFiltersGoogleBooks))
            .check(ViewAssertions.matches(ViewMatchers.isNotClickable()))

        onView(withId(R.id.buttonStartSearchGoogleBooks))
            .check(ViewAssertions.matches(ViewMatchers.isNotClickable()))

        pressBack()
    }
}