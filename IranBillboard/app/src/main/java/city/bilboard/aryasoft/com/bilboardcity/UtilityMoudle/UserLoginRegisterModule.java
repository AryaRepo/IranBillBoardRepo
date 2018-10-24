package city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import city.bilboard.aryasoft.com.bilboardcity.Activities.LandActivity;
import city.bilboard.aryasoft.com.bilboardcity.Activities.LoginActivity;
import city.bilboard.aryasoft.com.bilboardcity.Activities.MyApplication;
import city.bilboard.aryasoft.com.bilboardcity.Activities.SignUpActivity;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiCallBack;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.RecoverPasswordApiModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.RoleModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.Models.SignUpModel;
import city.bilboard.aryasoft.com.bilboardcity.R;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Response;

public class UserLoginRegisterModule
{
    private Context context;
    private RequestApi requestApi;
    private SweetAlertDialog LoadingDialog;
    private String UserName;
    private String Password;
    private Timer SchedulerTask = null;
    private String StartMin = "01";
    private String StartSec = "59";
    private String ResendTimeText = "ارسال دوباره تا ";
    private EditText edt_validationCode;
    private ImageButton btn_send_again;
    private TextView txt_validation_timer;

    public UserLoginRegisterModule(Context context)
    {
        this.context = context;
        this.requestApi = MyApplication.GetRetrofitRequestApi();
        LoadingDialog = new SweetAlertDialog(this.context, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText("لطفا کمی صبر کنید...");
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
    }

    public void SetUserName(String userName)
    {
        UserName = userName;
    }

    public void SetPassword(String password)
    {
        Password = password;
    }


    public void LoginUser()
    {
        LoadingDialog.show();
        Call<Integer> LoginUser = requestApi.LoginUser(UserName, Password);
        LoginUser.enqueue(new ApiCallBack<Integer>(context, LoginUser)
        {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body() != -1 && response.body() != -2)
                    {
                        SharedPreferencesHelper.WriteString("MobileNumber",UserName);
                        SharedPreferencesHelper.WriteString("CurrentPassword",Password);
                        GetUserRole(Integer.valueOf(response.body()));
                    }
                    else if (response.body() == -1)
                    {
                        //user is not activated
                        LoadingDialog.dismiss();
                        ActiveAccountAlert();
                    }
                    else if (response.body() == -2)
                    {
                        LoadingDialog.dismiss();
                        HelperModule.OpenCommonDialog(context, "کاربر یافت نشد", "کاربر گرامی چنین کاربری در سیستم موجود نیست.(شما باید در سیستم ثبت نام شوید)", "باشه");
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t)
            {
                LoadingDialog.dismiss();
                super.onFailure(call, t);
            }
        });
    }

