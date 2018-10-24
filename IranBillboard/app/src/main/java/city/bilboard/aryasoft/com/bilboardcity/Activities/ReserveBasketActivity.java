package city.bilboard.aryasoft.com.bilboardcity.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import java.util.ArrayList;
import java.util.List;

import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.DataBaseContext.basket;
import city.bilboard.aryasoft.com.bilboardcity.Models.BasketModel;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReserveBasketActivity extends AppCompatActivity
{
    private ArrayAdapter<BasketModel> BasketAdapter = null;
    private GridView BasketListItem = null;
    private Toolbar BasketToolbar = null;
    private LottieAnimationView LottieBasket_EmptyList = null;
    private LayoutInflater layoutInflater = null;
    private TextView Basket_txtBoardName = null;
    private TextView Basket_txtCategoryTitle = null;
    private TextView Basket_txtBoardPrice = null;
    private Context BasketContext = null;
    private ArrayList<BasketModel> BasketDataList = null;
    private Button Basket_btn_ShowDetail = null;
    private ImageButton Basket_btn_Delete = null;
    private Button ReserveBasket_btn_reserve = null;
    private RequestApi requestApi = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_basket);
        requestApi = MyApplication.GetRetrofitRequestApi();
        //------------------
        BasketToolbar = (Toolbar) findViewById(R.id.BasketToolbarInclude);
        BasketToolbar.setTitle("سبد رزرو شما");
        setSupportActionBar(BasketToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        HelperModule.ShowLoading(this);
        //--------------------
        BasketListItem = (GridView) findViewById(R.id.BasketListItem);
        ReserveBasket_btn_reserve = (Button) findViewById(R.id.ReserveBasket_btn_reserve);
        LottieBasket_EmptyList = (LottieAnimationView) findViewById(R.id.LottieBasket_EmptyList);
        BasketContext = this;
        layoutInflater = LayoutInflater.from(this);
        //---------------

        BasketModel basketModel = null;
        BasketDataList = new ArrayList<>();
        List<basket> baskets = basket.findWithQuery(basket.class, "Select * from basket");
        if (baskets.size() > 0)
        {
            for (int i = 0; i < baskets.size(); ++i)
            {
                basketModel = new BasketModel();
                basketModel.Basket_BoardID = baskets.get(i).boardid;
                basketModel.Basket_BoardName = baskets.get(i).boardname;
                basketModel.Basket_CategoryTitle = baskets.get(i).boardcategory;
                basketModel.Basket_BoardPrice = baskets.get(i).boardprice;
                BasketDataList.add(basketModel);
            }
            BasketListItem.setVisibility(View.VISIBLE);
            ReserveBasket_btn_reserve.setVisibility(View.VISIBLE);
            LottieBasket_EmptyList.setVisibility(View.GONE);
            PopulateDataToListView();
            HelperModule.HideLoading();
        }

        else if (baskets.size() == 0)
        {
            HelperModule.HideLoading();
            BasketListItem.setVisibility(View.GONE);
            ReserveBasket_btn_reserve.setVisibility(View.GONE);
            LottieBasket_EmptyList.setVisibility(View.VISIBLE);

        }
        ReserveBasket_btn_reserve.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Integer[] ReserveArray = new Integer[BasketDataList.size()];
                for (int i = 0; i < BasketDataList.size(); ++i)
                {
                    ReserveArray[i] = BasketDataList.get(i).Basket_BoardID;
                }
                //------------
                if (HelperModule.IsUser())
                {
                  /*  Call<Boolean> ReserveBoards = requestApi.ReserveBoards(ReserveArray, HelperModule.GetUserID(v.getContext()));
                    ReserveBoards.enqueue(new Callback<Boolean>()
                    {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response)
                        {
                            HelperModule.HideLoading();
                            if (response.isSuccessful())
                            {
                                if (response.body())
                                {
                                    Toast.makeText(ReserveBasketActivity.this, "درخواست رزرو برای تابلوها ارسال شد.", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(ReserveBasketActivity.this, "درخواست رزرو با مشکل مواجه شد", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t)
                        {
                            HelperModule.HideLoading();
//                            if (t instanceof TimeoutError || t instanceof NoConnectionError || t instanceof UnknownHostException)
//                            {
//                                Constants.InternetDisconnectedDialog(ReserveBasketActivity.this);
//                            }
                        }
                    });
                    */
                }
            }
        });
    }

    private void PopulateDataToListView()
    {
        BasketAdapter = new ArrayAdapter<BasketModel>(this, R.layout.basket_item_layout_container)
        {
            @Override
            public int getCount()
            {
                return BasketDataList.size();
            }

            @Override
            public BasketModel getItem(int position)
            {
                return BasketDataList.get(position);
            }

            @Override
            public long getItemId(int position)
            {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent)
            {
                View MyView = convertView;
                if (convertView == null)
                {
                    MyView = layoutInflater.inflate(R.layout.basket_item_layout_container, null);
                }
                Basket_txtBoardName = (TextView) MyView.findViewById(R.id.Basket_txtBoardName);
                Basket_txtCategoryTitle = (TextView) MyView.findViewById(R.id.Basket_txtCategoryTitle);
                Basket_txtBoardPrice = (TextView) MyView.findViewById(R.id.Basket_txtBoardPrice);
                Basket_btn_Delete = (ImageButton) MyView.findViewById(R.id.Basket_btn_Delete);
                Basket_btn_ShowDetail = (Button) MyView.findViewById(R.id.Basket_btn_ShowDetail);
                //---------------
                Basket_txtBoardName.setText(BasketDataList.get(position).Basket_BoardName);
                Basket_txtCategoryTitle.setText(BasketDataList.get(position).Basket_CategoryTitle);
                Basket_txtBoardPrice.setText(BasketDataList.get(position).Basket_BoardPrice);
                //----------------
                Basket_btn_ShowDetail.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(BasketContext, DetailActivity.class);
                        intent.putExtra("BoardID", BasketDataList.get(position).Basket_BoardID);
                        intent.putExtra("CategoryTitle", BasketDataList.get(position).Basket_CategoryTitle);
                        intent.putExtra("BoardsTitle", BasketDataList.get(position).Basket_BoardName);
                        intent.putExtra("BoardPrice", BasketDataList.get(position).Basket_BoardPrice);
                        BasketContext.startActivity(intent);
                        finish();
                    }
                });
                Basket_btn_Delete.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //Delete From DataBase
                        //Delete From FavoriteDataList
                        //Delete From ListView
                        //Toast.makeText(FavoriteActivity.this, "pos: "+position, Toast.LENGTH_SHORT).show();
                        //FavoriteAdapter
                        android.support.v7.app.AlertDialog.Builder DeleteFavoriteAlert = new android.support.v7.app.AlertDialog.Builder(BasketContext);
                        DeleteFavoriteAlert.setCancelable(false);
                        View AlertView = View.inflate(BasketContext, R.layout.dialog_notify, null);
                        TextView NotifyDialogTitle = (TextView) AlertView.findViewById(R.id.Notifydialog_title);
                        NotifyDialogTitle.setText("کاربر گرامی آیا مایل به حذف از لیست سبد رزرو  هستید؟");
                        DeleteFavoriteAlert.setView(AlertView);
                        DeleteFavoriteAlert.setPositiveButton("بله", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                BasketModel toRemove = getItem(position);
                                basket.executeQuery("delete from basket where boardid=?", toRemove.Basket_BoardID + "");
                                BasketDataList.remove(toRemove);
                                BasketAdapter.remove(toRemove);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });

                        DeleteFavoriteAlert.setNegativeButton("خیر", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });
                        //--------------------
                        DeleteFavoriteAlert.show();
                    }
                });

                return MyView;
            }
        };
        BasketListItem.setAdapter(BasketAdapter);
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
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
