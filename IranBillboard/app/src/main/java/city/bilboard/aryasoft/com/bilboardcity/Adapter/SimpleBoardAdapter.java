package city.bilboard.aryasoft.com.bilboardcity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.*;

import java.util.ArrayList;

import city.bilboard.aryasoft.com.bilboardcity.Activities.DetailActivity;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.BoardModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.CollectionModel;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.SharedPreferencesHelper;

public class SimpleBoardAdapter extends RecyclerView.Adapter<SimpleBoardAdapter.RecyclerViewHolder>
{

    private ArrayList<BoardModel> DataList;
    private Context _Context;

    public SimpleBoardAdapter(Context context)
    {
        this.DataList = new ArrayList<>();
        _Context = context;
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View RecyclerItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_data_item_layout, parent, false);
        return new RecyclerViewHolder(RecyclerItemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position)
    {
        if (DataList.size() > 0)
        {
            holder.txtBillboardName.setText(DataList.get(position).BoardsTitle);
            holder.txtBillboardType.setText(DataList.get(position).CategoryBoardTitle);
            holder.txtBillboardScore.setText(DataList.get(position).AveragePoint + " امتیاز ");
            if (!DataList.get(position).Price.equals("0"))
            {
                holder.txt_board_price.setText(DataList.get(position).Price + " تومان ");
            }
            else
            {
                holder.txt_board_price.setText("قیمت ثبت نشده");

            }
            if (DataList.get(position).SituationID != -1)
            {
                if (DataList.get(position).SituationID == 1)
                {
                    holder.txtBillboardState.setText("آزاد");
                }
                else
                {
                    holder.txtBillboardState.setText("رزرو");
                }
            }

            Picasso.get().load(_Context.getString(R.string.BoardImagesFolder) + DataList.get(position).ImageName).into(holder.imgBillboardPhoto);
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(_Context, DetailActivity.class);
                    intent.putExtra("BoardID", DataList.get(holder.getAdapterPosition()).BoardsID);
                    intent.putExtra("SituationId", DataList.get(holder.getAdapterPosition()).SituationID);
                    intent.putExtra("BoardsTitle", DataList.get(holder.getAdapterPosition()).BoardsTitle);
                    intent.putExtra("CategoryTitle", DataList.get(holder.getAdapterPosition()).CategoryBoardTitle);
                    intent.putExtra("BoardPrice", DataList.get(holder.getAdapterPosition()).Price);
                    intent.putExtra("UserID", SharedPreferencesHelper.ReadInt("UserID"));
                    _Context.startActivity(intent);

                }
            });
        }

    }



    public void RefreshAdapterData(ArrayList<BoardModel> DataList, boolean ShouldClear)
    {
        if (ShouldClear)
        {
            this.DataList.clear();
        }
        if (DataList.size() <= 0)
        {
            this.DataList = DataList;
        }
        else
        {
            this.DataList.addAll(DataList);
        }
        this.notifyDataSetChanged();
    }

    public void ClearAdapterData()
    {
        this.DataList.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return DataList.size();
    }

    //---------------------------------------------------------VIEW HOLDER--------------------------
    class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private TextView txtBillboardName = null;
        private TextView txtBillboardState = null;
        private TextView txtBillboardScore = null;
        private TextView txtBillboardType = null;
        private ImageView imgBillboardPhoto = null;
        private TextView txt_board_price = null;

        private RecyclerViewHolder(View itemView)
        {
            super(itemView);
            txtBillboardName = (TextView) itemView.findViewById(R.id.txt_board_name);
            txtBillboardState = (TextView) itemView.findViewById(R.id.txt_board_state);
            txtBillboardScore = (TextView) itemView.findViewById(R.id.txt_board_score);
            txtBillboardType = (TextView) itemView.findViewById(R.id.txt_board_type);
            imgBillboardPhoto = (ImageView) itemView.findViewById(R.id.img_board_photo);
            txt_board_price = (TextView) itemView.findViewById(R.id.txt_board_price);
        }
    }
//---------------------------------------------------------VIEW HOLDER------------------------------
}
