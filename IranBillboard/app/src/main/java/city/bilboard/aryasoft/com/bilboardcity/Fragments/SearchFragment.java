package city.bilboard.aryasoft.com.bilboardcity.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import java.util.ArrayList;
import city.bilboard.aryasoft.com.bilboardcity.Activities.MyApplication;
import city.bilboard.aryasoft.com.bilboardcity.Adapter.SimpleBoardAdapter;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiCallBack;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.BoardModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.ServiceGenerator;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.R;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Response;

public class SearchFragment extends Fragment
{
    private RecyclerView SearchRecyclerView = null;
    private EditText search_edt_SearchText = null;
    private SimpleBoardAdapter SearchRecyclerAdapter = null;
    private LinearLayout LottieSearch_EmptyList = null;
    private RequestApi requestApi = null;
    private cn.pedant.SweetAlert.SweetAlertDialog  LoadingDialog = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        requestApi = ServiceGenerator.createService(RequestApi.class);
        LoadingDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText("لطفا کمی صبر کنید...");
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        requestApi = MyApplication.GetRetrofitRequestApi();
        LottieSearch_EmptyList = view.findViewById(R.id.LottieSearch_EmptyList);
        search_edt_SearchText = view.findViewById(R.id.search_edt_SearchText);
        ImageButton btn_clean_search = view.findViewById(R.id.btn_clean_search);
        SearchRecyclerView = view.findViewById(R.id.SearchRecyclerView);
        ImageButton btn_search_search = view.findViewById(R.id.btn_search_search);
        SearchRecyclerAdapter = new SimpleBoardAdapter(view.getContext());
        GridLayoutManager searchLayoutManager = new GridLayoutManager(view.getContext(), 2);
        SearchRecyclerView.setLayoutManager(searchLayoutManager);
        SearchRecyclerView.setAdapter(SearchRecyclerAdapter);
        //-----------
        btn_search_search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LottieSearch_EmptyList.setVisibility(View.GONE);
                SearchRecyclerView.setVisibility(View.GONE);
                SearchBoards(search_edt_SearchText.getText().toString());
            }
        });
        btn_clean_search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                search_edt_SearchText.setText("");
            }
        });
    }

    private void SearchBoards(String SearchText)
    {
        LoadingDialog.show();
        Call<ArrayList<BoardModel>> Search = requestApi.Search(SearchText);
        Search.enqueue(new ApiCallBack<ArrayList<BoardModel>>(getContext(), Search)
        {
            @Override
            public void onResponse(Call<ArrayList<BoardModel>> call, Response<ArrayList<BoardModel>> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body().size() > 0)
                    {
                        LottieSearch_EmptyList.setVisibility(View.GONE);
                        SearchRecyclerView.setVisibility(View.VISIBLE);
                        SearchRecyclerAdapter.RefreshAdapterData(response.body(), true);
                        //------------------------------------------------------
                    }
                    else if (response.body().size() <= 0)
                    {
                        LottieSearch_EmptyList.setVisibility(View.VISIBLE);
                        SearchRecyclerView.setVisibility(View.GONE);
                    }
                    LoadingDialog.dismiss();
                }
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
