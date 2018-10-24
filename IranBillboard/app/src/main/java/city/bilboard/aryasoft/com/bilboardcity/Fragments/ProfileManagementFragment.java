package city.bilboard.aryasoft.com.bilboardcity.Fragments;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import city.bilboard.aryasoft.com.bilboardcity.Activities.EditProfileActivity;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiCallBack;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.ProfileModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.ServiceGenerator;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.R;

import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.SharedPreferencesHelper;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Response;

public class ProfileManagementFragment extends Fragment
{
    private int UserId = 0;
    private String RoleTitle = "";
    private RequestApi requestApi = null;
    private View _View;
    private TextView UserProfile_txtName = null;
    private TextView UserProfile_txtEmail = null;
    private TextView user_profile_point = null;
    private ProfileModel profileModel = null;
    private TextView txt_UserProfile_txtAboutMe = null;
    private ImageView imgMenuBg, imgMenuBg2;
    private SweetAlertDialog LoadingDialog;
    private String JsonProfile = "";
    private ImageButton userProfile_btnEditProfile = null;
    private Dialog EditPasswordAlert;
    private RelativeLayout bth_edit_password= null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        _View = inflater.inflate(R.layout.fragment_profile_managment, container, false);
        UserId = SharedPreferencesHelper.ReadInt("UserID");
        RoleTitle = SharedPreferencesHelper.ReadString("RoleTitle").equals("0") ? "سطح دسترسی نا مشخص" : SharedPreferencesHelper.ReadString("RoleTitle");
        requestApi = ServiceGenerator.createService(RequestApi.class);
        profileModel = new ProfileModel();
        return _View;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        userProfile_btnEditProfile.clearAnimation();
        userProfile_btnEditProfile.setColorFilter(Color.TRANSPARENT);
        GetProfileInfo();
    }

    private void InitViews()
    {
        userProfile_btnEditProfile = _View.findViewById(R.id.edit_pro);
        UserProfile_txtName = _View.findViewById(R.id.user_profile_name);
        UserProfile_txtEmail = _View.findViewById(R.id.txt_UserProfile_txtEmail);
        bth_edit_password = _View.findViewById(R.id.bth_edit_password);
        user_profile_point = _View.findViewById(R.id.user_profile_point);
        TextView userProfile_txtUserRole = _View.findViewById(R.id.user_profile_role_name);
        txt_UserProfile_txtAboutMe = _View.findViewById(R.id.txt_UserProfile_txtAboutMe);
        userProfile_txtUserRole.setText(RoleTitle);
        userProfile_btnEditProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //-------------------------------
                startActivity(new Intent(v.getContext(), EditProfileActivity.class).putExtra("UserInfo", JsonProfile));
            }
        });
        LoadingDialog = new SweetAlertDialog(_View.getContext(), SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText("لطفا کمی صبر کنید...");
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        bth_edit_password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                EditPassword();
            }
        });
    }

    private void GetProfileInfo()
    {
        LoadingDialog.show();
        Call<ProfileModel> GetUserInfoProfile = requestApi.GetUserInfoProfile(UserId);
        GetUserInfoProfile.enqueue(new ApiCallBack<ProfileModel>(getContext(), GetUserInfoProfile)
        {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response)
            {
                if (response.isSuccessful())
                {
                    profileModel = response.body();
                    UserProfile_txtName.setText(profileModel.Fname + " " + profileModel.Lname);
                    UserProfile_txtEmail.setText(profileModel.Email + "");
                    txt_UserProfile_txtAboutMe.setText(profileModel.Description);
                    user_profile_point.setText(profileModel.Points + " امتیاز ");
                    JsonProfile = new Gson().toJson(profileModel);
                    if (response.body().ImageName.equals("nophoto.png"))
                    {
                        Picasso.get().load(R.drawable.user_profile).into(((ImageView) _View.findViewById(R.id.user_profile_image)));
                    }
                    else
                    {
                        Picasso.get().load(getString(R.string.ProfileImageFolder) + response.body().ImageName).error(R.drawable.no_img).into(((ImageView) _View.findViewById(R.id.user_profile_image)));
                    }
                }
                LoadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t)
            {
                LoadingDialog.dismiss();
                ImageView user_profile_image = _View.findViewById(R.id.user_profile_image);
                Picasso.get().load(R.drawable.no_img).into(user_profile_image);
                UserProfile_txtName.setText("نام و فامیلی شما در سیستم ثبت نشده");
                UserProfile_txtEmail.setText("ایمیل شما در سیستم ثبت نشده");
                txt_UserProfile_txtAboutMe.setText("اطلاعات شما در سسیستم ثبت نام نشده");
                user_profile_point.setText("بدون امتیاز");
                userProfile_btnEditProfile.setColorFilter(Color.YELLOW);
                //-------------------------------------------------
                Animation mAnimation = new AlphaAnimation(1, 0);
                mAnimation.setDuration(700);
                mAnimation.setInterpolator(new LinearInterpolator());
                mAnimation.setRepeatCount(Animation.INFINITE);
                mAnimation.setRepeatMode(Animation.REVERSE);
                userProfile_btnEditProfile.startAnimation(mAnimation);
            }
        });
    }


    private void setToolbarBg()
    {
        imgMenuBg = _View.findViewById(R.id.header_cover_image1);
        imgMenuBg2 = _View.findViewById(R.id.header_cover_image2);
        Picasso.get().load(R.drawable.menu_toolbar_bg).into(imgMenuBg);
        Picasso.get().load(R.drawable.menu_toolbar_bg).into(imgMenuBg2);
        setToolbarBgAnimation();
    }

    private void setToolbarBgAnimation()
    {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0F, 0.0f);
        animator.setRepeatCount(-1);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(25000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {

            public void onAnimationUpdate(ValueAnimator animation)
            {
                float width = (float) imgMenuBg.getWidth();
                float translationY = width * ((Float) animation.getAnimatedValue()).floatValue();
                imgMenuBg.setTranslationX(-translationY);
                imgMenuBg2.setTranslationX(width - translationY);
            }
        });
        animator.start();
    }

    private void EditPassword()
    {
        final String MobileNumber = SharedPreferencesHelper.ReadString("MobileNumber");
        final String OldPassword = SharedPreferencesHelper.ReadString("CurrentPassword");
        android.support.v7.app.AlertDialog.Builder NewPasswordDialogAlert = new android.support.v7.app.AlertDialog.Builder(getContext());
        View AlertView = View.inflate(getContext(), R.layout.change_password_layout_dialog, null);
        NewPasswordDialogAlert.setView(AlertView);
        final EditText edt_new_password = AlertView.findViewById(R.id.edt_new_password);
        final EditText edt_new_password_rpt = AlertView.findViewById(R.id.edt_new_password_rpt);
        final Button btn_change_password = AlertView.findViewById(R.id.btn_change_password);
        EditPasswordAlert = NewPasswordDialogAlert.create();
        EditPasswordAlert.show();
        //---------------
        btn_change_password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LoadingDialog.show();
                if (!HelperModule.IsFillValidation(edt_new_password, edt_new_password_rpt))
                {
                    LoadingDialog.dismiss();
                    Toast.makeText(getContext(), "لطفا مقادیر خواسته شده را وارد کنید.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(edt_new_password.getText().toString().equals(edt_new_password_rpt.getText().toString())))
                {
                    LoadingDialog.dismiss();
                    Toast.makeText(getContext(), "مقادیر گذرواژه و تکرار گذرواژه همخوانی ندارد", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edt_new_password.getText().toString().length() < 6 && edt_new_password_rpt.getText().toString().length() < 6)
                {
                    LoadingDialog.dismiss();
                    Toast.makeText(getContext(), "حداقل میزان پسورد 6 حرف است", Toast.LENGTH_SHORT).show();
                    return;
                }
                Call<Boolean> ChangePassword = requestApi.ChangePassword(MobileNumber, OldPassword, edt_new_password.getText().toString());
                ChangePassword.enqueue(new ApiCallBack<Boolean>(getContext(),ChangePassword)
                {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response)
                    {
                        if (response.body())
                        {
                            Toast.makeText(getContext(), "گذرواژه شما تغییر کرد.", Toast.LENGTH_SHORT).show();
                            SharedPreferencesHelper.WriteString("CurrentPassword", edt_new_password.getText().toString());
                        }
                        else
                        {
                            Toast.makeText(getContext(), "مشکلی در ویرایش گذرواژه رخ داده است.", Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.dismiss();
                        EditPasswordAlert.dismiss();
                        //--------------------------
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t)
                    {
                        EditPasswordAlert.dismiss();
                        LoadingDialog.dismiss();
                        super.onFailure(call, t);
                    }
                });
                //-----------------------
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setToolbarBg();
        InitViews();
        GetProfileInfo();
    }
}
