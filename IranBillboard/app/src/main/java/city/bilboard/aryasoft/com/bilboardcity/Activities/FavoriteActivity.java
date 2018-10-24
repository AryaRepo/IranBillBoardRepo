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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

import city.bilboard.aryasoft.com.bilboardcity.DataBaseContext.favorites;
import city.bilboard.aryasoft.com.bilboardcity.Models.FavoriteModel;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FavoriteActivity extends AppCompatActivity
{
    private ArrayAdapter<FavoriteModel> FavoriteAdapter = null;
    private GridView FavoriteListItem = null;
    private LayoutInflater layoutInflater = null;
    private TextView Favorite_txtBoardName = null;
    private TextView Favorite_txtCategoryTitle = null;
    private TextView Favorite_txtBoardPrice = null;
    private ArrayList<FavoriteModel> FavoriteDataList = null;
    private ImageButton Favorite_btn_Delete = null;
    private Context ListViewContext = null;
    private RelativeLayout LottieFavorite_EmptyList = null;
    private Toolbar FavoriteToolbar = null;
    private Button Favorite_btn_ShowDetail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        FavoriteToolbar = findViewById(R.id.FavoriteToolbarInclude);
        FavoriteToolbar.setTitle("علاقمندی های شما");
        setSupportActionBar(FavoriteToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        HelperModule.ShowLoading(this);
        FavoriteListItem = findViewById(R.id.FavoriteListItem);
        LottieFavorite_EmptyList = findViewById(R.id.LottieFavorite_EmptyList);
        ListViewContext = this;
        layoutInflater = LayoutInflater.from(this);
        //-----
        FavoriteModel favoriteModel = null;
        FavoriteDataList = new ArrayList<>();
        List<favorites> favs = favorites.findWithQuery(favorites.class, "Select * from favorites");
        if (favs.size() > 0)
        {
            for (int i = 0; i < favs.size(); ++i)
            {
                favoriteModel = new FavoriteModel();
                favoriteModel.Favorite_BoardID = favs.get(i).boardid;
                favoriteModel.Favorite_BoardName = favs.get(i).boardname;
                favoriteModel.Favorite_CategoryTitle = favs.get(i).boardcategory;
                favoriteModel.Favorite_BoardPrice = favs.get(i).boardprice;
                FavoriteDataList.add(favoriteModel);
            }
        }
        if (FavoriteDataList.size() > 0)
        {
            FavoriteListItem.setVisibility(View.VISIBLE);
            LottieFavorite_EmptyList.setVisibility(View.GONE);
            PopulateDataToListView();
            HelperModule.HideLoading();
        }
        else if (FavoriteDataList.size() == 0)
        {
            HelperModule.HideLoading();
            FavoriteListItem.setVisibility(View.GONE);
            LottieFavorite_EmptyList.setVisibility(View.VISIBLE);
        }
    }

    private void PopulateDataToListView()
    {
        FavoriteAdapter = new ArrayAdapter<FavoriteModel>(this, R.layout.favorite_item_layout_container)
        {
            @Override
            public int getCount()
            {
                return FavoriteDataList.size();
            }

            @Override
            public FavoriteModel getItem(int position)
            {
                return FavoriteDataList.get(position);
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
                    MyView = layoutInflater.inflate(R.layout.favorite_item_layout_container, null);
                }
                Favorite_txtBoardName = MyView.findViewById(R.id.Favorite_txtBoardName);
                Favorite_txtCategoryTitle = MyView.findViewById(R.id.Favorite_txtCategoryTitle);
                Favorite_txtBoardPrice = MyView.findViewById(R.id.Favorite_txtBoardPrice);
                Favorite_btn_Delete = MyView.findViewById(R.id.Favorite_btn_Delete);
                Favorite_btn_ShowDetail = MyView.findViewById(R.id.Favorite_btn_ShowDetail);
                //---------------
                Favorite_txtBoardName.setText(FavoriteDataList.get(position).Favorite_BoardName);
                Favorite_txtCategoryTitle.setText(FavoriteDataList.get(position).Favorite_CategoryTitle);
                Favorite_txtBoardPrice.setText(FavoriteDataList.get(position).Favorite_BoardPrice+" تومان ");
                //----------------
                Favorite_btn_ShowDetail.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(ListViewContext, DetailActivity.class);
                        intent.putExtra("BoardID", FavoriteDataList.get(position).Favorite_BoardID);
                        intent.putExtra("CategoryTitle", FavoriteDataList.get(position).Favorite_CategoryTitle);
                        intent.putExtra("BoardsTitle", FavoriteDataList.get(position).Favorite_BoardName);
                        intent.putExtra("BoardPrice", FavoriteDataList.get(position).Favorite_BoardPrice);
                        ListViewContext.startActivity(intent);
                        finish();
                    }
                });
                Favorite_btn_Delete.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        android.support.v7.app.AlertDialog.Builder DeleteFavoriteAlert = new android.support.v7.app.AlertDialog.Builder(ListViewContext);
                        DeleteFavoriteAlert.setCancelable(false);
                        View AlertView = View.inflate(ListViewContext, R.layout.dialog_notify, null);
                        TextView NotifyDialogTitle = AlertView.findViewById(R.id.Notifydialog_title);
                        NotifyDialogTitle.setText("کاربر گرامی آیا مایل به حذف از لیست علاقمندی ها هستید؟");
                        DeleteFavoriteAlert.setView(AlertView);
                        DeleteFavoriteAlert.setPositiveButton("بله", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                FavoriteModel toRemove = getItem(position);
                                favorites.executeQuery("delete from favorites where boardid=?", toRemove.Favorite_BoardID+"");
                                FavoriteDataList.remove(toRemove);
                                FavoriteAdapter.remove(toRemove);
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
        FavoriteListItem.setAdapter(FavoriteAdapter);
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
