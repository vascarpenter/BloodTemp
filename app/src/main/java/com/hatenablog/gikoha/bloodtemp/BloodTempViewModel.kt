package com.hatenablog.gikoha.bloodtemp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class BloodTempViewModel @Inject constructor() : ViewModel()
{
    private val _buttontitle = MutableLiveData("SUBMIT")
    val buttontitle: LiveData<String> = _buttontitle

    private val _items = MutableStateFlow<List<BloodTemp>?>(null)
    val state = _items.map {
        BloodTempViewState(it)
    }

    fun changeTitle(title: String)
    {
        _buttontitle.value = title
    }

    fun clearData()
    {
        _items.value = null
    }

    fun loadData(callback: () -> Unit)
    {
        val getapi = apiBuilder().create(BloodTempGetAPI::class.java)

        // repo access is suspended function, so run in CoroutineScope

        viewModelScope.launch {

            try {
                val response = getapi.getItems()
                if (response.isSuccessful) {
                    // success
                    val data = response.body()!!
                    _items.update { data.toList() }
                    callback()
                } else {
                    Log.e("Error", response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e("Error", "connect error " + e.message)
            }

        }

    }

    fun postData(temp: String, memo: String, callback: () -> Unit)
    {
        val postapi = apiBuilder().create(BloodTempPostAPI::class.java)

        val d = BloodTempPost(BuildConfig.bloodapikey, temp, memo)

        viewModelScope.launch {
            try {

                // repo access is suspended function, so run in CoroutineScope
                val response = postapi.postItem(d)
                if (response.isSuccessful) {
                    // success
                    callback()
                } else {
                    Log.e("Error", response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e("Error", "connect error " + e.message)
            }

        }
    }

    // api builder utility function for retrofit

    private fun apiBuilder(): Retrofit
    {
        // access API
        val client = buildOkHttp()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.bloodserverurl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    // okhttp build utility function

    private fun buildOkHttp(): OkHttpClient
    {
        val client = OkHttpClient.Builder()
        client.connectTimeout(20, TimeUnit.SECONDS)
        client.readTimeout(15, TimeUnit.SECONDS)
        client.writeTimeout(15, TimeUnit.SECONDS)
        return client.build()
    }

}