    public void ForgotPassword( RecoverPasswordApiModel recoverPasswordApiModel)
    {
        LoadingDialog.show();
        Call<Integer> RecoverPassword = requestApi.RecoveryPassword(recoverPasswordApiModel);
        RecoverPassword.enqueue(new ApiCallBack<Integer>(context,RecoverPassword)
        {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response)
            {
                LoadingDialog.dismiss();
                if(response.body()==2)
                {
                    Toast.makeText(context, "چنین کاربری یافت نشد", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, "رمز عبور جدید برایتان پیامک شد", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t)
            {
                LoadingDialog.dismiss();
                super.onFailure(call, t);
            }
        });
    }

    public void RegisterUser(SignUpModel NewUser)
    {
        this.SetUserName(NewUser.MobileNumber);
        this.SetPassword(NewUser.Password);
        LoadingDialog.show();
        //----
        Call<Integer> SignUpUser = requestApi.SignUpUser(NewUser);
        SignUpUser.enqueue(new ApiCallBack<Integer>(context, SignUpUser)
        {
            @Override
            public void onResponse(Call<Integer> call, final Response<Integer> response)
            {
                if (response.isSuccessful())
                {
                    //----------------------
                    SharedPreferencesHelper.WriteString("SignUpPhoneNumber", UserName);
                    SharedPreferencesHelper.WriteString("SignedStep", "1");
                    //----------------------
                    LoadingDialog.dismiss();
                    if (response.body() == 1)
                    {
                        //user EXIST but not Activated
                        ActiveAccountAlert();
                    }
                    else if (response.body() == 0)
                    {
                        //User IS EXIST
                        //User IS ACTIVE
                        android.support.v7.app.AlertDialog.Builder MyAleart = new android.support.v7.app.AlertDialog.Builder(context);
                        MyAleart.setCancelable(false);
                        View AlertView = View.inflate(context, R.layout.dialog_notify, null);
                        TextView NotifyDialogTitle = AlertView.findViewById(R.id.Notifydialog_title);
                        NotifyDialogTitle.setText("کاربر گرامی شما قبلا در سیسیتم عضو شدید لطفا از قسمت ورود وارد حساب کاربری خود شوید.");
                        MyAleart.setView(AlertView);
                        MyAleart.setPositiveButton("متوجه شدم", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });
                        //--------------------
                        MyAleart.show();
                    }
                    else if (response.body() == -2)
                    {
                        //user recommanded number not exist
                        Toast.makeText(context, "کاربر عزیز شماره معرف شما اشتباه است یا اینکه در سیستم موجود نیست.", Toast.LENGTH_LONG).show();
                    }
                    else if (response.body() == -1)
                    {
                        //Maybe exception
                        Toast.makeText(context, "کاربر عزیز در فرایند ثبت نام شما مشکلی رخ داده است.", Toast.LENGTH_LONG).show();
                    }
                    else
                    {

                        android.support.v7.app.AlertDialog.Builder NotifyDialogAlert = new android.support.v7.app.AlertDialog.Builder(context);
                        NotifyDialogAlert.setCancelable(false);
                        View AlertView = View.inflate(context, R.layout.dialog_notify, null);
                        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_title);
                        TextView NotifyDialogMessage = AlertView.findViewById(R.id.Notifydialog_title);
                        NotifyDialogTitle.setText("خوش آمدید");
                        NotifyDialogMessage.setText("کاربر عزیز ثبت نام شما با موفقیت انجام گردید.\n تا چند لحظه ی دیگر کد فعال سازی برای شما پیامک خواهد شد.\n با استفاده از آن کدمیتوانید حساب کاربری تان را فعال کنید");
                        NotifyDialogAlert.setView(AlertView);
                        NotifyDialogAlert.setPositiveButton("باشه،فهمیدم", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                                SharedPreferencesHelper.WriteInt("ActiveCode", response.body());
                                ShowValidationDialog();
                            }
                        });
                        NotifyDialogAlert.show();
                    }
                    //----------------------------------------------------------------------------------------------------------------


                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t)
            {
                LoadingDialog.dismiss();
                super.onFailure(call, t);
            }
        });
    }

    private void ActiveAccountAlert()
    {
        android.support.v7.app.AlertDialog.Builder NotifyDialogAlert = new android.support.v7.app.AlertDialog.Builder(context);
        NotifyDialogAlert.setCancelable(false);
        View AlertView = View.inflate(context, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_title);
        TextView NotifyDialogMessage = AlertView.findViewById(R.id.Notifydialog_title);
        NotifyDialogTitle.setText("فعال سازی حساب");
        NotifyDialogMessage.setText("کاربر عزیز حساب کاربری تون هنوز فعال نشده.یک پیامک حاوی کدفعال سازی ارسال میشه.");
        NotifyDialogAlert.setView(AlertView);
        NotifyDialogAlert.setPositiveButton("ممنون،فهمیدم", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                //call API
                SendActiveCode();
            }
        });
        //--------------------
        NotifyDialogAlert.show();
    }


    private void SendActiveCode()
    {
        LoadingDialog.show();
        Call<Integer> RenewActiveCode = requestApi.RenewActiveCode(UserName);
        RenewActiveCode.enqueue(new ApiCallBack<Integer>(context, RenewActiveCode)
        {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response)
            {
                SharedPreferencesHelper.WriteInt("ActiveCode", response.body());
                LoadingDialog.dismiss();
                ShowValidationDialog();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t)
            {
                LoadingDialog.dismiss();
                super.onFailure(call, t);
            }
        });
    }

    private void ReSendActiveCode()
    {
        Call<Integer> RenewActiveCode = requestApi.RenewActiveCode(UserName);
        RenewActiveCode.enqueue(new ApiCallBack<Integer>(context, RenewActiveCode)
        {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response)
            {
                SharedPreferencesHelper.WriteInt("ActiveCode", response.body());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t)
            {
                LoadingDialog.dismiss();
                super.onFailure(call, t);
            }
        });
    }

    private void ShowValidationDialog()
    {
        android.support.v7.app.AlertDialog.Builder ValidationAlert = new android.support.v7.app.AlertDialog.Builder(context);
        ValidationAlert.setCancelable(false);
        View AlertView = View.inflate(context, R.layout.dialog_validation, null);
        edt_validationCode = AlertView.findViewById(R.id.edt_validationcode_validation);
        ImageButton btn_checkValid = AlertView.findViewById(R.id.btn_checkvalid_validation);
        btn_send_again = AlertView.findViewById(R.id.btn_send_again_validation);
        txt_validation_timer = AlertView.findViewById(R.id.txt_validation_timer);
        ValidationAlert.setView(AlertView);
        btn_send_again.setEnabled(false);
        btn_send_again.setBackgroundColor(Color.parseColor("#E57373"));
        ResendPolicy();
        btn_checkValid.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (HelperModule.IsFillValidation(edt_validationCode))
                {
                    ActiveUser(Integer.parseInt(edt_validationCode.getText().toString()));
                }
                else
                {
                    Toast.makeText(context, "کد را وارد کنید.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_send_again.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (btn_send_again.isEnabled())
                {
                    ReSendActiveCode();
                    txt_validation_timer.setVisibility(View.VISIBLE);
                    btn_send_again.setBackgroundColor(Color.parseColor("#E57373"));
                    ResendPolicy();
                    btn_send_again.setEnabled(false);
                }
            }
        });
        ValidationAlert.show();
    }

    private void GetUserRole(final int UserID)
    {
        Call<RoleModel> GetUserRole = requestApi.GetUserRole(UserID);
        GetUserRole.enqueue(new ApiCallBack<RoleModel>(context, GetUserRole)
        {
            @Override
            public void onResponse(Call<RoleModel> call, Response<RoleModel> response)
            {
                SharedPreferencesHelper.WriteInt("UserID", UserID);
                SharedPreferencesHelper.WriteInt("RoleId", response.body().RoleId);
                SharedPreferencesHelper.WriteString("RoleTitle", response.body().RoleTitle);
                SharedPreferencesHelper.WriteString("SignedStep", "2");
                if (LoadingDialog.isShowing())
                {
                    LoadingDialog.dismiss();
                }
                switch (response.body().RoleId)
                {
                    case 1:
                        Toast.makeText(context, "درود آریا نرم افزار مدیر سامانه عزیز خوش آمدید.", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(context, "درود مدیر عزیز خوش آمدید.", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(context, "درود مالک عزیز خوش آمدید.", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(context, "درود کاربر ویژه عزیز خوش آمدید.", Toast.LENGTH_SHORT).show();
                        break;
                }
                context.startActivity(new Intent(context, LandActivity.class));
                ((AppCompatActivity) context).finish();
            }

            @Override
            public void onFailure(Call<RoleModel> call, Throwable t)
            {
                HelperModule.HideLoading();
                super.onFailure(call, t);
            }
        });
    }

    private void ResendPolicy()
    {
        if (SchedulerTask != null)
        {
            SchedulerTask.cancel();
            SchedulerTask = null;
        }
        StartMin = "01";
        StartSec = "59";
        txt_validation_timer.setText(ResendTimeText + StartSec + " : " + StartMin);
        SchedulerTask = new Timer();
        SchedulerTask.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                ((AppCompatActivity) context).runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (Integer.parseInt(StartSec) - 1 >= 0)
                        {
                            int s = Integer.parseInt(StartSec);
                            --s;
                            StartSec = s + "";
                            txt_validation_timer.setText(ResendTimeText + StartSec + " : " + StartMin);
                            if (s == 0)
                            {
                                if (Integer.parseInt(StartMin) - 1 >= 0)
                                {
                                    int m = Integer.parseInt(StartMin);
                                    --m;
                                    StartMin = m + "";
                                    txt_validation_timer.setText(ResendTimeText + StartSec + " : " + StartMin);
                                    StartSec = "59";
                                }
                            }
                        }
                        if (StartSec.equals("0") && StartMin.equals("0"))
                        {
                            btn_send_again.setEnabled(true);
                            btn_send_again.setBackgroundColor(Color.parseColor("#FE1743"));
                            txt_validation_timer.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    private void ActiveUser(int ActiveCode)
    {
        if (SharedPreferencesHelper.ReadInt("ActiveCode") == ActiveCode)
        {
            Call<Boolean> ActiveUser = requestApi.ActiveUser(ActiveCode, UserName);
            LoadingDialog.show();
            ActiveUser.enqueue(new ApiCallBack<Boolean>(context, ActiveUser)
            {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response)
                {
                    if (response.body())
                    {
                        LoginUser();
                    }
                    else if (!response.body())
                    {
                        LoadingDialog.dismiss();
                        Toast.makeText(context, "خطا در فعال سازی", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t)
                {
                    LoadingDialog.dismiss();
                    super.onFailure(call, t);
                }
            });
        }
        else
        {
            Toast.makeText(context, "کد وارد شده صحیح نیست", Toast.LENGTH_LONG).show();
        }
    }
}
