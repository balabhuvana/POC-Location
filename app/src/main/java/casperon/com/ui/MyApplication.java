package casperon.com.ui;

import android.app.Application;

import network.ApiInterface;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bala on 11/9/16.
 */
public class MyApplication extends Application {

    //    public static final String BASE_URL = "http://demo0214632.mockable.io/";
    public static final String BASE_URL = "https://api.stackexchange.com";

    private static Retrofit retrofit = null;
    public static ApiInterface apiService;


    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService =
                    getClient().create(ApiInterface.class);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
