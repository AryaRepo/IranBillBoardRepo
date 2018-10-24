package city.bilboard.aryasoft.com.bilboardcity.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import city.bilboard.aryasoft.com.bilboardcity.Activities.BoardMapActivityDetail;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiCallBack;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.BoardDetailQuickInfoModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.Models.OwnerModel;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Response;

public class OwnerAdapter extends RecyclerView.Adapter<OwnerAdapter.OwnerRecyclerViewHolder>
{
    private ArrayList<OwnerModel> DataList;
    private RequestApi requestApi;
    private SweetAlertDialog loadingDialog;
    private Context context;
    private android.support.v7.app.AlertDialog.Builder BoardInfoAlert;
    private Dialog BoardInfoDlg;
    private String Latitude;
    private String Longitude;
    public OwnerAdapter(Context context, RequestApi requestApi, SweetAlertDialog loadingDialog)
    {
        this.requestApi = requestApi;
        this.DataList = new ArrayList<>();
        this.loadingDialog = loadingDialog;
        this.context = context;
        this.BoardInfoAlert = new android.support.v7.app.AlertDialog.Builder(context);
    }

    private void ShowDetailDialog(int BoardId)
    {
        if (!loadingDialog.isShowing())
        {
            View  AlertView = View.inflate( this.context, R.layout.board_detail_layout, null);
            BoardInfoAlert.setCancelable(false);
            final ImageButton btn_close_board_info = AlertView.findViewById(R.id.btn_close_board_info);
            final TextView board_detail_price = AlertView.findViewById(R.id.board_detail_price);
            final TextView board_detail_state = AlertView.findViewById(R.id.board_detail_state);
            final TextView board_detail_city = AlertView.findViewById(R.id.board_detail_city);
            final TextView board_detail_description = AlertView.findViewById(R.id.board_detail_description);
            final TextView board_detail_address = AlertView.findViewById(R.id.board_detail_address);
            final Button btn_show_board_map = AlertView.findViewById(R.id.btn_show_board_map);
            btn_show_board_map.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    BoardInfoDlg.dismiss();
                    context.startActivity(new Intent(context,BoardMapActivityDetail.class).putExtra("Latitude",Latitude).putExtra("Longitude",Longitude));
                }
            });
            BoardInfoAlert.setView(AlertView);
            btn_close_board_info.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    BoardInfoDlg.dismiss();
                }
            });
            //-----------------------------
            loadingDialog.show();
            Call<ArrayList<BoardDetailQuickInfoModel>> getBoardDetailQuickInfo = requestApi.GetBoardDetailQuickInfo(BoardId);
            getBoardDetailQuickInfo.enqueue(new ApiCallBack<ArrayList<BoardDetailQuickInfoModel>>(context, getBoardDetailQuickInfo)
            {
                @Override
                public void onResponse(Call<ArrayList<BoardDetailQuickInfoModel>> call, Response<ArrayList<BoardDetailQuickInfoModel>> response)
                {
                    Latitude=response.body().get(0).Latitude;
                    Longitude=response.body().get(0).Longitude;
                    //-----------------------------
                    board_detail_price.setText("قیمت تابلو : " + response.body().get(0).Price+"تومان");
                    board_detail_state.setText("استان : " + response.body().get(0).State);
                    board_detail_city.setText("شهر : " + response.body().get(0).City);
                    board_detail_description.setText("توضیحات : " + Html.fromHtml( response.body().get(0).Description).toString().replaceFirst("\\s+$", ""));
                    board_detail_address.setText("آدرس : " + Html.fromHtml(response.body().get(0).BoardsAddress)+"".replaceAll(" ",""));
                    //-----------------------------------
                    BoardInfoDlg = BoardInfoAlert.show();
                    loadingDialog.dismiss();
                }

                @Override
                public void onFailure(Call<ArrayList<BoardDetailQuickInfoModel>> call, Throwable t)
                {
                    loadingDialog.dismiss();
                    super.onFailure(call, t);
                }
            });
        }

    }

    @Override
    public OwnerRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View RecyclerItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_item_layout, parent, false);
        return new OwnerAdapter.OwnerRecyclerViewHolder(RecyclerItemView);
    }

    public void RefreshAdapterData(ArrayList<OwnerModel> DataList)
    {
        this.DataList.addAll(DataList);
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final OwnerRecyclerViewHolder holder, int position)
    {
        holder.txt_RentedByOwner_TxtBoardEndDate.setText(DataList.get(position).DateEnd);
        holder.txt_RentedByOwner_TxtBoardBeginDate.setText(DataList.get(position).DateStart);
       // holder.txt_RentedByOwner_TxtBoardBeginDate.setText(DataList.get(position).DateStart);
        Picasso.get().load(context.getString(R.string.BoardImagesFolder) + DataList.get(position).ImageName).into(holder.img_RentedByOwner_ImgBoard);
        holder.btn_board_detail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //open dialog
                ShowDetailDialog(DataList.get(holder.getAdapterPosition()).BoardsID);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return DataList.size();
    }


    //---------------------------------------------------------VIEW HOLDER--------------------------
    class OwnerRecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private TextView txt_RentedByOwner_TxtBoardEndDate;
        private TextView txt_RentedByOwner_TxtBoardBeginDate;
        private ImageView img_RentedByOwner_ImgBoard;
        private Button btn_board_detail;

        private OwnerRecyclerViewHolder(View itemView)
        {
            super(itemView);
            txt_RentedByOwner_TxtBoardEndDate = itemView.findViewById(R.id.RentedByOwner_TxtBoardEndDate);
            txt_RentedByOwner_TxtBoardBeginDate = itemView.findViewById(R.id.RentedByOwner_TxtBoardBeginDate);
            img_RentedByOwner_ImgBoard = itemView.findViewById(R.id.RentedByOwner_ImgBoard);
            btn_board_detail = itemView.findViewById(R.id.btn_board_detail_owner);
        }
    }
//---------------------------------------------------------VIEW HOLDER------------------------------
}
