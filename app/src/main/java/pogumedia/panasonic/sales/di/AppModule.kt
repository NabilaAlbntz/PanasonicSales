package pogumedia.panasonic.sales.di

import android.app.Application
import android.content.Context
import pogumedia.panasonic.sales.db.SessionManager
import pogumedia.panasonic.sales.helper.dialog.LoadingDialog
import pogumedia.panasonic.sales.helper.util.CheckNetworkConnection
import pogumedia.panasonic.sales.helper.util.ToastUtil
import dagger.Module
import dagger.Binds
import dagger.Provides


@Module
abstract class AppModule {

    @Binds
    abstract fun provideContext(application: Application): Context

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideSessionManager(context: Context): SessionManager {
            return SessionManager(context)
        }


        @JvmStatic
        @Provides
        fun provideToastUtil(context: Context): ToastUtil {
            return ToastUtil(context)
        }

        @JvmStatic
        @Provides
        fun provideCheckNetworkConnection(context: Context): CheckNetworkConnection {
            return CheckNetworkConnection(context)
        }

        @JvmStatic
        @Provides
        fun provideLoadingDialog(): LoadingDialog {
            return LoadingDialog()
        }

    }


}