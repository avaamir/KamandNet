package mp.amir.ir.kamandnet.respository.apiservice

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
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
    const val Domain = "http://app.wewi.ir/"
    private const val BASE_API_URL = Domain

    private var event = Event(Unit)
    private var onUnauthorizedListener: OnUnauthorizedListener? = null
    private var internetConnectionListener: InternetConnectionListener? = null

    private var token: String? = null

    val CLIENT: KamandClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        retrofitBuilder.build().create(KamandClient::class.java)
    }

    private lateinit var connectivityManager: ConnectivityManager


    private val retrofitBuilder: Retrofit.Builder by lazy {

        val client: OkHttpClient = OkHttpClient.Builder()
            //UnsafeOkHttpClient.getUnsafeOkHttpClientBuilder()
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
                            event =
                                Event(
                                    Unit
                                )
                        }
                        onUnauthorizedListener?.onUnauthorizedAction(event)
                    }
                })
                addInterceptor(object : NetworkConnectionInterceptor() {
                    override fun isInternetAvailable(): Boolean {
                        return isNetworkAvailable()
                    }

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
        Retrofit.Builder()
            .baseUrl(BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
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


    fun isNetworkAvailable(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }


    fun setContext(context: Context) {
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    interface OnUnauthorizedListener {
        fun onUnauthorizedAction(event: Event<Unit>)
    }

    interface InternetConnectionListener {
        fun onInternetUnavailable()
    }


}