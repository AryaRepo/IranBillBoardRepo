package city.bilboard.aryasoft.com.bilboardcity.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import java.util.ArrayList;
import city.bilboard.aryasoft.com.bilboardcity.Adapter.AdminAdapter;
import city.bilboard.aryasoft.com.bilboardcity.Adapter.OwnerAdapter;
import city.bilboard.aryasoft.com.bilboardcity.Adapter.UsersAdapter;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiCallBack;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.Models.AdminModel;
import city.bilboard.aryasoft.com.bilboardcity.Models.OwnerModel;
import city.bilboard.aryasoft.com.bilboardcity.Models.UserModel;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.SharedPreferencesHelper;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
public class ManagementActivity extends AppCompatActivity
{
    private SweetAlertDialog LoadingDialog = null;
    private TextView txt_toolbar = null;
    int RoleId = -1;
    private RequestApi requestApi = null;
    boolean IsDone = false;
    private boolean IsLoading = false;
    int PageNumber = 1;
    private int UserId = -1;
    private LottieAnimationView EmptyList = null;
    private LinearLayoutManager ReserveBoardsListLayoutManager = null;
    private AdminAdapter adminAdapter = null;
    private RecyclerView recyclerViewAdmin;
    private LinearLayoutManager ReportRentBoardsLayoutManager = null;
    private UsersAdapter usersAdapter = null;
    private RecyclerView recyclerViewUser;

    //--------------------------------------
    private LinearLayoutManager ReportRentBoardsOwnerLayoutManager = null;
    private OwnerAdapter ownerAdapter = null;
    private RecyclerView RecyclerViewOwner= null;;

