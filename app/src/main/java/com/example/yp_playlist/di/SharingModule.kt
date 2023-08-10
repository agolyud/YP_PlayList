package com.example.yp_playlist.di

import com.example.yp_playlist.sharing.SharingInteractor
import com.example.yp_playlist.sharing.data.ExternalNavigator
import com.example.yp_playlist.sharing.data.impl.ExternalNavigatorImpl
import com.example.yp_playlist.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val sharingModule = module {

    single<ExternalNavigator> {
        ExternalNavigatorImpl(context = get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(externalNavigator = get())
    }
}