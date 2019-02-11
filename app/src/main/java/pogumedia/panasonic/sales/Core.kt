package pogumedia.panasonic.sales

import android.content.Context
import android.support.multidex.MultiDex
import com.facebook.stetho.Stetho
import com.google.firebase.FirebaseApp
import pogumedia.panasonic.sales.di.DaggerAppComponent

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication




open class Core : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent.inject(this)
        return appComponent
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG)
            initStetho()
    }

    private fun initStetho() {
        Stetho.initializeWithDefaults(this)
    }


}
