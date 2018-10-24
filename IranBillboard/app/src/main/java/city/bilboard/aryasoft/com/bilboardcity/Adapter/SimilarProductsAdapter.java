package city.bilboard.aryasoft.com.bilboardcity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.SimilarProductApiModel;
import city.bilboard.aryasoft.com.bilboardcity.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class SimilarProductsAdapter extends BaseAdapter
{
    private class MyViewHolder
    {
        CircleImageView imgSimilarBoardPhoto;
        TextView txtSimilarBoardName;
    }

    private Context _Context;
    private ArrayList<SimilarProductApiModel> _List;
    private MyViewHolder holder;

    public SimilarProductsAdapter(Context _Context, ArrayList<SimilarProductApiModel> list)
    {
        this._Context = _Context;
        this._List = list;
    }

    @Override
    public int getCount()
    {
        return _List.size();
    }

    @Override
    public Object getItem(int position)
    {
        return _List.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        if (row == null)
        {
            row = LayoutInflater.from(_Context).inflate(R.layout.similar_product_item_layout, parent, false);
            holder = new MyViewHolder();
        }
        initViews(row);

        Picasso.get().load(_Context.getString(R.string.BoardImageThumb)+_List.get(position).ImageName).error(R.drawable.no_img)
                .into(holder.imgSimilarBoardPhoto);
        holder.txtSimilarBoardName.setText(_List.get(position).BoardsTitle);
        return row;
    }

    private void initViews(View view)
    {
        holder.imgSimilarBoardPhoto = view.findViewById(R.id.img_similar_board_photo);
        holder.txtSimilarBoardName = view.findViewById(R.id.txt_similar_board_name);
    }
}
