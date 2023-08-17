package com.zero.mp3ytdownloader.di

import android.app.Application
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PythonModule {

    @Provides
    @Singleton
    fun providesPython(app: Application): Python {
        if (!Python.isStarted())
            Python.start(AndroidPlatform(app.applicationContext))
        return Python.getInstance()
    }

}