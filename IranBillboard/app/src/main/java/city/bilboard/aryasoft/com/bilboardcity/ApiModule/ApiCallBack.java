package  city.bilboard.aryasoft.com.bilboardcity.ApiModule;



import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class ApiCallBack<T> implements Callback<T>
{
    private static final int Retries = 3;
    private final Call<T> call;
    private int retryCount = 0;
    private Context ApiContext = null;

    public ApiCallBack(Context ApiContext, Call<T> call)
    {
        this.call = call;
        this.ApiContext = ApiContext;
    }

    @Override
    public void onFailure(Call<T> call, Throwable t)
    {
        if (t instanceof UnknownHostException)
        {
            HelperModule.InternetDisconnectedDialog(ApiContext);
        }
        else if(t instanceof SocketTimeoutException)
        {
            HelperModule.NoDataReceivedDialog(ApiContext);
        }
        else
        {
            if (retryCount++ < Retries)
            {
                retry();
               //Toast.makeText(ApiContext, "retry: "+retryCount, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void retry()
    {
        call.clone().enqueue(this);
    }
}
