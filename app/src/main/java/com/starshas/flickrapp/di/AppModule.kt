package com.starshas.flickrapp.di

import android.content.Context
import androidx.room.Room
import com.starshas.flickrapp.common.AppConstants
import com.starshas.flickrapp.data.FlickrApi
import com.starshas.flickrapp.data.db.FlickrDao
import com.starshas.flickrapp.data.db.FlickrDb
import com.starshas.flickrapp.data.repositories.FlickrItemsRepository
import com.starshas.flickrapp.data.repositories.FlickrItemsRepositoryImpl
import com.starshas.flickrapp.domain.usecases.GetFlickrItemsUseCase
import com.starshas.flickrapp.domain.usecases.GetFlickrItemsUseCaseImpl
import com.starshas.flickrapp.utils.StringProvider
import com.starshas.flickrapp.utils.StringProviderImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(AppConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideFlickrDbApi(retrofit: Retrofit): FlickrApi = retrofit.create(FlickrApi::class.java)

    @Singleton
    @Provides
    fun provideFlickrItemsRepository(flickrDB: FlickrApi, flickrDao: FlickrDao): FlickrItemsRepository {
        return FlickrItemsRepositoryImpl(flickrDB, flickrDao)
    }

    @Singleton
    @Provides
    fun provideFlickrItemsUseCase(flickrRepository: FlickrItemsRepository): GetFlickrItemsUseCase {
        return GetFlickrItemsUseCaseImpl(flickrRepository)
    }

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): FlickrDb {
        return Room.databaseBuilder(
            context,
            FlickrDb::class.java,
            AppConstants.DB_NAME
        ).build()
    }

    @Provides
    fun provideFlickrDao(database: FlickrDb): FlickrDao {
        return database.getFlickrDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class BindsModule {
    @Binds
    @Singleton
    abstract fun bindStringProvider(impl: StringProviderImpl): StringProvider
}
