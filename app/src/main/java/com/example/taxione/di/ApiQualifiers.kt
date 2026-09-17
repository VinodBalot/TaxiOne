package com.example.taxione.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BookApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AQIApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BigDataCloudApi