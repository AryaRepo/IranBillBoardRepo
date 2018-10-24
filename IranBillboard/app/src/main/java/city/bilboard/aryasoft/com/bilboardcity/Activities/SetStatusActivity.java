package city.bilboard.aryasoft.com.bilboardcity.Activities;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import java.util.ArrayList;

import city.bilboard.aryasoft.com.bilboardcity.Adapter.OwnerBoardStatusAdapter;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiCallBack;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.BoardModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.SharedPreferencesHelper;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SetStatusActivity extends AppCompatActivity
{
    private SweetAlertDialog LoadingDialog = null;
    boolean IsDone = false;
    private boolean IsLoading = false;
    int PageNumber = 1;
    private int UserId = -1;
    private RequestApi requestApi = null;
    private RecyclerView recyclerViewChangeStatus=null;
    private OwnerBoardStatusAdapter BoardListOfOwnerRecyclerAdapter=null;
    private LottieAnimationView EmptyList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_status);
        requestApi= MyApplication.GetRetrofitRequestApi();
        UserId= SharedPreferencesHelper.ReadInt("UserID");
        initViews();
        LoadBoardsList(PageNumber);
        //-----------------
    }

    private void initViews()
    {
        EmptyList= findViewById(R.id.st_EmptyList);
        //------------
        LoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText("لطفا کمی صبر کنید...");
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        ///------------
        recyclerViewChangeStatus = findViewById(R.id.recycler_change_status);
        GridLayoutManager boardListOfOwnerLayoutManager = new GridLayoutManager(this, 2);
        BoardListOfOwnerRecyclerAdapter = new OwnerBoardStatusAdapter(this,LoadingDialog);
        recyclerViewChangeStatus.setLayoutManager(boardListOfOwnerLayoutManager);
        recyclerViewChangeStatus.setAdapter(BoardListOfOwnerRecyclerAdapter);

    }
    private void LoadBoardsList(int PageNumber)
    {
        if (!IsDone)
        {
            LoadingDialog.show();
            Call<ArrayList<BoardModel>> getBoardListOfOwner = requestApi.GetBoardListOfOwner(UserId, PageNumber);
            getBoardListOfOwner.enqueue(new ApiCallBack<ArrayList<BoardModel>>(this, getBoardListOfOwner)
            {
                @Override
                public void onResponse(Call<ArrayList<BoardModel>> call, Response<ArrayList<BoardModel>> response)
                {
                    if (response.isSuccessful())
                    {
                        if (response.body() != null && response.body().size() > 0)
                        {
                            BoardListOfOwnerRecyclerAdapter.RefreshAdapterData(response.body());
                            recyclerViewChangeStatus.setVisibility(View.VISIBLE);
                            IsLoading = false;
                        }
                        else
                        {
                            IsDone = true;
                            IsLoading = false;
                            if (BoardListOfOwnerRecyclerAdapter.getItemCount() <= 0)
                            {
                                EmptyList.setVisibility(View.VISIBLE);
                                recyclerViewChangeStatus.setVisibility(View.GONE);
                            }
                        }
                        LoadingDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<BoardModel>> call, Throwable t)
                {
                    LoadingDialog.dismiss();
                    super.onFailure(call,t);
                }
            });
        }
    }
    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
