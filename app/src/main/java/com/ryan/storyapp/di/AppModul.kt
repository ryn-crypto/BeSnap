//package com.ryan.storyapp.di
//
//import android.content.Context
//import com.ryan.storyapp.ui.main.MainActivity
//import com.ryan.storyapp.utils.SharedPreferencesManager
//import dagger.Component
//import dagger.Module
//import dagger.Provides
//
//@Module
//class SharedPreferencesModule(private val context: Context) {
//
//    @Provides
//    fun provideSharedPreferencesManager(): SharedPreferencesManager {
//        return SharedPreferencesManager(context)
//    }
//}
//
//@Component(modules = [SharedPreferencesModule::class])
//interface AppComponent {
//    fun inject(mainActivity: MainActivity)
//    // Jika Anda perlu menginjeksikan SharedPreferencesManager ke activity lain, tambahkan di sini.
//}
