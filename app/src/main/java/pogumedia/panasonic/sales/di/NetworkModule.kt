package pogumedia.panasonic.sales.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import pogumedia.panasonic.sales.BuildConfig
import pogumedia.panasonic.sales.helper.util.Constants
import pogumedia.panasonic.sales.helper.util.DateUtil
import pogumedia.panasonic.sales.service.api.ApiInterface
import pogumedia.panasonic.sales.service.api.EncryptionApi
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.serializeNulls()
        gsonBuilder.setDateFormat(DateUtil.FORMAT_ONE)
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    fun provideRequestInterceptor(): EncryptionApi {
        return EncryptionApi()
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(encryptionApi: EncryptionApi): OkHttpClient {
        val interceptors = HttpLoggingInterceptor()
        interceptors.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
        client.readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request()
                    val requestBuilder = request.newBuilder()
                    requestBuilder.header(Constants.PARAM_HEADER, encryptionApi.SCREET_KEY)
                            .header(Constants.PARAM_HEADER_TOKEN, encryptionApi.crypt())
                            .header(Constants.PARAM_HEADER_TIME, encryptionApi.token_time)
                            .header(Constants.PARAM_HEADER_USER_AGENT, EncryptionApi.KEY_USER_AGENT)

                    chain.proceed(requestBuilder.build())
                }
        if (BuildConfig.DEBUG)
            client.addInterceptor(interceptors)

        return client.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .baseUrl("http://www.pogumedia-pnt.com/pogumedia/public/api/")
                .client(okHttpClient)
                .build()
    }

    @Provides
    @Singleton
    fun provideApiInterface(retrofit: Retrofit): ApiInterface {
        return retrofit.create<ApiInterface>(ApiInterface::class.java)
    }

}