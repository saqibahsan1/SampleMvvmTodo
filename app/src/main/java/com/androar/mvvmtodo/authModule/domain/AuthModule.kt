package com.androar.mvvmtodo.authModule.domain


import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(ViewModelComponent::class)
interface AuthModule {

//    @Binds
//    fun bindAuthRepository(default: A): AuthRepository


}



@Module
@InstallIn(SingletonComponent::class)
interface AuthSingletonBindings {
//    @Binds
//    fun bindAuthConfigurationProvider(default: DefaultAuthConfigurationProvider): AuthConfigurationProvider
}

//@InstallIn(SingletonComponent::class)
//@Module
//class AuthServiceBinding {
//    @Provides
//    fun providesAuthService(retrofit: A): AuthService =
//        retrofit.get().create()
//}
