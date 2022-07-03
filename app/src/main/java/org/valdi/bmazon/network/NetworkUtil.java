package org.valdi.bmazon.network;

import org.valdi.bmazon.model.AuthProvider;
import org.valdi.bmazon.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

public class NetworkUtil {

    public static RetrofitInterface getRetrofit() {
        final RxJavaCallAdapterFactory adapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        return new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addCallAdapterFactory(adapter)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitInterface.class);

    }

    public static RetrofitInterface getRetrofit(final AuthProvider provider) {
        final OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(chain -> {
            final Request original = chain.request();
            final Request.Builder builder = original.newBuilder()
                    .addHeader("x-access-token", provider.getToken())
                    .method(original.method(), original.body());
            return chain.proceed(builder.build());
        });
        final RxJavaCallAdapterFactory adapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        return new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .client(client.build())
                .addCallAdapterFactory(adapter)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitInterface.class);
    }
}
