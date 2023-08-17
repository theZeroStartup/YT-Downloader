package com.zero.mp3ytdownloader.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.zero.mp3ytdownloader.datasource.local.SharedPrefDelegate
import com.zero.mp3ytdownloader.datasource.local.SharedPrefDelegateImpl
import com.zero.mp3ytdownloader.datasource.local.SharedPreferenceUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SharedPreferenceModule {

    //If you need normal shared pref
    @Provides
    @Singleton
    fun provideSharedPreference(app: Application): SharedPreferences{
        return app.getSharedPreferences("Shared_Pref", MODE_PRIVATE)
    }

    //If you need encrypted shared pref
    @Provides
    @Singleton
    fun provideSharedPreferenceUtils(app: Application): SharedPreferenceUtils {
        return SharedPreferenceUtils(applicationContext = app)
    }

    @Provides
    @Singleton
    fun provideSharedPrefImpl(sharedPreferenceUtils: SharedPreferenceUtils): SharedPrefDelegate {
        return SharedPrefDelegateImpl(sharedPreferenceUtils)
    }
}