package com.attitudetech.pawsroom.network;

import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String TOKEN = "token";
    static final String BASE_URL = "http://eu.pawtrails.pet/api/";
    private static Retrofit retrofit = null;
    private static ConcurrentHashMap<String, String> mapHeaders;

    public static Retrofit getRetrofitClient() {
        if (retrofit == null) {
            OkHttpClient httpClient = initOkHttpClient();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build();
        }
        return retrofit;
    }

    static OkHttpClient initOkHttpClient() {
        //TODO remove before generate the apk
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient()
                .newBuilder()
                .addInterceptor(ApiClient::addHeaders)
                //TODO remove before generate the apk
                .addInterceptor(chain -> {
                    Request request = chain.request();

                    Response response = chain.proceed(request);
                    String rawJson = response.body().string();

                    Log.e("Json Response", String.format("Url is: %s", response.request().url().toString()));
                    Log.e("Json Response", String.format("JSON header is: %s", response.request().headers().toString()));
                    Log.e("Json Response", String.format("JSON body is: %s", bodyToString(response.request().body())));
                    Log.e("Json Response", String.format("JSON response is: %s", rawJson));

                    // Re-create the response before returning it because body can be read only once
                    return response.newBuilder()
                            .body(ResponseBody
                                    .create(response
                                            .body()
                                            .contentType(), rawJson))
                            .build();
                })
                .addInterceptor(httpLoggingInterceptor)
                .build();
    }

    private static Response addHeaders(Interceptor.Chain chain) throws IOException {
        Request.Builder builder = chain
                .request()
                .newBuilder();

        if(mapHeaders != null) {
            for (String key : mapHeaders.keySet()) {
                builder.addHeader(key, mapHeaders.get(key));
            }
        }
        return chain.proceed(builder.build());
    }

    public static void addHeader(String key, String value) {
        if (key == null || value == null)
            return;
        if(mapHeaders == null) {
            mapHeaders = new ConcurrentHashMap<>();
        }
        mapHeaders.put(key, value);
    }

    public static void removeHeader(String key) {
        if (key == null)
            return;
        if (mapHeaders != null) {
            if(mapHeaders.containsKey(key)) {
                mapHeaders.remove(key);
            }
        }
    }

    public static String getHeaderValue(String key){
        if(mapHeaders != null && mapHeaders.containsKey(key)){
            return mapHeaders.get(key);
        }
        return null;
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}