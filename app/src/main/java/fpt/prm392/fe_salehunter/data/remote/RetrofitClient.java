package fpt.prm392.fe_salehunter.data.remote;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit mainClient;
    private static Retrofit upcItemDbClient;
    private static Retrofit barcodeMonsterClient;
    private static Retrofit openFoodFactsClient;

    private static String mainClientUrl="http://127.0.0.1:5140/";
    private static String upcItemDbClientUrl="https://api.upcitemdb.com/";
    private static String openFoodFactsClientUrl="https://world.openfoodfacts.org/";

    public static Retrofit getMainInstance(){
        if(mainClient == null){
            mainClient = new Retrofit.Builder()
                    .baseUrl(mainClientUrl)
                    .client(initiateClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        return mainClient;
    }

    public static Retrofit getUpcItemDbInstance(){
        if(upcItemDbClient == null){
            upcItemDbClient = new Retrofit.Builder()
                    .client(initiateClient())
                    .baseUrl(upcItemDbClientUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        return upcItemDbClient;
    }

    public static Retrofit getOpenFoodFactsInstance(){
        if(openFoodFactsClient == null){
            openFoodFactsClient = new Retrofit.Builder()
                    .client(initiateClient())
                    .baseUrl(openFoodFactsClientUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        return openFoodFactsClient;
    }

    private static OkHttpClient initiateClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)  // Connection timeout
                .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)    // Read timeout for AI responses
                .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)    // Write timeout
                .build();
    }
}
