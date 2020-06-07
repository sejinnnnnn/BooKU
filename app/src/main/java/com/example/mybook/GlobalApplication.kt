package com.example.mybook

import android.app.Application
import android.content.Context
import com.kakao.auth.*

class GlobalApplication:Application() {
    lateinit var instance:GlobalApplication


    fun getGlobalApplicationContext():GlobalApplication{
        if(instance == null){
            throw IllegalStateException("this application does not inherit com.kakao.GLobalAPplication")
        }
        return instance
    }
    inner class KaKaoSDKAdapter : KakaoAdapter(){
        override fun getApplicationConfig(): IApplicationConfig {
            //      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            return object:IApplicationConfig {
                override fun getApplicationContext(): Context {
                    return instance!!.getGlobalApplicationContext()
                }
            }
        }
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         * @return Session의 설정값.
         */
        override fun getSessionConfig(): ISessionConfig {
            return object : ISessionConfig {
                override fun getAuthTypes(): Array<AuthType> {
                    return arrayOf(AuthType.KAKAO_LOGIN_ALL)
                }

                override fun isUsingWebviewTimer(): Boolean {
                    return false
                }

                override fun isSecureMode(): Boolean {
                    return false
                }

                override fun getApprovalType(): ApprovalType? {
                    return ApprovalType.INDIVIDUAL
                }

                override fun isSaveFormData(): Boolean {
                    return true
                }
            }
        }//getSession
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        KakaoSDK.init(KaKaoSDKAdapter())
    }
}