package ru.javacat.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.javacat.data.impl.RepositoryImpl
import ru.javacat.domain.repo.Repository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsRepository(impl: RepositoryImpl): Repository
}