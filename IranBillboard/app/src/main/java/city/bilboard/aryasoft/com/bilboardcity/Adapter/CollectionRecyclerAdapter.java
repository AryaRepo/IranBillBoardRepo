package city.bilboard.aryasoft.com.bilboardcity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import city.bilboard.aryasoft.com.bilboardcity.Activities.BoardsActivity;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.CollectionModel;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.VectorDrawablePreLollipopHelper;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.VectorView;

public class CollectionRecyclerAdapter extends RecyclerView.Adapter<CollectionRecyclerAdapter.RecyclerViewHolder>
{

    private ArrayList<CollectionModel> CollectionList;
    private Context context;
    private int CityCodeFilter;

    public CollectionRecyclerAdapter(Context context,int cityCodeFilter)
    {
        this.context = context;
        this.CollectionList = new ArrayList<>();
        this.CityCodeFilter=cityCodeFilter;

    }

    public void AddCollectionDataList(ArrayList<CollectionModel> CollectionDataList)
    {
        this.CollectionList.addAll(CollectionDataList);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View RecyclerItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_container_template, parent, false);
        return new RecyclerViewHolder(RecyclerItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position)
    {
        holder.txt_collection_title.setText(CollectionList.get(position).CollectionTypeTitle);
        SimpleBoardAdapter boardAdapter = new SimpleBoardAdapter(context);
        holder.rec_collection.setAdapter(boardAdapter);
        boardAdapter.RefreshAdapterData(CollectionList.get(position).ProductInfoList, true);
        VectorDrawablePreLollipopHelper.SetVectors(context.getResources(), new VectorView(R.drawable.chevron_left, holder.btn_show_more_collection, VectorDrawablePreLollipopHelper.MyDirType.start));
        //------------
        holder.btn_show_more_collection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ///show more data
                Intent BoardIntent = new Intent(v.getContext(), BoardsActivity.class);
                BoardIntent.putExtra("CityCodeFilter", CityCodeFilter);
                BoardIntent.putExtra("SectionType", CollectionList.get(holder.getAdapterPosition()).CollectionTypeID);
                context.startActivity(BoardIntent);
            }
        });
    }


    @Override
    public int getItemCount()
    {
        return CollectionList.size();
    }

    //---------------------------------------------------------VIEW HOLDER--------------------------
    class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private TextView txt_collection_title;
        private TextView btn_show_more_collection;
        private RecyclerView rec_collection;

        private RecyclerViewHolder(View itemView)
        {
            super(itemView);
            //------------------
            txt_collection_title = itemView.findViewById(R.id.txt_collection_title);
            btn_show_more_collection = itemView.findViewById(R.id.btn_show_more_collection);
            rec_collection = itemView.findViewById(R.id.rec_collection);

        }
    }
//---------------------------------------------------------VIEW HOLDER------------------------------
}
