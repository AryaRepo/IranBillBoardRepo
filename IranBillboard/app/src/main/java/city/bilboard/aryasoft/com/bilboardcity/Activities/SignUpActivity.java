package city.bilboard.aryasoft.com.bilboardcity.Activities;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import city.bilboard.aryasoft.com.bilboardcity.Models.SignUpModel;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.UserLoginRegisterModule;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.VectorDrawablePreLollipopHelper;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.VectorView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity
{
    private EditText SignUp_txt_PhoneNumber = null;
    private EditText SignUp_txt_Password = null;
    private EditText SignUp_txt_Email = null;
    private EditText signUp_txt_Reagent = null;
    private ImageView img_sgn_footer = null;
    private ImageView img_sgn_footer2 = null;
    private UserLoginRegisterModule LoginRegisterModule = null;

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
        setContentView(R.layout.signup_layout);
        InitializeViews();
        AnimatingViews();
        LoginRegisterModule = new UserLoginRegisterModule(this);
    }

    private void InitializeViews()
    {
        ArrayList<VectorView> vectorsList = new ArrayList<>();
        img_sgn_footer = findViewById(R.id.img_sgn_footer);
        img_sgn_footer2 = findViewById(R.id.img_sgn_footer2);
        Picasso.get().load(R.drawable.city).into(img_sgn_footer);
        Picasso.get().load(R.drawable.city).into(img_sgn_footer2);
        CardView signUp_Card_Container = findViewById(R.id.SignUp_Card_Container);
        Button signUp_btn_Register = findViewById(R.id.SignUp_btn_Register);
        SignUp_txt_PhoneNumber = findViewById(R.id.SignUp_txt_PhoneNumber);
        SignUp_txt_Password = findViewById(R.id.SignUp_txt_PassWord);
        signUp_txt_Reagent = findViewById(R.id.SignUp_txt_Reagent);
        SignUp_txt_Email = findViewById(R.id.SignUp_txt_Email);
        ImageView signUp_Reagent_help = findViewById(R.id.SignUp_Reagent_help);
        ImageView signUp_email_help = findViewById(R.id.SignUp_email_help);
        signUp_Card_Container.setMaxCardElevation(0);
        vectorsList.add(new VectorView(R.drawable.ic_phone_myuser, SignUp_txt_PhoneNumber, VectorDrawablePreLollipopHelper.MyDirType.end));
        vectorsList.add(new VectorView(R.drawable.ic_password, SignUp_txt_Password, VectorDrawablePreLollipopHelper.MyDirType.end));
        vectorsList.add(new VectorView(R.drawable.ic_users_reagent, signUp_txt_Reagent, VectorDrawablePreLollipopHelper.MyDirType.end));
        vectorsList.add(new VectorView(R.drawable.ic_user_email, SignUp_txt_Email, VectorDrawablePreLollipopHelper.MyDirType.end));
        VectorDrawablePreLollipopHelper.SetVectors(getResources(), vectorsList);
        // ---------------------------------------------
        Animation mAnimation = new AlphaAnimation(1, 0);
        mAnimation.setDuration(700);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE);
        signUp_Reagent_help.startAnimation(mAnimation);
        signUp_email_help.startAnimation(mAnimation);
        //-------------Blink--------------------------------
        signUp_Reagent_help.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.clearAnimation();
                android.support.v7.app.AlertDialog.Builder MyAleart = new android.support.v7.app.AlertDialog.Builder(v.getContext());
                MyAleart.setCancelable(false);
                View AlertView = View.inflate(v.getContext(), R.layout.dialog_notify, null);
                TextView NotifyDialogTitle = (TextView) AlertView.findViewById(R.id.Notifydialog_title);
                NotifyDialogTitle.setText(R.string.ReagentHelpText);
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
        });
        signUp_email_help.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.clearAnimation();
                android.support.v7.app.AlertDialog.Builder MyAlert = new android.support.v7.app.AlertDialog.Builder(v.getContext());
                MyAlert.setCancelable(false);
                View AlertView = View.inflate(v.getContext(), R.layout.dialog_notify, null);
                TextView NotifyDialogTitle = AlertView.findViewById(R.id.Notifydialog_title);
                NotifyDialogTitle.setText(R.string.EmailHelpText);
                MyAlert.setView(AlertView);
                MyAlert.setPositiveButton("متوجه شدم", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                //--------------------
                MyAlert.show();

            }
        });
        signUp_btn_Register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (SignUp_txt_PhoneNumber.getText().toString().length() < 11 || SignUp_txt_Password.getText().toString().isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "لطفا شماره تلفن و گذرواژه را به درستی پر کنید.", Toast.LENGTH_SHORT).show();
                    return;
                }
                SignUpModel NewUser = new SignUpModel();
                NewUser.MobileNumber = HelperModule.arabicToDecimal(SignUp_txt_PhoneNumber.getText().toString());
                NewUser.Password = HelperModule.arabicToDecimal(SignUp_txt_Password.getText().toString());
                if (SignUp_txt_Email.getText().toString().length() > 0)
                {
                    NewUser.Email = SignUp_txt_Email.getText().toString();
                }
                NewUser.RecommendMobileNumber = HelperModule.arabicToDecimal(signUp_txt_Reagent.getText().toString());
                LoginRegisterModule.RegisterUser(NewUser);
            }
        });

    }

    private void AnimatingViews()
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
                        float width = (float) img_sgn_footer.getWidth();
                        float translationX = width * ((Float) animation.getAnimatedValue()).floatValue();
                        img_sgn_footer.setTranslationX(-translationX);
                        img_sgn_footer2.setTranslationX(width - translationX);
                    }
                });
                animator.start();
            }
        });
        AnimHandler.post(AnimThread);

    }


}

