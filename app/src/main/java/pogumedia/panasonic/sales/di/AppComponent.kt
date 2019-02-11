package pogumedia.panasonic.sales.di

import android.app.Application
import pogumedia.panasonic.sales.Core
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton
import dagger.BindsInstance


@Singleton
@Component(modules =
[(AppModule::class),
    (ActivityBuilder::class),
    (AndroidSupportInjectionModule::class),
    (NetworkModule::class),
    (RoomModule::class)
])

interface AppComponent : AndroidInjector<DaggerApplication> {

    fun inject(app: Core)

    override fun inject(instance: DaggerApplication?)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }


}