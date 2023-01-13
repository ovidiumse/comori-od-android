package com.ovidium.comoriod.api

import android.content.Context
import android.content.pm.PackageManager
import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor(private val context: Context) : Interceptor {

    private val userAgent: String by lazy {
        buildUserAgent(context)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.header("User-Agent", userAgent)
        return chain.proceed(builder.build())
    }

    private fun buildUserAgent(context: Context): String {
        with(context.packageManager) {
            val versionName = try {
                getPackageInfo(context.packageName, 0).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                "-"
            }

            val applicationInfo = context.applicationInfo
            val stringId = applicationInfo.labelRes
            val appName =
                if (stringId == 0) applicationInfo.nonLocalizedLabel.toString()
                else context.getString(stringId)

            val agent = System.getProperty("http.agent")

            return "$appName/$versionName $agent"
        }
    }
}