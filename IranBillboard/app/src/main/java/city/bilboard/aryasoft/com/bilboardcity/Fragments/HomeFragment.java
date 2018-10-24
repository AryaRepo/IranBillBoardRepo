package city.bilboard.aryasoft.com.bilboardcity.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import city.bilboard.aryasoft.com.bilboardcity.Activities.BoardsActivity;
import city.bilboard.aryasoft.com.bilboardcity.Activities.CategoriesActivity;
import city.bilboard.aryasoft.com.bilboardcity.Activities.MyApplication;
import city.bilboard.aryasoft.com.bilboardcity.Adapter.CollectionRecyclerAdapter;
import city.bilboard.aryasoft.com.bilboardcity.Adapter.SimpleBoardAdapter;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiCallBack;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.BoardModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.BoardsCategory;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.CollectionModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.SliderModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.Models.CityModel;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.Slider.DepthPageTransformer;
import city.bilboard.aryasoft.com.bilboardcity.Slider.HomeSliderPagerAdapter;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.VectorDrawablePreLollipopHelper;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.VectorView;
import ir.hamsaa.RtlMaterialSpinner;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Response;

public class HomeFragment extends Fragment
{
    private RequestApi requestApi;
    private TextView txtCat1, txtCat2, txtCat3, txtCat4, txtCatMore;
    private TextView btnShowMoreFav;
    private ViewSwitcher HomeSwitcher;
    private static int currentPage = 0;
    private View mView;
    private ImageButton btnOpenMenu;
    private ImageButton btn_filter_lists;
    private CardView cardHomeToolbar;
    private CardView cardCat;

