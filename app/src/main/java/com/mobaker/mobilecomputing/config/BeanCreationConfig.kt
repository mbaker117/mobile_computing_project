package com.mobaker.mobilecomputing.config

import com.mobaker.mobilecomputing.services.ITaskService
import com.mobaker.mobilecomputing.services.IUserService
import com.mobaker.mobilecomputing.services.impl.FirebaseTaskService
import com.mobaker.mobilecomputing.services.impl.FirebaseUserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

class BeanCreationConfig {

    @Module
    @InstallIn(SingletonComponent::class)
    object UserServiceModule {
        @Provides
        @Singleton
        fun userService(): IUserService {
            return FirebaseUserService()
        }
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object TaskServiceModule {
        @Provides
        @Singleton
        fun taskService(): ITaskService {
            return FirebaseTaskService()
        }
    }

}