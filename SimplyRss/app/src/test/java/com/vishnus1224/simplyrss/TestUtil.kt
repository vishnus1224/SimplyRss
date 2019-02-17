package com.vishnus1224.simplyrss

import com.vishnus1224.simplyrss.util.StringProvider
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runners.model.Statement
import org.mockito.Mockito

fun makeStringProviderWith(map: Map<Int, String>): StringProvider = { stringId ->
    map[stringId] ?: "test string"
}

inline fun <reified T> T.mock() = Mockito.mock(T::class.java)

val testSchedulersRule = TestRule { base, _ ->
    object : Statement() {
        override fun evaluate() {
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

            try {
                base.evaluate()
            } finally {
                RxJavaPlugins.reset()
                RxAndroidPlugins.reset()
            }
        }

    }
}