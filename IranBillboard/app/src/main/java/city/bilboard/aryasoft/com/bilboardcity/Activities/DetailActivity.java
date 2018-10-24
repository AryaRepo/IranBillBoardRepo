package city.bilboard.aryasoft.com.bilboardcity.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.orm.SugarRecord;
import org.lucasr.twowayview.TwoWayView;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import city.bilboard.aryasoft.com.bilboardcity.Adapter.SimilarProductsAdapter;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiCallBack;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.BoardDetailModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.ResOfRentApiModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.ReserveBoardsApiModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.SimilarProductApiModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.DataBaseContext.favorites;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.Slider.DepthPageTransformer;
import city.bilboard.aryasoft.com.bilboardcity.Slider.SliderPagerAdapter;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.VectorDrawablePreLollipopHelper;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.VectorView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private SweetAlertDialog LoadingDialog;
    private RequestApi requestApi;
    private Timer SchedulerTask;
    private TextView detailed_txt_min;
    private TextView detailed_txt_hour;
    private TextView detailed_txt_day;
    private TextView detailed_txt_sec;
    private TextView detailed_txt_price;
    private TextView detail_txt_DoesHaveLightness;
    private TextView detailed_txt_category;
    private TextView detailed_txt_board_info;
    private TextView detailed_txt_percent;
    private TextView detailed_BoardStyleAreaText;
    private TextView detailed_txt_address;
    private Button detailed_btn_ReserveDemand;
    private NestedScrollView detail_scrollview;
    private ViewPager BoardSlider = null;
    private DecoView arcView;
    private SupportMapFragment mapFragment;
    private CircleIndicator BoardSliderIndicator;
    private ViewSwitcher DetailSwitcher;
    private TwoWayView similar_boards_list;
    private SliderPagerAdapter mPager;
    private String BoardInfoText = " تابلویی با ابعاد %1s  و حالت %2s دارد و دارای %3s وجه می باشد و با مساحت %4s می باشد";
    private String CategoryTitle = "";
    private String BoardsTitle = "";
    private String BoardPrice = "";
    private String Latitude = "";
    private String Longitude = "";
    private int counting = 0;
    private int BoardID = 0;
    private int ChartPercent = 0;
    private int UserId = -1;
    private int SituationId = 0;
    //------------------------------------
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
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        GetIntentAndPreferences();
        Initializer();
    }

    private void GetIntentAndPreferences()
    {
        BoardID = getIntent().getIntExtra("BoardID", 0);
        CategoryTitle = getIntent().getStringExtra("CategoryTitle");
        BoardsTitle = getIntent().getStringExtra("BoardsTitle");
        BoardPrice = getIntent().getStringExtra("BoardPrice");
        SituationId = getIntent().getIntExtra("SituationId", 0);
        UserId = getIntent().getIntExtra("UserID", 0);
        requestApi = MyApplication.GetRetrofitRequestApi();
    }

    private void Initializer()
    {
        Toolbar appToolbar = findViewById(R.id.DetailedToolBarInclude);
        BoardSlider = findViewById(R.id.slider);
        BoardSliderIndicator = findViewById(R.id.Sliderindicator);
        appToolbar.setTitle(null);
        setSupportActionBar(appToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        //--------------------------------------
        LoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText("لطفا کمی صبر کنید...");
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        //----------------------------------
        DetailSwitcher = findViewById(R.id.detail_switcher);
        ArrayList<VectorView> vectorsList = new ArrayList<VectorView>();
        detailed_btn_ReserveDemand = findViewById(R.id.detailed_btn_ReserveDemand);
        detailed_BoardStyleAreaText = findViewById(R.id.detailed_BoardStyleAreaText);
        arcView = findViewById(R.id.dynamicArcView);
        similar_boards_list = findViewById(R.id.similar_boards_list);
        detailed_txt_day = findViewById(R.id.detail_day_txt);
        detailed_txt_hour = findViewById(R.id.detail_hour_txt);
        detailed_txt_min = findViewById(R.id.detail_min_txt);
        detailed_txt_sec = findViewById(R.id.detail_sec_txt);
        ImageButton detail_btn_Fav = findViewById(R.id.detail_btn_Fav);
        detailed_txt_category = findViewById(R.id.detailed_txt_category);
        detailed_txt_address = findViewById(R.id.detailed_txt_address);
        detailed_txt_price = findViewById(R.id.detailed_txt_price);
        TextView txt_Waiting_users = findViewById(R.id.txt_Waiting_users);
        detailed_txt_board_info = findViewById(R.id.detailed_txt_board_info);
        detailed_txt_percent = findViewById(R.id.detailed_txt_percent);
        detail_scrollview = findViewById(R.id.detail_scrollview);
        detail_txt_DoesHaveLightness = findViewById(R.id.detail_txt_DoesHaveLightness);
        vectorsList.add(new VectorView(R.drawable.ic_credit_card, detailed_btn_ReserveDemand, VectorDrawablePreLollipopHelper.MyDirType.end));
        vectorsList.add(new VectorView(R.drawable.ic_coins, detailed_txt_price, VectorDrawablePreLollipopHelper.MyDirType.end));
        vectorsList.add(new VectorView(R.drawable.ic_users_group, txt_Waiting_users, VectorDrawablePreLollipopHelper.MyDirType.end));
        vectorsList.add(new VectorView(R.drawable.ic_board_category, detailed_txt_category, VectorDrawablePreLollipopHelper.MyDirType.end));
        vectorsList.add(new VectorView(R.drawable.ic_sun, detail_txt_DoesHaveLightness, VectorDrawablePreLollipopHelper.MyDirType.end));
        VectorDrawablePreLollipopHelper.SetVectors(getResources(), vectorsList);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview1);
        detail_btn_Fav.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        AddBoardToFavourite();
                    }
                }
        );
        detailed_btn_ReserveDemand.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(final View v)
            {
                if (HelperModule.IsUser())
                {
                    ReserveBoard();
                }
                else
                {
                    HelperModule.ShowSignInAlert(v.getContext());
                }

            }
        });
        PrepareDetail();
    }

    private void PrepareDetail()
    {
        DetailSwitcher.setDisplayedChild(0);
        Call<ArrayList<BoardDetailModel>> GetBoardDetail = requestApi.GetBoardDetail(BoardID);
        GetBoardDetail.enqueue(new ApiCallBack<ArrayList<BoardDetailModel>>(this, GetBoardDetail)
        {
            @Override
            public void onResponse(@NonNull Call<ArrayList<BoardDetailModel>> call, @NonNull Response<ArrayList<BoardDetailModel>> response)
            {
                if (response.isSuccessful())
                {
                    BoardDetailModel BoardDetail = response.body().get(0);
                    mPager = new SliderPagerAdapter(getApplicationContext(), BoardDetail.Images);
                    BoardSlider.setPageTransformer(true, new DepthPageTransformer());
                    BoardSlider.setAdapter(mPager);
                    BoardSliderIndicator.setViewPager(BoardSlider);
                    //----------------
                    ChartPercent = BoardDetail.TimeRemainingtoPercent;
                    detailed_txt_day.setText(BoardDetail.DayRemaining + "");
                    detailed_txt_hour.setText(BoardDetail.HoursRemaining + "");
                    detailed_txt_min.setText(BoardDetail.MinutesRemaining + "");
                    detailed_txt_sec.setText(BoardDetail.SecondsRemaining + "");
                    detailed_txt_category.setText(CategoryTitle);
                    if (BoardDetail.Light)
                    {
                        detail_txt_DoesHaveLightness.setText("روشنایی دارد");
                    }
                    else
                    {
                        detail_txt_DoesHaveLightness.setText("روشنایی ندارد");
                    }
                    if (BoardDetail.Price != 0)
                    {
                        detailed_txt_price.setText(BoardDetail.Price + " تومان ");
                        BoardPrice = BoardDetail.Price + "";
                    }
                    else
                    {
                        detailed_txt_price.setText("قیمت ثبت نشده");
                    }
                    BoardInfoText = String.format(BoardInfoText, BoardDetail.Dimensions, BoardDetail.StyleType, "4", BoardDetail.Area);
                    detailed_BoardStyleAreaText.setText(BoardInfoText);
                    detailed_txt_address.setText
                            (
                                    BoardDetail.State + " - " + BoardDetail.City + " - " + Html.fromHtml(BoardDetail.BoardsAddress)
                            );
                    detailed_txt_board_info.setText(Html.fromHtml(BoardDetail.Description));
                    Latitude = BoardDetail.Latitude;
                    Longitude = BoardDetail.Longitude;
                    mapFragment.getMapAsync(DetailActivity.this);
                    //-------------------------------------
                    if (SituationId == 1 || SituationId == 2)
                    {
                        detailed_btn_ReserveDemand.setVisibility(View.VISIBLE);
                    }
                    LoadSimilarBoards();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BoardDetailModel>> call, Throwable t)
            {
                super.onFailure(call, t);
                DetailSwitcher.setDisplayedChild(1);
            }
        });


    }

    private void LoadSimilarBoards()
    {
        detail_scrollview.scrollTo(0, 0);
        Call<ArrayList<SimilarProductApiModel>> GetSimilarBoards = requestApi.GetSimilarBoards(BoardID);
        GetSimilarBoards.enqueue(new ApiCallBack<ArrayList<SimilarProductApiModel>>(DetailActivity.this, GetSimilarBoards)
        {
            @Override
            public void onResponse(Call<ArrayList<SimilarProductApiModel>> call, final Response<ArrayList<SimilarProductApiModel>> response)
            {
                if (response.body() != null)
                {
                    SimilarProductsAdapter productsAdapter = new SimilarProductsAdapter(DetailActivity.this, response.body());
                    similar_boards_list.setAdapter(productsAdapter);
                }
                else
                {
                    SimilarProductsAdapter productsAdapter = new SimilarProductsAdapter(DetailActivity.this, new ArrayList<SimilarProductApiModel>());
                    similar_boards_list.setAdapter(productsAdapter);
                }

                PerformAnimations();
                ManageTimeCounter();
                DetailSwitcher.setDisplayedChild(1);
                similar_boards_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        BoardID = response.body().get(i).BoardsID;

                        PrepareDetail();
                    }
                });
            }

            @Override
            public void onFailure(Call<ArrayList<SimilarProductApiModel>> call, Throwable t)
            {
                if (LoadingDialog.isShowing())
                {
                    LoadingDialog.dismiss();
                }
                super.onFailure(call, t);
            }
        });

    }


    private void ReserveBoard()
    {
        LoadingDialog.show();
        ReserveBoardsApiModel ReserveRequestModel = new ReserveBoardsApiModel();
        ReserveRequestModel.AddBoardId(BoardID);
        ReserveRequestModel.SetUserId(UserId);
        Call<ArrayList<ResOfRentApiModel>> ReserveBoards = requestApi.ReserveBoards(ReserveRequestModel);
        ReserveBoards.enqueue(new ApiCallBack<ArrayList<ResOfRentApiModel>>(this, ReserveBoards)
        {
            @Override
            public void onResponse(@NonNull Call<ArrayList<ResOfRentApiModel>> call, @NonNull Response<ArrayList<ResOfRentApiModel>> response)
            {
                LoadingDialog.dismiss();
                if (response.isSuccessful())
                {

                    if (response.body() != null)
                    {
                        ResOfRentApiModel Result = response.body().get(0);
                        if (Result.ResRent)
                        {
                            Toast.makeText(DetailActivity.this, "کاربر عزیز درخواست رزرو موقتا ثبت گردید.منتظر تایید از سمت راهبر سیستم باشید.", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(DetailActivity.this, "کاربر عزیز رزرو شما قبلا برای این تابلو درخواست رزرو کردید", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ResOfRentApiModel>> call, Throwable t)
            {
                LoadingDialog.dismiss();
                super.onFailure(call, t);
            }
        });
    }

    private void AddBoardToFavourite()
    {
        int FavoritesCount = SugarRecord.listAll(favorites.class).size();
        if (FavoritesCount < 5)
        {
            favorites tbFavorites;
            if (FavoritesCount == 0)
            {
                tbFavorites = new favorites(BoardID, BoardsTitle, CategoryTitle, BoardPrice);
                tbFavorites.save();
                Toast.makeText(DetailActivity.this, "تابلوی  مورد نظر به لیست علاقمندی ها اضافه گردید", Toast.LENGTH_LONG).show();
            }
            else if (FavoritesCount > 0)
            {
                List<favorites> favs = favorites.findWithQuery(favorites.class, "Select * from favorites where boardid = ?", BoardID + "");
                if (favs.size() == 0)
                {
                    tbFavorites = new favorites(BoardID, BoardsTitle, CategoryTitle, BoardPrice);
                    tbFavorites.save();
                    Toast.makeText(DetailActivity.this, "تابلوی  مورد نظر به لیست علاقمندی ها اضافه گردید", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(DetailActivity.this, "تابلوی در لیست علاقمندی های شما موجود است", Toast.LENGTH_LONG).show();
                }
            }
        }
        else if (FavoritesCount == 5)
        {
            Toast.makeText(DetailActivity.this, "حد مجاز علاقمندی های شما سر رسیده است.", Toast.LENGTH_SHORT).show();
        }
    }

    private void PerformAnimations()
    {
        Handler MainTrd = new Handler();
        Thread MainJob = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                YoYo.with(Techniques.Shake)
                        .duration(1500)
                        .repeat(-1)
                        .playOn(findViewById(R.id.detailed_img_TimeLeft));
                SeriesItem seriesItem1 = new SeriesItem.Builder(getResources().getColor(R.color.colorAccent))
                        .setRange(0, 100, 0)
                        .setLineWidth(32f)
                        .build();
                arcView.addSeries(seriesItem1);

                RotateAnimation ChartAnimation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
                DecelerateInterpolator interpolator = new DecelerateInterpolator();
                ChartAnimation.setInterpolator(interpolator);
                ChartAnimation.setDuration(1500);
                ChartAnimation.setRepeatCount(0);
                arcView.setAnimation(ChartAnimation);
                arcView.addEvent(new DecoEvent.Builder(ChartPercent).setIndex(0).setDelay(1600).build());
                YoYo.with(Techniques.FadeIn)
                        .duration(1000)
                        .repeat(0)
                        .playOn(findViewById(R.id.detailed_txt_percent));
                final Handler ThreadInterFace = new Handler();
                Timer LifTime = new Timer();
                final Runnable Job = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (counting < ChartPercent)
                        {
                            detailed_txt_percent.setText(++counting + " %");
                        }
                    }
                };
                LifTime.schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        ThreadInterFace.post(Job);
                    }
                }, 50, 50);
            }
        });

        MainTrd.post(MainJob);
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        LatLng BoardCoordinate = new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude));
        googleMap.addMarker(new MarkerOptions().position(BoardCoordinate).title("مکان تابلو"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BoardCoordinate, 5));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {
                startActivity(new Intent(DetailActivity.this, BoardMapActivityDetail.class).putExtra("Latitude", Latitude).putExtra("Longitude", Longitude));
            }
        });

    }

    private void ManageTimeCounter()
    {
        if (SchedulerTask != null)
        {
            SchedulerTask.cancel();
            SchedulerTask = null;
        }
        SchedulerTask = new Timer();
        SchedulerTask.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int s = Integer.parseInt(detailed_txt_sec.getText().toString());
                        int m = Integer.parseInt(detailed_txt_min.getText().toString());
                        int h = Integer.parseInt(detailed_txt_hour.getText().toString());
                        int d = Integer.parseInt(detailed_txt_day.getText().toString());
                        //------------------------
                        if (s - 1 >= 0)
                        {
                            detailed_txt_sec.setText(--s + "");
                            if (s == 0)
                            {
                                if (m - 1 >= 0)
                                {
                                    detailed_txt_min.setText(--m + "");
                                    detailed_txt_sec.setText("59");
                                    if (m == 0)
                                    {
                                        if (h - 1 >= 0)
                                        {
                                            detailed_txt_hour.setText(--h + "");
                                            detailed_txt_min.setText("59");
                                            if (h == 0)
                                            {
                                                if (d - 1 >= 0)
                                                {
                                                    detailed_txt_day.setText(--d + "");
                                                    detailed_txt_hour.setText("23");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }, 0, 1000);
    }

}

