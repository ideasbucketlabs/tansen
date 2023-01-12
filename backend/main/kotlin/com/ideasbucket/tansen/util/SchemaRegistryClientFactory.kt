/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.util

import io.netty.handler.ssl.SslContextBuilder
import io.netty.resolver.DefaultAddressResolverGroup
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.util.ResourceUtils
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.io.FileInputStream
import java.io.IOException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory

object SchemaRegistryClientFactory {

    @Throws(
        NoSuchAlgorithmException::class,
        IOException::class,
        KeyStoreException::class,
        CertificateException::class,
        UnrecoverableKeyException::class
    )
    @JvmStatic
    fun getClient(
        trustStoreLocation: String,
        trustStorePassword: String,
        keyStoreLocation: String,
        keyStorePassword: String
    ): WebClient.Builder {
        if (trustStorePassword.isBlank() || trustStoreLocation.isBlank()) {
            return WebClient.builder()
        }
        val contextBuilder = SslContextBuilder.forClient()

        val trustStore = KeyStore.getInstance("JKS")
        trustStore.load(FileInputStream(ResourceUtils.getFile(trustStoreLocation)), trustStorePassword.toCharArray())

        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(trustStore)

        contextBuilder.trustManager(trustManagerFactory)

        if (keyStoreLocation.isNotBlank() && keyStorePassword.isNotBlank()) {
            val keyStore: KeyStore = KeyStore.getInstance("JKS")
            keyStore.load(FileInputStream(ResourceUtils.getFile(keyStoreLocation)), keyStorePassword.toCharArray())
            val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(keyStore, keyStorePassword.toCharArray())
            contextBuilder.keyManager(keyManagerFactory)
        }

        val context = contextBuilder.build()

        return WebClient.builder()
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.create().secure {
                        it.sslContext(context)
                    }.resolver(DefaultAddressResolverGroup.INSTANCE)
                )
            )
    }
}
