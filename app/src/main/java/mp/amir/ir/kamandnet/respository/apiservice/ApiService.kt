package mp.amir.ir.kamandnet.respository.apiservice

import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import mp.amir.ir.kamandnet.BuildConfig
import mp.amir.ir.kamandnet.respository.UserConfigs
import mp.amir.ir.kamandnet.respository.apiservice.interceptors.AuthCookieInterceptor
import mp.amir.ir.kamandnet.respository.apiservice.interceptors.NetworkConnectionInterceptor
import mp.amir.ir.kamandnet.respository.apiservice.interceptors.UnauthorizedInterceptor
import mp.amir.ir.kamandnet.utils.general.Event
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiService {
    lateinit var domain: String
        private set
    private val BASE_API_URL get() = domain + "api/"

    private var event = Event(Unit)
    private var onUnauthorizedListener: OnUnauthorizedListener? = null
    private var internetConnectionListener: InternetConnectionListener? = null

    private var token: String? = null

    private lateinit var iNetworkAvailabilityImpl: NetworkConnectionInterceptor.INetworkAvailability

    var mClient: KamandClient? = null
    val client: KamandClient
        get() =
            mClient ?: makeRetrofit(BASE_API_URL).create(KamandClient::class.java)
                .also { mClient = it }

    private fun makeRetrofit(baseURL: String): Retrofit {
        val client: OkHttpClient = OkHttpClient.Builder()
            .apply {
                addInterceptor { chain ->
                    val builder = chain.request().newBuilder()
                        //   .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")

                    token?.let { token ->
                        builder.addHeader("Authorization", token)
                    }
                    val newRequest: Request = builder.build()
                    chain.proceed(newRequest)
                }

                addInterceptor(AuthCookieInterceptor())
                addInterceptor(object : UnauthorizedInterceptor() {
                    override fun onUnauthorized() {
                        if (UserConfigs.isLoggedIn) {
                            UserConfigs.logout()
                            token = null
                            event = Event(Unit)
                        }
                        onUnauthorizedListener?.onUnauthorizedAction(event)
                    }
                })
                addInterceptor(object : NetworkConnectionInterceptor(iNetworkAvailabilityImpl) {
                    override fun onInternetUnavailable() {
                        CoroutineScope(Main).launch {
                            internetConnectionListener?.onInternetUnavailable()
                        }
                    }
                })

                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }.build()

        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    @Synchronized
    fun setToken(token: String?) {
        ApiService.token = token
    }

    @Synchronized
    fun setOnUnauthorizedAction(action: OnUnauthorizedListener) {
        onUnauthorizedListener = action
    }

    @Synchronized
    fun removeUnauthorizedAction(action: OnUnauthorizedListener) {
        if (action == onUnauthorizedListener)
            onUnauthorizedListener = null
    }

    @Synchronized
    fun setInternetConnectionListener(action: InternetConnectionListener) {
        internetConnectionListener = action
    }

    @Synchronized
    fun removeInternetConnectionListener(action: InternetConnectionListener) {
        if (action == internetConnectionListener) {
            internetConnectionListener = null
        }
    }

    @Synchronized
    fun init(
        domain: String,
        iNetworkAvailability: NetworkConnectionInterceptor.INetworkAvailability
    ) {
        this.domain = domain
        mClient = null
        this.iNetworkAvailabilityImpl = iNetworkAvailability
    }

    @Synchronized
    fun changeDomain(
        domain: String,
    ) {
        this.domain = domain
        mClient = null
    }

    interface OnUnauthorizedListener {
        fun onUnauthorizedAction(event: Event<Unit>)
    }

    interface InternetConnectionListener {
        fun onInternetUnavailable()
    }


}