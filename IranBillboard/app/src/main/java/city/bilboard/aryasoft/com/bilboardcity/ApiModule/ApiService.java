package  city.bilboard.aryasoft.com.bilboardcity.ApiModule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService
{
  //  private static  String Base_URL="http://billboard.moblemalayer.com/api/";
    private static  String Base_URL="http://billboard.mobleabrisham.ir/api/";
    private static Retrofit retrofit = null;
    private static Gson gson = null;
    private static OkHttpClient okHttpClient = null;
    public static <ServiceRequestTypeSafe> ServiceRequestTypeSafe createService(Class<ServiceRequestTypeSafe> serviceClass)
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
                    .baseUrl(Base_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson));
            retrofit = builder.build();
        }
        return retrofit.create(serviceClass);

    }
}
