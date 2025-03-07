-keepattributes SourceFile,LineNumberTable

-keep class com.geetest.sdk.**{*;}

-keep class char46.auth.**{*;}

-dontwarn com.geetest.sdk.**

-dontwarn com.google.errorprone.annotations.Immutable
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn "org.conscrypt.Conscrypt$Version"
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
