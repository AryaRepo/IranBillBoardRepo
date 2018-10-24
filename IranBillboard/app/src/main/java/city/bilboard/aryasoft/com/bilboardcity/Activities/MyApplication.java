package city.bilboard.aryasoft.com.bilboardcity.Activities;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.orm.SugarContext;

import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.ServiceGenerator;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.SharedPreferencesHelper;
import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyApplication extends Application
{
    private static RequestApi retrofitRequestApi = null;
    private static Context context;
    @Override
    public void onCreate()
    {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iransans.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        context = this;
        SugarContext.init(context);
        SharedPreferencesHelper.InitPreferences(context);
    }

    public static RequestApi GetRetrofitRequestApi()
    {
        if (retrofitRequestApi == null)
        {
            retrofitRequestApi = ServiceGenerator.createService(RequestApi.class);
        }
        return retrofitRequestApi;
    }
}
