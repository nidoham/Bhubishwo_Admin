package com.nidoham.bhubishwo.admin.di

import com.google.firebase.firestore.FirebaseFirestore
import com.nidoham.bhubishwo.admin.App
import com.nidoham.bhubishwo.admin.data.repository.ResourceRepository
import com.nidoham.bhubishwo.admin.imgbb.ImgbbRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideResourceRepository(firestore: FirebaseFirestore): ResourceRepository {
        return ResourceRepository(firestore)
    }

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideImgbbRepository(): ImgbbRepository {
        return ImgbbRepository(App.IMGBB_API_KEY)
    }
}