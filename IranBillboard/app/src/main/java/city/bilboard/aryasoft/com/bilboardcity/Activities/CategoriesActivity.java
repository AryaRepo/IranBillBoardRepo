package city.bilboard.aryasoft.com.bilboardcity.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiCallBack;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.BoardsCategory;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CategoriesActivity extends AppCompatActivity
{
    private LayoutInflater layoutInflater ;
    private GridView CategoriesListView ;
    private TextView txt_CategoryName;
    private ArrayList<BoardsCategory> CategoryList ;
    private ViewSwitcher category_switcher = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        CategoriesListView = findViewById(R.id.CategoriesListView);
        RequestApi requestApi = MyApplication.GetRetrofitRequestApi();
        Toolbar categoriesActivityToolbar = findViewById(R.id.CategoriesToolbarInclude);
        categoriesActivityToolbar.setTitle("دسته بندی های تابلو");
        setSupportActionBar(categoriesActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layoutInflater = LayoutInflater.from(this);
        category_switcher=findViewById(R.id.category_switcher);
        //------------------------------------------
        Call<ArrayList<BoardsCategory>> GetBoardCategories = requestApi.GetBoardCategories();
        GetBoardCategories.enqueue(new ApiCallBack<ArrayList<BoardsCategory>>(this, GetBoardCategories)
        {
            @Override
            public void onResponse(Call<ArrayList<BoardsCategory>> call, Response<ArrayList<BoardsCategory>> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body().size() > 0 && response.body() != null)
                    {
                        CategoryList = new ArrayList<>();
                        CategoryList.addAll(response.body());
                        PopulateDataToListView();
                        category_switcher.setDisplayedChild(1);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BoardsCategory>> call, Throwable t)
            {
                category_switcher.setDisplayedChild(1);
                super.onFailure(call, t);
            }
        });
        CategoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent BoardCategoryIntent = new Intent(CategoriesActivity.this, BoardsActivity.class);
                BoardCategoryIntent.putExtra("CategoryId", CategoryList.get(position).categoryboardid);
                startActivity(BoardCategoryIntent);
            }
        });

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


    private void PopulateDataToListView()
    {
        ArrayAdapter<BoardsCategory> categoriesAdapter = new ArrayAdapter<BoardsCategory>(this, R.layout.categories_item_layout)
        {
            @Override
            public int getCount()
            {
                return CategoryList.size();
            }

            @Override
            public BoardsCategory getItem(int position)
            {
                return CategoryList.get(position);
            }

            @Override
            public long getItemId(int position)
            {
                return position;
            }

            @NonNull
            @Override
            public View getView(final int position, View convertView, @NonNull ViewGroup parent)
            {
                View MyView = convertView;
                if (convertView == null)
                {
                    MyView = layoutInflater.inflate(R.layout.categories_item_layout, null);
                }
                txt_CategoryName = MyView.findViewById(R.id.txt_CategoryName);
                txt_CategoryName.setText(CategoryList.get(position).categoryboardtitle);


                return MyView;
            }
        };
        CategoriesListView.setAdapter(categoriesAdapter);

    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
