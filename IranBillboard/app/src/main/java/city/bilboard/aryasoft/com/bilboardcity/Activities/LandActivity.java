package city.bilboard.aryasoft.com.bilboardcity.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.Toast;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiCallBack;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.RoleModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiService;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.Fragments.HomeFragment;
import city.bilboard.aryasoft.com.bilboardcity.Fragments.ProfileManagementFragment;
import city.bilboard.aryasoft.com.bilboardcity.Fragments.SearchFragment;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.SharedPreferencesHelper;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import city.bilboard.aryasoft.com.bilboardcity.R;

public class LandActivity extends AppCompatActivity
{
    boolean IsUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        IsUser = HelperModule.IsUser();
        setContentView(R.layout.activity_land);
        GetUserRole();
        setupNavigationBottom();
        setupFragments(new HomeFragment());
        //--------------
    }


    private void GetUserRole()
    {
        if (SharedPreferencesHelper.ReadInt("UserID") != -1)
        {
            RequestApi requestApi = ApiService.createService(RequestApi.class);
            Call<RoleModel> GetUserRole = requestApi.GetUserRole(SharedPreferencesHelper.ReadInt("UserID"));
            GetUserRole.enqueue(new ApiCallBack<RoleModel>(this, GetUserRole)
            {
                @Override
                public void onResponse(Call<RoleModel> call, Response<RoleModel> response)
                {
                    SharedPreferencesHelper.WriteInt("RoleId", response.body().RoleId);
                    SharedPreferencesHelper.WriteString("RoleTitle", response.body().RoleTitle);
                }
                @Override
                public void onFailure(Call<RoleModel> call, Throwable t)
                {
                    super.onFailure(call, t);
                }
            });
        }
    }

    private void setupFragments(Fragment frgobj)
    {
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.main_placeholder, frgobj);
        ft.commit();
    }
    AHBottomNavigation bottomNavigation;
    private void setupNavigationBottom()
    {
         bottomNavigation = findViewById(R.id.bottom_navigation_land);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_user_gray, R.color.bottomBarIconColor);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.ic_home_gray, R.color.bottomBarIconColor);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_search_black, R.color.bottomBarIconColor);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#F5F5F5"));
        bottomNavigation.setBehaviorTranslationEnabled(true);
        bottomNavigation.setAccentColor(getResources().getColor(R.color.colorAccent));
        bottomNavigation.setInactiveColor(Color.parseColor("#BDBDBD"));
        bottomNavigation.setForceTint(true);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setCurrentItem(1);
        // Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener()
        {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected)
            {
                // Do something cool here...
                switch (position)
                {
                    case 0:
                        setupFragments(new SearchFragment());
                        break;
                    case 1:
                        setupFragments(new HomeFragment());
                        break;
                    case 2:

                        if (!IsUser)
                        {
                            Toast.makeText(LandActivity.this, "کاربر عزیز لطفا اول ثبت نام/ورود کنید.", Toast.LENGTH_SHORT).show();
                            Intent LoginOrSignUpUserIntent = new Intent(LandActivity.this, LoginActivity.class);
                            startActivity(LoginOrSignUpUserIntent);
                            finish();
                        }
                        else
                        {
                            setupFragments(new ProfileManagementFragment());
                        }
                        break;
                    default:
                        setupFragments(new HomeFragment());
                        break;
                }
                return true;
            }
        });
        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener()
        {
            @Override
            public void onPositionChange(int y)
            {
                // Manage the new y position
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.land_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        if( bottomNavigation.getCurrentItem()==1)
        {
            HelperModule.ShowExitAppDialog(this);
            return;
        }
        setupFragments(new HomeFragment());
        bottomNavigation.setCurrentItem(1);
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
