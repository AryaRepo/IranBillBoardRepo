package city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import city.bilboard.aryasoft.com.bilboardcity.Models.CityModel;
import city.bilboard.aryasoft.com.bilboardcity.Models.StateCityModel;
import city.bilboard.aryasoft.com.bilboardcity.R;

public class HelperModule
{
    private static Dialog MainDlg = null;

    public static void InternetDisconnectedDialog(final Context AppContext)
    {
        MainDlg = null;
        android.support.v7.app.AlertDialog.Builder InternetAlert = new android.support.v7.app.AlertDialog.Builder(AppContext);
        InternetAlert.setCancelable(false);
        View AlertView = View.inflate(AppContext, R.layout.internet_dialog, null);
        Button Internet_btn_WIFI = (Button) AlertView.findViewById(R.id.Internet_btn_WIFI);
        Button Internet_btn_Mobile = (Button) AlertView.findViewById(R.id.Internet_btn_Mobile);
        InternetAlert.setView(AlertView);
        Internet_btn_Mobile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //--------------Do Something...

                Intent DATAIntent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                AppContext.startActivity(DATAIntent);
                MainDlg.dismiss();
                System.exit(0);

            }
        });
        Internet_btn_WIFI.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent WIFIIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                AppContext.startActivity(WIFIIntent);
                MainDlg.dismiss();
                System.exit(0);
            }
        });
        InternetAlert.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                {
                    dialog.dismiss();
                    ShowExitAppDialog(AppContext);
                }
                return false;
            }
        });
        //--------------------
        MainDlg = InternetAlert.show();
    }

    public static boolean IsFillValidation(EditText... Views)
    {
        int TrueCounter=0;
        boolean Result = false;
        for (EditText itemView : Views)
        {
            if( !(itemView.getText().toString().trim().isEmpty()))
            {
                ++TrueCounter;
            }
        }
        if(TrueCounter==Views.length)
        {
            Result=true;
        }
        return Result;
    }

    public static void OpenCommonDialog(Context AppContext, String Title, String Message, String PositiveButtonText)
    {
        android.support.v7.app.AlertDialog.Builder NotifyDialogAlert = new android.support.v7.app.AlertDialog.Builder(AppContext);
        NotifyDialogAlert.setCancelable(false);
        View AlertView = View.inflate(AppContext, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = (TextView) AlertView.findViewById(R.id.txt_dialog_title);
        TextView NotifyDialogMessage = (TextView) AlertView.findViewById(R.id.Notifydialog_title);
        NotifyDialogTitle.setText(Title);
        NotifyDialogMessage.setText(Message);
        NotifyDialogAlert.setView(AlertView);

        NotifyDialogAlert.setPositiveButton(PositiveButtonText, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        //--------------------
        NotifyDialogAlert.show();
    }
    public static ArrayList<CityModel> GetCities(Context context)
    {
        ArrayList<CityModel> CityList = new ArrayList<>();
        String json = "";
        JSONArray CityArray = null;
        try
        {
            InputStream is = context.getAssets().open("cities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            //--------------------
            CityArray = new JSONArray(json);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        for (int i = 0; i < CityArray.length(); ++i)
        {
            try
            {
                JSONObject jsonObject = CityArray.getJSONObject(i);
                if (jsonObject.getInt("StateCode") == 27)
                {
                    JSONArray JACity = jsonObject.getJSONArray("City");
                    for (int j = 0; j < JACity.length(); ++j)
                    {
                        JSONObject jsonObjectCity = JACity.getJSONObject(j);
                        CityModel city = new CityModel();
                        city.CityCode = jsonObjectCity.getInt("CityCode");
                        city.CityName = jsonObjectCity.getString("CityName");
                        CityList.add(city);
                    }
                }
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return CityList;
        //-------------------------
    }
    public static void ShowLoading(final Context AppContext)
    {
        MainDlg = null;
        android.support.v7.app.AlertDialog.Builder LoadingAlert = new android.support.v7.app.AlertDialog.Builder(AppContext);
        LoadingAlert.setCancelable(false);
        View AlertView = View.inflate(AppContext, R.layout.loading_dialog, null);
        LoadingAlert.setView(AlertView);
        //--------------------
        MainDlg = LoadingAlert.show();
        //MainDlg.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        MainDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public static void NoDataReceivedDialog(final Context AppContext)
    {
        android.support.v7.app.AlertDialog.Builder NotifyDialogAlert = new android.support.v7.app.AlertDialog.Builder(AppContext);
        NotifyDialogAlert.setCancelable(false);
        View AlertView = View.inflate(AppContext, R.layout.dialog_notify, null);
        TextView NotifyDialogMessage = (TextView) AlertView.findViewById(R.id.Notifydialog_title);
        NotifyDialogMessage.setText("کاربرگرامی باوجود اینکه دستگاه شما به اینترنت وصل است اما داده ای دریافت نشد.\nممکن است مشکل ازارتباط اینترنتی یا شبکه ای باشد.");
        NotifyDialogAlert.setView(AlertView);
        NotifyDialogAlert.setNegativeButton("باشه،فهمیدم!", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                System.exit(1);
            }
        });
        NotifyDialogAlert.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                {
                    dialog.dismiss();
                    ShowExitAppDialog(AppContext);
                }
                return false;
            }
        });
        //--------------------
        NotifyDialogAlert.show();
    }

    public static void HideLoading()
    {
        if (MainDlg != null)
        {
            MainDlg.dismiss();
        }
    }

    public static String LoadJSONFromAsset(Context context)
    {
        String json = null;
        try
        {
            InputStream is = context.getAssets().open("cities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex)
        {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static ArrayList<StateCityModel> GetStateCityList(Context Context)
    {
        ArrayList<StateCityModel> StateList = new ArrayList<>();
        JSONArray CityArray ;
        String StatesJson = LoadJSONFromAsset(Context);
        try
        {
            CityArray = new JSONArray(StatesJson);

            for (int i = 0; i < CityArray.length(); ++i)
            {

                JSONObject StateCityJson = CityArray.getJSONObject(i);
                StateCityModel StateCityObj = new StateCityModel();
                StateCityObj.StateCode = StateCityJson.getInt("StateCode");
                StateCityObj.StateName = StateCityJson.getString("StateName");
                JSONArray ArrayCity = StateCityJson.getJSONArray("City");
                StateCityObj.CityCollection=new ArrayList<>();
                for (int k = 0; k < ArrayCity.length(); ++k)
                {
                    JSONObject CityJson = ArrayCity.getJSONObject(k);
                    CityModel City = new CityModel();
                    City.CityCode = CityJson.getInt("CityCode");
                    City.CityName = CityJson.getString("CityName");

                    StateCityObj.CityCollection.add(City);
                }
                StateList.add(StateCityObj);
            }
        } catch (Exception exp)
        {
        }
        //----------------------
        return StateList;
    }


    public static void ShowExitAppDialog(final Context AppContext)
    {
        android.support.v7.app.AlertDialog.Builder ExitAccountAlert = new android.support.v7.app.AlertDialog.Builder(AppContext);
        ExitAccountAlert.setCancelable(false);
        View AlertView = View.inflate(AppContext, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = (TextView) AlertView.findViewById(R.id.Notifydialog_title);
        NotifyDialogTitle.setText("کاربر گرامی آیا مایل به خروج از برنامه هستید؟");
        ExitAccountAlert.setView(AlertView);

        ExitAccountAlert.setPositiveButton("بله", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                System.exit(1);
            }
        });

        ExitAccountAlert.setNegativeButton("خیر", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        //--------------------
        ExitAccountAlert.show();
    }

    public static boolean IsUser()
    {
        boolean result;
        int UserID = SharedPreferencesHelper.ReadInt("UserID");
        result = UserID != -1;
        return result;
    }

    public static byte[] ConvertBitmapToByteArray(Bitmap YourBitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        YourBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] ConvertImageToByteArray(Intent YourData, Context context)
    {
        final long KiB = 1024;
        String Uri_Path = YourData.getDataString();
        android.database.Cursor cursor = context.getContentResolver().query(android.net.Uri.parse(Uri_Path), null, null, null, null);
        int idx = -1;
        if (cursor != null)
        {
            cursor.moveToFirst();
            idx = cursor.getColumnIndex(android.provider.MediaStore.Images.ImageColumns.DATA);
            String path = cursor.getString(idx);
            File f = new File(path);
            Bitmap bm = BitmapFactory.decodeFile(path);
            long len = (f.length() / KiB);
            cursor.close();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if(len<100)
            {
                bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            }
            else if(len>100 && len< 500)
            {
                bm.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            }
            else  if( len> 500)
            {
                bm.compress(Bitmap.CompressFormat.JPEG, 15, byteArrayOutputStream);
            }
            return byteArrayOutputStream.toByteArray();
        }
        return null;
    }

    public static void ShowSignInAlert(final Context AppContext)
    {
        android.support.v7.app.AlertDialog.Builder SignInAlert = new android.support.v7.app.AlertDialog.Builder(AppContext);
        SignInAlert.setCancelable(false);
        View AlertView = View.inflate(AppContext, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = (TextView) AlertView.findViewById(R.id.Notifydialog_title);
        NotifyDialogTitle.setText("کاربر گرامی شما کاربر مهمان هستید ، برای استفاده از امکانات برنامه باید ثبت نام کنیدیا اینکه ورود به سیستم کنید.");
        SignInAlert.setView(AlertView);

        SignInAlert.setPositiveButton("متوجه شدم", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        //--------------------
        SignInAlert.show();
    }

    private static boolean Result = false;

    public static boolean DeleteFavorite(final Context AppContext)
    {
        android.support.v7.app.AlertDialog.Builder DeleteFavoriteAlert = new android.support.v7.app.AlertDialog.Builder(AppContext);
        DeleteFavoriteAlert.setCancelable(false);
        View AlertView = View.inflate(AppContext, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = (TextView) AlertView.findViewById(R.id.Notifydialog_title);
        NotifyDialogTitle.setText("کاربر گرامی آیا مایل به حذف از لیست علاقمندی ها هستید؟");
        DeleteFavoriteAlert.setView(AlertView);
        DeleteFavoriteAlert.setPositiveButton("بله", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Result = true;
                //  dialog.dismiss();
            }
        });

        DeleteFavoriteAlert.setNegativeButton("خیر", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Result = false;
                // dialog.dismiss();
            }
        });
        //--------------------
        DeleteFavoriteAlert.show();
        return Result;
    }

    public static String arabicToDecimal(String number)
    {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++)
        {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
            {
                ch -= 0x0660 - '0';
            }
            else if (ch >= 0x06f0 && ch <= 0x06F9)
            {
                ch -= 0x06f0 - '0';
            }
            chars[i] = ch;
        }
        return new String(chars);
    }
    public static String stripNonDigits(final CharSequence input)
    {
        final StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++)
        {
            final char c = input.charAt(i);
            if (c > 47 && c < 58)
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
