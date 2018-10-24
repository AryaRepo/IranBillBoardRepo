package city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MrBinary on 2/2/2018.
 */

public class ServiceGenerator
{
    private static Retrofit retrofit = null;
    private static Gson gson = null;
  //  private static final String API_BASE_URL = "http://billboard.moblemalayer.com/api/";
    private static  String API_BASE_URL="http://billboard.mobleabrisham.ir/api/";
    private static
    OkHttpClient okHttpClient = null;

//    private static Retrofit.Builder builder = new Retrofit.Builder()
//            .client(okHttpClient)
//            .baseUrl(API_BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson));


    public static <S> S createService(Class<S> serviceClass)
    {
        if (okHttpClient == null)
        {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(35, TimeUnit.SECONDS)
                    .writeTimeout(35, TimeUnit.SECONDS)
                    .readTimeout(35, TimeUnit.SECONDS).build();
        }
        if (gson == null)
        {
            gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();
        }
        if (retrofit == null)
        {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson));
            retrofit = builder.build();
        }
        return retrofit.create(serviceClass);
    }

}
