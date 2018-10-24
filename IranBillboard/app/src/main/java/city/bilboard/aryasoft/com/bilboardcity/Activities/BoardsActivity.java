package city.bilboard.aryasoft.com.bilboardcity.Activities;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import city.bilboard.aryasoft.com.bilboardcity.Adapter.SimpleBoardAdapter;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiCallBack;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.BoardModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.CollectionModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.R;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BoardsActivity extends AppCompatActivity
{
    private RequestApi requestApi;
    private RelativeLayout LottieBoards_EmptyList ;
    private GridLayoutManager BoardsLayoutManager ;
    private SimpleBoardAdapter BoardsRecyclerAdapter ;
    private RecyclerView BoardsRecyclerView;
    private boolean IsLoading = false;
    boolean IsDone = false;
    private int CityCodeFilter = -1;
    private int SkipNumber = -15;
    private SweetAlertDialog LoadingDialog ;

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boards);
        Toolbar boardsActivityToolbar = findViewById(R.id.BoardsToolbarInclude);
        boardsActivityToolbar.setTitle("مشاهده تابلو ها");
        setSupportActionBar(boardsActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        requestApi = MyApplication.GetRetrofitRequestApi();
        int sectionId = getIntent().getIntExtra("SectionType", -1);
        int categoryId = getIntent().getIntExtra("CategoryId", -1);
        CityCodeFilter = getIntent().getIntExtra("CityCodeFilter", -1);
        BoardsRecyclerView = findViewById(R.id.BoardsRecyclerView);
        LottieBoards_EmptyList = findViewById(R.id.LottieBoards_EmptyList);
        //-------------------
        LoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        //--------------------
        BoardsRecyclerAdapter = new SimpleBoardAdapter(this);
        BoardsLayoutManager = new GridLayoutManager(this, 2);
        BoardsRecyclerView.setLayoutManager(BoardsLayoutManager);
        BoardsRecyclerView.setAdapter(BoardsRecyclerAdapter);
        if (categoryId != -1)
        {
            LoadBoardsByCategory(categoryId);
            return;
        }
        if (sectionId == 0)
        {
            LoadFavoriteBoards();
        }
        else if (sectionId > 0)
        {
            LoadBoardsByCollection(sectionId);
        }
        RecyclerLoadMoreData(categoryId, sectionId);
    }

    private void RecyclerLoadMoreData(final int categoryId, final int sectionId)
    {
        BoardsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (BoardsRecyclerAdapter.getItemCount() >= 15)
                {
                    if (IsLoading)
                    {
                        return;
                    }
                    int VisibleItemCount = BoardsLayoutManager.getChildCount();
                    int TotalItemCount = BoardsLayoutManager.getItemCount();
                    int PastVisibleItem = BoardsLayoutManager.findFirstVisibleItemPosition();
                    if (PastVisibleItem + VisibleItemCount == TotalItemCount && TotalItemCount != 0)
                    {
                        IsLoading = true;
                        if (categoryId != -1)
                        {
                            LoadBoardsByCategory(categoryId);
                            return;
                        }
                        if (sectionId == 0)
                        {
                            LoadFavoriteBoards();
                        }
                        else if (sectionId > 0)
                        {
                            LoadBoardsByCollection(sectionId);
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void LoadFavoriteBoards()
    {
        if (!IsDone)
        {
            LoadingDialog.show();
            Call<ArrayList<BoardModel>> GetFavouriteBoards = requestApi.GetFavouriteBoards(CityCodeFilter, SkipNumber += 15, 15);
            GetFavouriteBoards.enqueue(new ApiCallBack<ArrayList<BoardModel>>(this, GetFavouriteBoards)
            {
                @Override
                public void onResponse(Call<ArrayList<BoardModel>> call, Response<ArrayList<BoardModel>> response)
                {
                    if (response.body().size() != 0)
                    {
                        LottieBoards_EmptyList.setVisibility(View.GONE);
                        BoardsRecyclerView.setVisibility(View.VISIBLE);
                        IsLoading = false;
                        BoardsRecyclerAdapter.RefreshAdapterData(response.body(), false);
                    }
                    else
                    {
                        IsDone = true;
                        IsLoading = false;
                        if (BoardsRecyclerAdapter.getItemCount() <= 0)
                        {
                            LottieBoards_EmptyList.setVisibility(View.VISIBLE);
                            BoardsRecyclerView.setVisibility(View.GONE);
                        }
                    }
                    LoadingDialog.dismiss();
                }

                @Override
                public void onFailure(Call<ArrayList<BoardModel>> call, Throwable t)
                {
                    LoadingDialog.dismiss();
                    super.onFailure(call, t);
                }
            });
        }
    }

    private void LoadBoardsByCategory(int categoryId)
    {
        if (!IsDone)
        {
            LoadingDialog.show();
            Call<ArrayList<BoardModel>> GetBoardsByCategoryId = requestApi.GetBoardsByCategoryId(categoryId, SkipNumber);
            GetBoardsByCategoryId.enqueue(new ApiCallBack<ArrayList<BoardModel>>(this, GetBoardsByCategoryId)
            {
                @Override
                public void onResponse(@NonNull Call<ArrayList<BoardModel>> call, @NonNull Response<ArrayList<BoardModel>> response)
                {
                    if (response.body() != null && response.body().size() > 0)
                    {
                        BoardsRecyclerAdapter.RefreshAdapterData(response.body(), false);
                        LottieBoards_EmptyList.setVisibility(View.GONE);
                        BoardsRecyclerView.setVisibility(View.VISIBLE);
                        IsLoading = false;
                    }
                    else
                    {
                        IsDone = true;
                        IsLoading = false;
                        if (BoardsRecyclerAdapter.getItemCount() <= 0)
                        {
                            LottieBoards_EmptyList.setVisibility(View.VISIBLE);
                            BoardsRecyclerView.setVisibility(View.GONE);
                        }
                    }
                    LoadingDialog.dismiss();
                }

                @Override
                public void onFailure(Call<ArrayList<BoardModel>> call, Throwable t)
                {
                    LoadingDialog.dismiss();
                    super.onFailure(call, t);
                }
            });
        }
    }

    private void LoadBoardsByCollection(int collectionId)
    {
        if (!IsDone)
        {
            Call<ArrayList<CollectionModel>> GetBoardsByCollection = requestApi.LoadBoardsByCollectionId(CityCodeFilter, 0, 15, 0, 15, collectionId);
            GetBoardsByCollection.enqueue(new ApiCallBack<ArrayList<CollectionModel>>(this, GetBoardsByCollection)
            {
                @Override
                public void onResponse(@NonNull Call<ArrayList<CollectionModel>> call, @NonNull Response<ArrayList<CollectionModel>> response)
                {

                    if (response.body() != null && response.body().size() > 0)
                    {
                        BoardsRecyclerAdapter.RefreshAdapterData(response.body().get(0).ProductInfoList, false);
                        LottieBoards_EmptyList.setVisibility(View.GONE);
                        BoardsRecyclerView.setVisibility(View.VISIBLE);
                        IsLoading = false;
                    }
                    else
                    {
                        IsDone = true;
                        IsLoading = false;
                        if (BoardsRecyclerAdapter.getItemCount() <= 0)
                        {
                            LottieBoards_EmptyList.setVisibility(View.VISIBLE);
                            BoardsRecyclerView.setVisibility(View.GONE);
                        }
                    }
                    LoadingDialog.dismiss();
                }

                @Override
                public void onFailure(Call<ArrayList<CollectionModel>> call, Throwable t)
                {
                    LoadingDialog.dismiss();
                    super.onFailure(call, t);
                }
            });
        }
    }


}
