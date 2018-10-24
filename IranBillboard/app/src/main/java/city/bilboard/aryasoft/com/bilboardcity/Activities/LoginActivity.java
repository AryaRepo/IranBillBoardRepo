package city.bilboard.aryasoft.com.bilboardcity.Activities;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiCallBack;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.RecoverPasswordApiModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiService;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.UserLoginRegisterModule;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity
{
    private ImageView imgLoginHeader, imgLoginFooter, imgLoginFooter2, imgLoginIcon;
    private Button btnLogin, btnSignup;
    private Button btn_forget_pass = null;
    private EditText edt_login_username = null;
    private EditText edt_login_password = null;
    private UserLoginRegisterModule LoginRegisterModule = null;
    private Dialog PasswordRecoveryDialog;
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(this, LandActivity.class));
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        initViews();
        LoginRegisterModule = new UserLoginRegisterModule(this);
        setFooterBgAnimation();
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (edt_login_username.getText().length() <= 0 || edt_login_password.getText().length() <= 0)
                {
                    return;
                }
                LoginRegisterModule.SetUserName(HelperModule.arabicToDecimal(edt_login_username.getText().toString()));
                LoginRegisterModule.SetPassword(HelperModule.arabicToDecimal(edt_login_password.getText().toString()));
                LoginRegisterModule.LoginUser();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent regIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(regIntent);
            }
        });
        btn_forget_pass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                android.support.v7.app.AlertDialog.Builder PasswordRecoveryAlert = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
                View AlertView = View.inflate(LoginActivity.this, R.layout.forgot_password_dialog, null);
                Button btn_PasswordRecoverySend = AlertView.findViewById(R.id.btn_PasswordRecoverySend);
                final EditText edt_PasswordRecoveryPhoneNumber = AlertView.findViewById(R.id.edt_PasswordRecoveryPhoneNumber);
                PasswordRecoveryAlert.setView(AlertView);
                PasswordRecoveryDialog=PasswordRecoveryAlert.create();
                btn_PasswordRecoverySend.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        String UserPhoneNumber = edt_PasswordRecoveryPhoneNumber.getText().toString();
                        if (UserPhoneNumber.isEmpty())
                        {
                            Toast.makeText(LoginActivity.this, "لطفا شماره تلفن همراه خود را وارد کنید.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            RecoverPasswordApiModel recoverPasswordApiModel = new RecoverPasswordApiModel();
                            recoverPasswordApiModel.MobileNumber = UserPhoneNumber;
                            LoginRegisterModule.ForgotPassword(recoverPasswordApiModel);
                            PasswordRecoveryDialog.dismiss();
                        }
                    }
                });
                PasswordRecoveryDialog= PasswordRecoveryAlert.show();
            }
        });
    }

    private void initViews()
    {
        imgLoginFooter = findViewById(R.id.img_login_footer);
        imgLoginFooter2 = findViewById(R.id.img_login_footer2);
        imgLoginHeader = findViewById(R.id.img_login_header);
        imgLoginIcon = findViewById(R.id.img_login_icon);
        btn_forget_pass = findViewById(R.id.btn_forget_pass);
        btnLogin = findViewById(R.id.btn_login_signin);
        btnSignup = findViewById(R.id.btn_login_reg);
        edt_login_username = findViewById(R.id.edt_login_username);
        edt_login_password = findViewById(R.id.edt_login_password);
        Picasso.get().load(R.drawable.login_toolbar2).into(imgLoginHeader);
        Picasso.get().load(R.drawable.city).into(imgLoginFooter);
        Picasso.get().load(R.drawable.city).into(imgLoginFooter2);
        Picasso.get().load(R.drawable.city2).into(imgLoginIcon);
        edt_login_username.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() == 11)
                {
                    edt_login_password.requestFocus();
                }
            }
        });
    }

    private void setFooterBgAnimation()
    {
        Handler AnimHandler = new Handler();
        Thread AnimThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
                animator.setRepeatCount(-1);
                animator.setInterpolator(new LinearInterpolator());
                animator.setDuration(25000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {

                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        float width = (float) imgLoginFooter.getWidth();
                        float translationX = width * ((Float) animation.getAnimatedValue()).floatValue();
                        imgLoginFooter.setTranslationX(-translationX);
                        imgLoginFooter2.setTranslationX(width - translationX);
                    }
                });
                animator.start();
            }
        });
        AnimHandler.post(AnimThread);

    }


}