    //--------------------------------------
    private void LoadReserveBoardsList(int PageNumber)
    {
        if (!IsDone)
        {
            LoadingDialog.show();
            Call<ArrayList<AdminModel>> getReserveBoardsList = requestApi.GetReserveBoardsList(PageNumber);
            getReserveBoardsList.enqueue(new ApiCallBack<ArrayList<AdminModel>>(this, getReserveBoardsList)
            {
                @Override
                public void onResponse(Call<ArrayList<AdminModel>> call, Response<ArrayList<AdminModel>> response)
                {
                    if (response.isSuccessful())
                    {
                        if (response.body() != null && response.body().size() > 0)
                        {
                            adminAdapter.RefreshAdapterData(response.body());
                            EmptyList.setVisibility(View.GONE);
                            recyclerViewAdmin.setVisibility(View.VISIBLE);
                            LoadingDialog.dismiss();
                            IsLoading = false;
                        }
                        else
                        {
                            IsDone = true;
                            LoadingDialog.dismiss();
                            IsLoading = false;

                            if (adminAdapter.getItemCount() <= 0)
                            {
                                EmptyList.setVisibility(View.VISIBLE);
                                recyclerViewAdmin.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<AdminModel>> call, Throwable t)
                {
                    LoadingDialog.dismiss();
                    super.onFailure(call, t);
                }
            });
        }
    }

    private void ReportRentBoards(int UserId, int PageNumber)
    {
        if (!IsDone)
        {
            LoadingDialog.show();
            Call<ArrayList<UserModel>> getReportRentBoards = requestApi.GetReportRentBoards(UserId, PageNumber);
            getReportRentBoards.enqueue(new ApiCallBack<ArrayList<UserModel>>(this, getReportRentBoards)
            {
                @Override
                public void onResponse(Call<ArrayList<UserModel>> call, Response<ArrayList<UserModel>> response)
                {
                    if (response.isSuccessful())
                    {
                        if (response.body() != null && response.body().size() > 0)
                        {
                            usersAdapter.RefreshAdapterData(response.body());
                            EmptyList.setVisibility(View.GONE);
                            recyclerViewUser.setVisibility(View.VISIBLE);
                            LoadingDialog.dismiss();
                            IsLoading = false;
                        }
                        else
                        {
                            IsDone = true;
                            LoadingDialog.dismiss();
                            IsLoading = false;

                            if (usersAdapter.getItemCount() <= 0)
                            {
                                EmptyList.setVisibility(View.VISIBLE);
                                recyclerViewUser.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<UserModel>> call, Throwable t)
                {
                    LoadingDialog.dismiss();
                    super.onFailure(call, t);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        UserId = SharedPreferencesHelper.ReadInt("UserID");
        RoleId = SharedPreferencesHelper.ReadInt("RoleId");
        requestApi = MyApplication.GetRetrofitRequestApi();
        //-----------------
        LoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText("لطفا کمی صبر کنید...");
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        //-------------------------
        try
        {

            switch (RoleId)
            {
                case 2:
                    setContentView(R.layout.admin_layout);
                    /*
                    list tablo haye rezerv sgodeh (modir)=>aks,mobile
                     */
                    initViewsAdmin();
                    break;
                case 3:
                    setContentView(R.layout.owner_layout);
                    initViewsOwner();
                    /*
                    list tablo haye ejareh dadeh shodeh malek
                     */
                    break;
                case 4:
                    setContentView(R.layout.special_user_layout);
                    /*
                     list tablo haye ejareh grefteh shodeh (karbar vizheh)
                     */
                    initViewsSpecialUser();
                    break;
            }
        } catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void ReportRentBoardsOwner(int UserId, int PageNumber)
    {
        if (!IsDone)
        {
            LoadingDialog.show();
            Call<ArrayList<OwnerModel>> getReportRentBoardesOwner = requestApi.GetReportRentBoardesOwner(UserId, PageNumber);
            getReportRentBoardesOwner.enqueue(new ApiCallBack<ArrayList<OwnerModel>>(this, getReportRentBoardesOwner)
            {
                @Override
                public void onResponse(Call<ArrayList<OwnerModel>> call, Response<ArrayList<OwnerModel>> response)
                {
                    if (response.isSuccessful())
                    {
                        if (response.body() != null && response.body().size() > 0)
                        {
                            ownerAdapter.RefreshAdapterData(response.body());
                            EmptyList.setVisibility(View.GONE);
                            RecyclerViewOwner.setVisibility(View.VISIBLE);
                            LoadingDialog.dismiss();
                            IsLoading = false;
                        }
                        else
                        {
                            IsDone = true;
                            LoadingDialog.dismiss();
                            IsLoading = false;

                            if (ownerAdapter.getItemCount() <= 0)
                            {
                                EmptyList.setVisibility(View.VISIBLE);
                                RecyclerViewOwner.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<OwnerModel>> call, Throwable t)
                {
                    LoadingDialog.dismiss();
                    super.onFailure(call, t);
                }
            });
        }
    }

    private void initViewsAdmin()
    {
        recyclerViewAdmin = findViewById(R.id.recycler_reserved_billboard_admin);
        txt_toolbar = findViewById(R.id.txt_toolbar);
        txt_toolbar.setText("تابلو های رزرو شده");
        EmptyList = findViewById(R.id.Admin_EmptyList);
        ReserveBoardsListLayoutManager = new GridLayoutManager(this,2);
        adminAdapter = new AdminAdapter(this,LoadingDialog,requestApi);
        recyclerViewAdmin.setLayoutManager(ReserveBoardsListLayoutManager);
        recyclerViewAdmin.setAdapter(adminAdapter);
        ///===========
        LoadReserveBoardsList(PageNumber);
        recyclerViewAdmin.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (IsLoading)
                {
                    return;
                }
                int VisibleItemCount = ReserveBoardsListLayoutManager.getChildCount();
                int TotalItemCount = ReserveBoardsListLayoutManager.getItemCount();
                int PastVisibleItem = ReserveBoardsListLayoutManager.findFirstVisibleItemPosition();
                if (PastVisibleItem + VisibleItemCount == TotalItemCount && TotalItemCount != 0)
                {
                    IsLoading = true;
                    ++PageNumber;
                    LoadReserveBoardsList(PageNumber);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initViewsOwner()
    {
        RecyclerViewOwner = findViewById(R.id.recycler_rented_boards_by_owner);
        txt_toolbar = findViewById(R.id.txt_toolbar);
        txt_toolbar.setText("تابلو های اجاره داده شده ی شما(مالک)");
        EmptyList = findViewById(R.id.owner_EmptyList);
        ownerAdapter = new OwnerAdapter(this,requestApi,LoadingDialog);
        ReportRentBoardsOwnerLayoutManager = new GridLayoutManager(this, 2);
        RecyclerViewOwner.setLayoutManager(ReportRentBoardsOwnerLayoutManager);
        RecyclerViewOwner.setAdapter(ownerAdapter);
        Button btnChangeBoardsStatus = findViewById(R.id.btn_set_billboard_status);

        btnChangeBoardsStatus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(ManagementActivity.this, SetStatusActivity.class));
            }
        });
        ReportRentBoardsOwner(UserId, PageNumber);
        ///===========
        RecyclerViewOwner.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (IsLoading)
                {
                    return;
                }
                int VisibleItemCount = ReportRentBoardsOwnerLayoutManager.getChildCount();
                int TotalItemCount = ReportRentBoardsOwnerLayoutManager.getItemCount();
                int PastVisibleItem = ReportRentBoardsOwnerLayoutManager.findFirstVisibleItemPosition();
                if (PastVisibleItem + VisibleItemCount == TotalItemCount && TotalItemCount != 0)
                {
                    IsLoading = true;
                    ++PageNumber;
                    ReportRentBoardsOwner(UserId, PageNumber);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initViewsSpecialUser()
    {
        recyclerViewUser = findViewById(R.id.recycler_rented_boards_by_users);
        EmptyList = findViewById(R.id.Sp_user_EmptyList);
        txt_toolbar = findViewById(R.id.txt_toolbar);
        txt_toolbar.setText("تابلو های اجاره گرفته شده توسط شما(کاربرویژه)");
        ReportRentBoardsLayoutManager = new GridLayoutManager(this, 2);
        usersAdapter = new UsersAdapter(this,LoadingDialog,requestApi);
        recyclerViewUser.setLayoutManager(ReportRentBoardsLayoutManager);
        recyclerViewUser.setAdapter(usersAdapter);
        ///===========
        ReportRentBoards(UserId, PageNumber);
        recyclerViewUser.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (IsLoading)
                {
                    return;
                }
                int VisibleItemCount = ReportRentBoardsLayoutManager.getChildCount();
                int TotalItemCount = ReportRentBoardsLayoutManager.getItemCount();
                int PastVisibleItem = ReportRentBoardsLayoutManager.findFirstVisibleItemPosition();
                if (PastVisibleItem + VisibleItemCount == TotalItemCount && TotalItemCount != 0)
                {
                    IsLoading = true;
                    ++PageNumber;
                    ReportRentBoards(UserId, PageNumber);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
