package com.github.contacter

import android.app.Application

class MyApplication: Application() {
    lateinit var viewModel: ContactViewModel

    override fun onCreate() {
        super.onCreate()
        viewModel = ContactViewModel(this.applicationContext.contentResolver)
    }
}