    //---------------------------------------
    private int CityCodeFilter = 0;
    private ArrayList<CityModel> CityList;
    private RtlMaterialSpinner sp_city;
    private int CityListPosition = 0;
    private TextView txt_filtered_city;
    //===========
    private ScrollView homeScrollview;
    private RecyclerView recFavourite;
    private RecyclerView recCollections;
    private RelativeLayout relFav;
    private SimpleBoardAdapter BoardsAdapter;
    private CollectionRecyclerAdapter homeCollectionsRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        requestApi = MyApplication.GetRetrofitRequestApi();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        initViews();
        setVectors();
        LoadCategories();
        btnOpenMenu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.scale_up_frg, 0);
                ft.replace(R.id.main_placeholder, new MenuFragment());
                ft.commit();
            }
        });
        btn_filter_lists.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ShowFilterDialog(v);
            }
        });
    }

    private void initViews()
    {
        HomeSwitcher = mView.findViewById(R.id.home_switcher);
        HomeSwitcher.setDisplayedChild(1);
        recFavourite = mView.findViewById(R.id.rec_favourite);
        recCollections = mView.findViewById(R.id.rec_collections);
        relFav = mView.findViewById(R.id.rel_fav);
        //--------------------
        homeScrollview = mView.findViewById(R.id.home_scrollview);
        btnOpenMenu = mView.findViewById(R.id.btn_menu);
        btn_filter_lists = mView.findViewById(R.id.btn_filter_lists);
        txt_filtered_city = mView.findViewById(R.id.txt_filtered_city);
        txtCat1 = mView.findViewById(R.id.txt_cat1);
        txtCat2 = mView.findViewById(R.id.txt_cat2);
        txtCat3 = mView.findViewById(R.id.txt_cat3);
        txtCat4 = mView.findViewById(R.id.txt_cat4);
        txtCatMore = mView.findViewById(R.id.txt_cat_more);
        btnShowMoreFav = mView.findViewById(R.id.btn_show_more_fav);
        cardHomeToolbar = mView.findViewById(R.id.home_toolbar);
        cardCat = mView.findViewById(R.id.card_cat);
        cardHomeToolbar.setMaxCardElevation(0);
        cardCat.setMaxCardElevation(0);
        btnShowMoreFav.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent BoardIntent = new Intent(v.getContext(), BoardsActivity.class);
                BoardIntent.putExtra("CityCodeFilter", CityCodeFilter);
                BoardIntent.putExtra("SectionType", 0);//0 for favorite other than 0 is for collectionsId
                startActivity(BoardIntent);
            }
        });
    }


    private void setVectors()
    {
        ArrayList<VectorView> vectorsList = new ArrayList<>();
        vectorsList.add(new VectorView(R.drawable.ic_mybill_cat5, txtCat1, VectorDrawablePreLollipopHelper.MyDirType.top));
        vectorsList.add(new VectorView(R.drawable.ic_mybill_cat6, txtCat2, VectorDrawablePreLollipopHelper.MyDirType.top));
        vectorsList.add(new VectorView(R.drawable.ic_mybill_cat3, txtCat3, VectorDrawablePreLollipopHelper.MyDirType.top));
        vectorsList.add(new VectorView(R.drawable.ic_mybill_cat4, txtCat4, VectorDrawablePreLollipopHelper.MyDirType.top));
        vectorsList.add(new VectorView(R.drawable.ic_more, txtCatMore, VectorDrawablePreLollipopHelper.MyDirType.top));
        vectorsList.add(new VectorView(R.drawable.chevron_left, btnShowMoreFav, VectorDrawablePreLollipopHelper.MyDirType.start));
        VectorDrawablePreLollipopHelper.SetVectors(getResources(), vectorsList);
    }

    private void ShowFilterDialog(View v)
    {
        final AppCompatDialog DLG;
        android.support.v7.app.AlertDialog.Builder CityFilterAlert = new android.support.v7.app.AlertDialog.Builder(v.getContext());
        View AlertView = View.inflate(v.getContext(), R.layout.filter_layout, null);
        CityFilterAlert.setView(AlertView);
        DLG = CityFilterAlert.show();
        sp_city = AlertView.findViewById(R.id.sp_city_lists);
        Button submit_filter = AlertView.findViewById(R.id.submit_filter);
        //--------------------
        CityList = HelperModule.GetCities(v.getContext());
        String[] CityNames = new String[CityList.size()];
        for (int i = 0; i < CityList.size(); ++i)
        {
            CityNames[i] = CityList.get(i).CityName;
        }
        ArrayAdapter<String> Adp = new ArrayAdapter<>(v.getContext(), R.layout.city_item_drop_down, CityNames);
        sp_city.setAdapter(Adp);
        sp_city.setSelection(CityListPosition, true);
        //--------------------
        submit_filter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                HomeSwitcher.setDisplayedChild(1);
                //-----------------
                CityListPosition = sp_city.getSelectedItemPosition();
                if (sp_city.getSelectedItemPosition() == 0)
                {
                    CityCodeFilter = 0;
                    txt_filtered_city.setText("");
                }
                else
                {
                    CityCodeFilter = CityList.get(CityListPosition - 1).CityCode;
                    txt_filtered_city.setText("  شهر " + CityList.get(CityListPosition - 1).CityName);
                }
                GetFavouriteBoards();
                DLG.dismiss();
            }
        });
    }


    private void LoadCategories()
    {
        final Call<ArrayList<BoardsCategory>> GetBoardCategories = requestApi.GetBoardCategories();
        GetBoardCategories.enqueue(new ApiCallBack<ArrayList<BoardsCategory>>(getContext(), GetBoardCategories)
        {
            @Override
            public void onResponse(Call<ArrayList<BoardsCategory>> call, Response<ArrayList<BoardsCategory>> response)
            {
                if (response.isSuccessful())
                {
                    try
                    {
                        txtCat1.setText(response.body().get(0).categoryboardtitle);
                        txtCat1.setTag(response.body().get(0).categoryboardid);
                        txtCat1.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent BoardCategoryIntent = new Intent(v.getContext(), BoardsActivity.class);
                                BoardCategoryIntent.putExtra("CityCodeFilter", CityCodeFilter);
                                BoardCategoryIntent.putExtra("CategoryId", ((int) txtCat1.getTag()));
                                startActivity(BoardCategoryIntent);
                            }
                        });
                        //-----------------------------Second
                        txtCat2.setText(response.body().get(1).categoryboardtitle);
                        txtCat2.setTag(response.body().get(1).categoryboardid);
                        txtCat2.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent BoardCategoryIntent = new Intent(v.getContext(), BoardsActivity.class);
                                BoardCategoryIntent.putExtra("CityCodeFilter", CityCodeFilter);
                                BoardCategoryIntent.putExtra("CategoryId", ((int) txtCat2.getTag()));
                                startActivity(BoardCategoryIntent);
                            }
                        });
                        //-----------------------------Third
                        txtCat3.setText(response.body().get(2).categoryboardtitle);
                        txtCat3.setTag(response.body().get(2).categoryboardid);
                        txtCat3.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent BoardCategoryIntent = new Intent(v.getContext(), BoardsActivity.class);
                                BoardCategoryIntent.putExtra("CategoryId", ((int) txtCat3.getTag()));
                                BoardCategoryIntent.putExtra("CityCodeFilter", CityCodeFilter);
                                startActivity(BoardCategoryIntent);
                            }
                        });
                        //-----------------------------Forth
                        txtCat4.setText(response.body().get(3).categoryboardtitle);
                        txtCat4.setTag(response.body().get(3).categoryboardid);
                        txtCat4.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent BoardCategoryIntent = new Intent(v.getContext(), BoardsActivity.class);
                                BoardCategoryIntent.putExtra("CategoryId", ((int) txtCat4.getTag()));
                                BoardCategoryIntent.putExtra("CityCodeFilter", CityCodeFilter);
                                startActivity(BoardCategoryIntent);
                            }
                        });
                        txtCatMore.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                startActivity(new Intent(v.getContext(), CategoriesActivity.class));
                            }
                        });
                        SetupSlider();

                    } catch (Exception exp)
                    {
                        HomeSwitcher.setDisplayedChild(0);
                    }
                }
                else
                {
                    HomeSwitcher.setDisplayedChild(0);
                    HelperModule.NoDataReceivedDialog(mView.getContext());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BoardsCategory>> call, Throwable t)
            {
                HomeSwitcher.setDisplayedChild(0);
                super.onFailure(call, t);
            }
        });
    }

    private void GetFavouriteBoards()
    {
        final Call<ArrayList<BoardModel>> GetFavouriteBoards = requestApi.GetFavouriteBoards(CityCodeFilter, 0, 15);
        GetFavouriteBoards.enqueue(new ApiCallBack<ArrayList<BoardModel>>(getContext(), GetFavouriteBoards)
        {
            @Override
            public void onResponse(Call<ArrayList<BoardModel>> call, Response<ArrayList<BoardModel>> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body().size() == 0)
                    {
                        relFav.setVisibility(View.GONE);
                        GetCollections();
                        return;
                    }
                    BoardsAdapter = new SimpleBoardAdapter(getContext());
                    recFavourite.setAdapter(BoardsAdapter);
                    BoardsAdapter.RefreshAdapterData(response.body() == null ? new ArrayList<BoardModel>() : response.body(), false);
                    relFav.setVisibility(View.VISIBLE);
                    recFavourite.setVisibility(View.VISIBLE);

                }
                else
                {
                    HomeSwitcher.setDisplayedChild(0);
                    HelperModule.NoDataReceivedDialog(mView.getContext());
                }
                GetCollections();
            }

            @Override
            public void onFailure(Call<ArrayList<BoardModel>> call, Throwable t)
            {
                HomeSwitcher.setDisplayedChild(0);
                super.onFailure(call, t);
            }
        });
    }

    private void GetCollections()
    {
        Call<ArrayList<CollectionModel>> GetBoardsByCollection = requestApi.GetBoardsByCollection(CityCodeFilter, 0, 15, 0, 15);
        GetBoardsByCollection.enqueue(new ApiCallBack<ArrayList<CollectionModel>>(getContext(), GetBoardsByCollection)
        {
            @Override
            public void onResponse(Call<ArrayList<CollectionModel>> call, Response<ArrayList<CollectionModel>> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body().size() == 0)
                    {
                        recCollections.setVisibility(View.GONE);
                        return;
                    }
                    homeCollectionsRecyclerAdapter = new CollectionRecyclerAdapter(getContext(), CityCodeFilter);
                    recCollections.setAdapter(homeCollectionsRecyclerAdapter);
                    homeCollectionsRecyclerAdapter.AddCollectionDataList(response.body());
                    recCollections.setVisibility(View.VISIBLE);
                    homeScrollview.fullScroll(ScrollView.FOCUS_UP);
                    homeScrollview.smoothScrollTo(0, 0);
                }
                else
                {
                    HelperModule.NoDataReceivedDialog(mView.getContext());
                }
                HomeSwitcher.setDisplayedChild(0);
            }

            @Override
            public void onFailure(Call<ArrayList<CollectionModel>> call, Throwable t)
            {
                HomeSwitcher.setDisplayedChild(0);
                super.onFailure(call, t);
            }
        });
    }


    private void SetupSlider()
    {
        final ViewPager viewpager = mView.findViewById(R.id.slider);
        final CircleIndicator indicator = mView.findViewById(R.id.Sliderindicator);
        final Call<ArrayList<SliderModel>> LoadSlider = requestApi.LoadSlider();
        LoadSlider.enqueue(new ApiCallBack<ArrayList<SliderModel>>(getContext(), LoadSlider)
        {
            @Override
            public void onResponse(Call<ArrayList<SliderModel>> call, Response<ArrayList<SliderModel>> response)
            {
                ArrayList<SliderModel> SliderList = new ArrayList<SliderModel>();
                SliderList.addAll(response.body());
                HomeSliderPagerAdapter mPager = new HomeSliderPagerAdapter(mView.getContext(), SliderList);
                viewpager.setPageTransformer(true, new DepthPageTransformer());
                viewpager.setAdapter(mPager);
                indicator.setViewPager(viewpager);
                final Handler handler = new Handler();
                final Runnable Update = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (currentPage == viewpager.getAdapter().getCount())
                        {
                            currentPage = 0;
                        }
                        viewpager.setCurrentItem(currentPage++, true);
                    }
                };

                Timer swipeTimer = new Timer();
                swipeTimer.schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        handler.post(Update);
                    }
                }, 5000, 5000);
                GetFavouriteBoards();
            }

            @Override
            public void onFailure(Call<ArrayList<SliderModel>> call, Throwable t)
            {
                HomeSwitcher.setDisplayedChild(0);
                super.onFailure(call, t);
            }
        });

    }

}
