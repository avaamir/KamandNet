package mp.amir.ir.kamandnet.respository.apiservice.interceptors

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


abstract class NetworkConnectionInterceptor(private val iNetworkAvailability: INetworkAvailability) : Interceptor {

    abstract fun onInternetUnavailable()

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        if (!iNetworkAvailability.isInternetAvailable()) {
            onInternetUnavailable()
            request = request.newBuilder().header(
                "Cache-Control",
                "public, only-if-cached, max-stale=" + 60 * 60 * 24
            ).build()
            val response = chain.proceed(request)
            if (response.cacheResponse == null) {
                //todo cache is unavailable
                println("debug: okhttp: cache is unavailable")
            }
            return response
        }
        return chain.proceed(request)
    }

    interface INetworkAvailability {
        fun isInternetAvailable(): Boolean
    }
}