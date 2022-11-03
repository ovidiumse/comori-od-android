package com.ovidium.comoriod.utils

import com.ovidium.comoriod.BuildConfig
import io.fusionauth.jwt.domain.JWT
import io.fusionauth.jwt.ec.ECSigner
import java.time.ZoneOffset
import java.time.ZonedDateTime


class JWTUtils {
    private val signer by lazy { ECSigner.newSHA512Signer(BuildConfig.JWTPrivateKey) }

    fun buildToken(userId: String?, issuer: String): String {
        val now = ZonedDateTime.now(ZoneOffset.UTC)

        val jwt = JWT().setIssuer(issuer).setIssuedAt(now).setSubject(userId)
            .setExpiration(now.plusMinutes(20))

        return JWT.getEncoder().encode(jwt, signer)
    }
}