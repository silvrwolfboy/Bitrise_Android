package io.stanwood.bitrise.di

import android.preference.PreferenceManager
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import io.stanwood.bitrise.BuildConfig
import io.stanwood.bitrise.R
import io.stanwood.bitrise.data.net.BitriseService
import io.stanwood.bitrise.navigation.Navigator
import io.stanwood.bitrise.util.gson.GsonDateFormatAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router
import java.util.*


val applicationModule = applicationContext {

    /**
     * Gson
     */
    provide {
        GsonBuilder()
            .registerTypeAdapter(Date::class.java, GsonDateFormatAdapter(BuildConfig.API_DATE_TIME_FORMAT))
            .create()
    }

    /**
     * OkHttpClient
     */
    provide {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient
                .Builder()
                .addInterceptor(interceptor)
                .build()
    }

    /**
     * Retrofit
     */
    provide {
        Retrofit
                .Builder()
                .baseUrl(BuildConfig.BITRISE_API_BASE_URL)
                .client(get())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(get()))
                .build()
    }

    /**
     * BitriseService
     */
    provide { get<Retrofit>().create(BitriseService::class.java) }

    /**
     * Cicerone
     */
    provide { Cicerone.create() }

    /**
     * NavigatorHolder
     */
    provide { get<Cicerone<Router>>().navigatorHolder }

    /**
     * Router
     */
    provide { get<Cicerone<Router>>().router }

    /**
     * Navigator
     */
    provide { Navigator(getProperty(Properties.ACTIVITY), R.id.root) }

    /**
     * SharedPreferences
     */
    provide { PreferenceManager.getDefaultSharedPreferences(androidApplication()) }
}
