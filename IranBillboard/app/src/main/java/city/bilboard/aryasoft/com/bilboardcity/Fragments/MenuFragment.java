package city.bilboard.aryasoft.com.bilboardcity.Fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import city.bilboard.aryasoft.com.bilboardcity.Activities.FavoriteActivity;
import city.bilboard.aryasoft.com.bilboardcity.Activities.InfoActivity;
import city.bilboard.aryasoft.com.bilboardcity.Activities.LoginActivity;
import city.bilboard.aryasoft.com.bilboardcity.Activities.ManagementActivity;
import city.bilboard.aryasoft.com.bilboardcity.DataBaseContext.basket;
import city.bilboard.aryasoft.com.bilboardcity.DataBaseContext.favorites;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.SharedPreferencesHelper;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.VectorDrawablePreLollipopHelper;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.VectorView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MenuFragment extends Fragment
{
    private View mView;
    private ImageButton btnCloseMenu2;
    private Button btnManagement;
    private Button btnShareApp;
    private ImageView imgMenuBg;
    private ImageView imgMenuBg2;
    private String LoginState = "0";
    private Button btnLoginMenu;
    private Button btn_Favorite;
    private Button btn_help;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        initViews(mView);
        initEvents();
        setToolbarBg();
        setToolbarBgAnimation();
    }

    private class ShareAppAsync extends AsyncTask<Void, Void, Void>
    {
        private SweetAlertDialog LoadingDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            LoadingDialog = new SweetAlertDialog(mView.getContext(), SweetAlertDialog.PROGRESS_TYPE);
            LoadingDialog.setTitleText("لطفا کمی صبر کنید...");
            LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            LoadingDialog.setCancelable(false);
            LoadingDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            ShareApplication(mView.getContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            LoadingDialog.dismiss();
        }
    }

    private void initViews(View view)
    {
        final int RoleId = SharedPreferencesHelper.ReadInt("RoleId");
        LoginState = SharedPreferencesHelper.ReadString("SignedStep");//>0=Not A User ....1=>Its LoggedIn User
        btnManagement = view.findViewById(R.id.btn_management_menu);
        btnShareApp = view.findViewById(R.id.btn_share_app_menu);
        btn_help = view.findViewById(R.id.btn_help);
        VectorDrawablePreLollipopHelper.SetVectors(getResources(), new VectorView(R.drawable.ic_help, btn_help, VectorDrawablePreLollipopHelper.MyDirType.end));
        btnLoginMenu = view.findViewById(R.id.btn_login_menu);
        btn_Favorite = view.findViewById(R.id.btn_Favorite);
        imgMenuBg = view.findViewById(R.id.img_menu_bg);
        imgMenuBg2 = view.findViewById(R.id.img_menu_bg2);
        btnCloseMenu2 = view.findViewById(R.id.btn_close_menu2);
        CardView menuCardView = view.findViewById(R.id.card_menu);
        menuCardView.setMaxCardElevation(0);
        switch (RoleId)
        {
            case 2:
                btnManagement.setVisibility(View.VISIBLE);
                btnManagement.setText("مدیریت تابلو(مدیر)");

                break;
            case 3:
                btnManagement.setVisibility(View.VISIBLE);
                btnManagement.setText("مدیریت تابلو های من (مالک) ");
                btn_help.setVisibility(View.VISIBLE);
                break;
            case 4:
                btnManagement.setVisibility(View.VISIBLE);
                btnManagement.setText("تابلوهای من (کاربر ویژه)");
                btn_help.setVisibility(View.VISIBLE);
                break;
        }
        if (LoginState.equals("2"))
        {
            btnLoginMenu.setText("خروج از حساب");
        }
        btn_help.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (RoleId == 3)
                {
                    startActivity(new Intent(getActivity(), InfoActivity.class).putExtra("info_type", 3));
                }
                else if (RoleId == 4)
                {
                    startActivity(new Intent(getActivity(), InfoActivity.class).putExtra("info_type", 4));
                }
            }
        });
    }

    private void initEvents()
    {
        btnCloseMenu2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(0, R.anim.scale_down_frg);
                ft.replace(R.id.main_placeholder, new HomeFragment());
                ft.commit();
            }
        });
        btnManagement.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (HelperModule.IsUser())
                {
                    Intent i = new Intent(getActivity().getBaseContext(), ManagementActivity.class);
                    startActivity(i);
                }
                else
                {
                    HelperModule.ShowSignInAlert(v.getContext());
                }
            }
        });
        btnLoginMenu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {

                if (LoginState.equals("2"))
                {
                    ShowLogOutDialog(v.getContext());
                }
                else
                {
                    Intent LoginOrSignUpUserIntent = new Intent(getContext(), LoginActivity.class);
                    startActivity(LoginOrSignUpUserIntent);
                    getActivity().finish();
                }
            }
        });
        btn_Favorite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (HelperModule.IsUser())
                {
                    Intent FavoriteActivityIntent = new Intent(v.getContext(), FavoriteActivity.class);
                    startActivity(FavoriteActivityIntent);
                }
                else
                {
                    HelperModule.ShowSignInAlert(v.getContext());
                }
            }
        });
        btnShareApp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                new ShareAppAsync().execute();
            }
        });
    }

    private void ShowLogOutDialog(Context context)
    {
        android.support.v7.app.AlertDialog.Builder ExitAccountAlert = new android.support.v7.app.AlertDialog.Builder(context);
        ExitAccountAlert.setCancelable(false);
        View AlertView = View.inflate(context, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = AlertView.findViewById(R.id.Notifydialog_title);
        NotifyDialogTitle.setText("کاربر گرامی آیا مایل خروج از حساب کاربری خود هستید؟\n توجه داشته باشید که با خروج از حساب کاربری اطلاعات شما از سیستم پاک نخواهد شد و شما دوباره می توانید به سیستم ورود کنید");
        ExitAccountAlert.setView(AlertView);
        ExitAccountAlert.setPositiveButton("موافقم", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                SharedPreferencesHelper.ClearPreferences();
                dialog.dismiss();
                favorites.deleteAll(favorites.class);
                basket.deleteAll(basket.class);
                try
                {
                    Thread.sleep(500);
                    System.exit(0);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        ExitAccountAlert.setNegativeButton("مخالفم", new DialogInterface.OnClickListener()
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

    private void setToolbarBg()
    {
        Picasso.get().load(R.drawable.menu_toolbar_bg).into(imgMenuBg);
        Picasso.get().load(R.drawable.menu_toolbar_bg).into(imgMenuBg2);
    }

    private void setToolbarBgAnimation()
    {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(-1);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(25000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {

            public void onAnimationUpdate(ValueAnimator animation)
            {
                float height = (float) imgMenuBg.getHeight();
                float translationY = height * ((Float) animation.getAnimatedValue()).floatValue();
                imgMenuBg.setTranslationY(-translationY);
                imgMenuBg2.setTranslationY(height - translationY);
            }
        });
        animator.start();
    }

    public static void ShareApplication(Context AppContext)
    {
        ApplicationInfo app = AppContext.getApplicationInfo();
        String filePath = app.sourceDir;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        File originalApk = new File(filePath);
        try
        {
            File tempFile = new File(AppContext.getExternalCacheDir() + "/ExtractedApk");
            if (!tempFile.isDirectory())
            {
                if (!tempFile.mkdirs())
                {
                    return;
                }
            }
            tempFile = new File(tempFile.getPath() + "/" + AppContext.getString(app.labelRes).replace(" ", "").toLowerCase() + ".apk");
            if (!tempFile.exists())
            {
                if (!tempFile.createNewFile())
                {
                    return;
                }
            }
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
            AppContext.startActivity(Intent.createChooser(intent, "اشتراک گذاری برنامه با"));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
