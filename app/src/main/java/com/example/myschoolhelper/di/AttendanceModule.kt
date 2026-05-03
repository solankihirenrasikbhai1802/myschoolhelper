package com.example.myschoolhelper.di

import android.content.Context
import com.example.myschoolhelper.data.remote.AttendanceApi
import com.example.myschoolhelper.data.remote.RetrofitClient
import com.example.myschoolhelper.repository.AttendanceRepository
import com.example.myschoolhelper.viewmodel.AttendanceViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AttendanceModule {

    @Provides
    @Singleton
    fun provideAttendanceApi(): AttendanceApi {
        // Using existing RetrofitClient - create attendance API without session manager
        return RetrofitClient.createAttendanceApi()
    }

    @Provides
    @Singleton
    fun provideAttendanceRepository(attendanceApi: AttendanceApi): AttendanceRepository {
        return AttendanceRepository(attendanceApi)
    }

    @Provides
    @Singleton
    fun provideAttendanceViewModel(repository: AttendanceRepository): AttendanceViewModel {
        return AttendanceViewModel(repository)
    }
}
