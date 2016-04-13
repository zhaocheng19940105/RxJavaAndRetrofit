package com.alaske.demo.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.alaske.demo.App;
import com.alaske.demo.config.EnvConfig;
import com.alaske.demo.cookie.ClearableCookieJar;
import com.alaske.demo.cookie.PersistentCookieJar;
import com.alaske.demo.cookie.cache.SetCookieCache;
import com.alaske.demo.cookie.presistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author: zhaocheng
 * Date: 2016-04-12
 * Time: 17:44
 * Name:ApiManager
 * Introduction:
 */
public class ApiManager {

    private static final String RESP_CACHE = "RespCache";

    /**
     *    为处理之后的不同的Api  WebApiService 假设之后加上PayApiService等..
     */
    private   HashMap<Class, Retrofit> SERVICE_RETROFIT_BIND = new HashMap<>();


    private ConcurrentHashMap<Class, Object> cachedApis = new ConcurrentHashMap<>();

    private Retrofit retrofit_webapi;

    private ConcurrentHashMap<Class,Object> cacheApis = new ConcurrentHashMap<>();

    public ApiManager(){
        // init cookie manager
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(),new SharedPrefsCookiePersistor(App.getApp()));

        //init okHttp 3 logger
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // init cache
        File httpCacheDirectory = new File(App.getApp().getExternalCacheDir(),RESP_CACHE);

        //init client
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .cache(new Cache(httpCacheDirectory,20*1024*1024))
                .addInterceptor(loggingInterceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .addNetworkInterceptor(mCommonInfoInterceptor)
                .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .authenticator(mAuthenticator)
                .build();


        Gson gson_payapi = new GsonBuilder()
                .create();
        retrofit_webapi = new Retrofit.Builder()
                .baseUrl(EnvConfig.getWebApiBaseUrl())
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson_payapi))
                .build();

        SERVICE_RETROFIT_BIND.put(WebApiService.class,retrofit_webapi);

    }


    public <T> T getService(Class<T> clz) {
        Object obj = cachedApis.get(clz);
        if (obj != null) {
            return (T) obj;
        } else {
            Retrofit retrofit = SERVICE_RETROFIT_BIND.get(clz);
            if (retrofit != null) {
                T service = retrofit.create(clz);
                cachedApis.put(clz, service);
                return service;
            } else {
                return null;
            }
        }
    }

    Authenticator mAuthenticator = new Authenticator() {
        @Override
        public Request authenticate(Route route, Response response)
                throws IOException { // 遇上401之后的refreshtoken
            //Your.sToken = service.refreshToken();
            return response.request().newBuilder()
                    .build();
        }
    };


    Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl())
                    .build();
        }
    };

    Interceptor mCommonInfoInterceptor = new Interceptor() {
        @Override public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Map<String, String> added = new HashMap<>();
            Request newRequest = interceptFormBody(request, added);
            newRequest = newRequest.newBuilder()
                    .build();
            return chain.proceed(newRequest);
        }
    };

    public static Request interceptFormBody(Request request, Map<String, String> added)
            throws IOException {

        RequestBody requestBody = request.body();

        if (requestBody instanceof FormBody) {
            FormBody formBody = (FormBody) requestBody;
            FormBody.Builder formBuilder = new FormBody.Builder();
            if (formBody.size() > 0) {
                for (int idx = 0; idx < formBody.size(); idx++) {
                    formBuilder.addEncoded(formBody.encodedName(idx),
                            formBody.encodedValue(idx));
                }
            }
            if (added != null && added.size() > 0) {
                for (Map.Entry<String, String> entry : added.entrySet()) {
                    formBuilder.add(entry.getKey(), entry.getValue());
                }
            }
            return request.newBuilder().post(formBuilder.build()).build();
        } else if (requestBody instanceof MultipartBody){
            MultipartBody multipartBody = (MultipartBody) requestBody;
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
            if (multipartBody.size() > 0){
                for (int idx = 0; idx < multipartBody.size(); idx ++){
                    multipartBuilder.addPart(multipartBody.part(idx));
                }
            }
            if (added != null && added.size() > 0) {
                for (Map.Entry<String, String> entry: added.entrySet()){
                    multipartBuilder.addFormDataPart(
                            entry.getKey(), null,
                            RequestBody.create(
                                    MediaType.parse("text/plain; charset=UTF-8"), entry.getValue()) );
                }
            }
            multipartBody = multipartBuilder.build();
            return request.newBuilder().post(multipartBody)  // need update boundary
                    .header("Content-Type", "multipart/form-data; boundary="+multipartBody.boundary())
                    .build();
        }else {
            //RequestBody
            StringBuilder sbParam = new StringBuilder("");
            if (added != null && added.size() > 0) {
                for (Map.Entry<String, String> entry: added.entrySet()) {
                    sbParam.append("&")
                            .append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue()));
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Sink sink = Okio.sink(baos);
                BufferedSink bufferedSink = Okio.buffer(sink);

                /**
                 * Write old params
                 * */
                request.body().writeTo(bufferedSink);

                /**
                 * write to buffer additional params
                 * */
                bufferedSink.writeString(sbParam.toString(), Charset.defaultCharset());

                RequestBody newRequestBody = RequestBody.create(
                        request.body().contentType(),
                        bufferedSink.buffer().readUtf8()
                );

                return request.newBuilder().post(newRequestBody).build();
            }

        }

        return request;
    }

    private String cacheControl() {
        String cacheHeaderValue;
        if (networkStatusOK(App.getApp())) {
            int maxAge = 33; // read from cache for 1 minute
            cacheHeaderValue = "public, max-age=" + maxAge;
        } else {
            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
            cacheHeaderValue = "public, only-if-cached, max-stale=" + maxStale;
        }
        return cacheHeaderValue;
    }

    public static boolean networkStatusOK(Context context) {
        boolean netStatus = false;
        try {
            ConnectivityManager e = (ConnectivityManager)context.getSystemService("connectivity");
            NetworkInfo activeNetworkInfo = e.getActiveNetworkInfo();
            if(activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected()) {
                netStatus = true;
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return netStatus;
    }

}
