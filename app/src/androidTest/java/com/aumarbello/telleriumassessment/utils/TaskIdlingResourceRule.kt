package com.aumarbello.telleriumassessment.utils

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import org.junit.runner.Description
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

/**
 * A Junit rule that registers Architecture Components' background threads as an Espresso idling
 * resource.
 *
 * Source: https://github.com/android/architecture-components-samples/blob/master/GithubBrowserSample/app/src/androidTest/java/com/android/example/github/util/TaskExecutorWithIdlingResourceRule.kt
 */
class TaskIdlingResourceRule : CountingTaskExecutorRule() {
    // give it a unique id to workaround an espresso bug where you cannot register/unregister
    // an idling resource w/ the same name.
    private val id = UUID.randomUUID().toString()
    private val idlingResource: IdlingResource = object : IdlingResource {
        override fun getName(): String {
            return "architecture components idling resource $id"
        }

        override fun isIdleNow(): Boolean {
            return this@TaskIdlingResourceRule.isIdle
        }

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
            callbacks.add(callback)
        }
    }
    private val callbacks = CopyOnWriteArrayList<IdlingResource.ResourceCallback>()
    override fun starting(description: Description?) {
        IdlingRegistry.getInstance().register(idlingResource)
        super.starting(description)
    }

    override fun finished(description: Description?) {
        drainTasks(10, TimeUnit.SECONDS)
        callbacks.clear()
        IdlingRegistry.getInstance().unregister(idlingResource)
        super.finished(description)
    }

    override fun onIdle() {
        super.onIdle()
        for (callback in callbacks) {
            callback.onTransitionToIdle()
        }
    }
}