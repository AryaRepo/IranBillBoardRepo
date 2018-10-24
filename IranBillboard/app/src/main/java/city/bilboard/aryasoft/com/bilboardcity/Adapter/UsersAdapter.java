package city.bilboard.aryasoft.com.bilboardcity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import city.bilboard.aryasoft.com.bilboardcity.Activities.BoardMapActivityDetail;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiCallBack;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.BoardDetailQuickInfoModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.Models.UserModel;
import city.bilboard.aryasoft.com.bilboardcity.R;;
import retrofit2.Call;
import retrofit2.Response;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserRecyclerViewHolder>
{
    private ArrayList<UserModel> DataList;
    private android.app.Dialog BoardInfoDlg;
    private Context context;
    private cn.pedant.SweetAlert.SweetAlertDialog loadingDialog;
    private android.support.v7.app.AlertDialog.Builder BoardInfoAlert;
    private RequestApi requestApi;

    public UsersAdapter(Context context, cn.pedant.SweetAlert.SweetAlertDialog loadingDialog, RequestApi requestApi)
    {
        this.context = context;
        this.loadingDialog = loadingDialog;
        this.DataList = new ArrayList<>();
        this.requestApi = requestApi;
        this.BoardInfoAlert = new android.support.v7.app.AlertDialog.Builder(context);
    }

    @Override
    public UserRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View RecyclerItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.special_user_item_layout, parent, false);
        return new UsersAdapter.UserRecyclerViewHolder(RecyclerItemView);
    }

    String Latitude;
    String Longitude;

    private void ShowDetailDialog(int BoardId)
    {
        if (!loadingDialog.isShowing())
        {
            View AlertView = View.inflate(this.context, R.layout.board_detail_layout, null);
            BoardInfoAlert.setCancelable(false);
            final android.widget.ImageButton btn_close_board_info = AlertView.findViewById(R.id.btn_close_board_info);
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
                    context.startActivity(new Intent(context, BoardMapActivityDetail.class).putExtra("Latitude", Latitude).putExtra("Longitude", Longitude));
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
            getBoardDetailQuickInfo.enqueue(new ApiCallBack<ArrayList<BoardDetailQuickInfoModel>>(this.context, getBoardDetailQuickInfo)
            {
                @Override
                public void onResponse(Call<ArrayList<BoardDetailQuickInfoModel>> call, Response<ArrayList<BoardDetailQuickInfoModel>> response)
                {
                    //-----------------------------
                    board_detail_price.setText("قیمت تابلو : " + response.body().get(0).Price + "تومان");
                    board_detail_state.setText("استان : " + response.body().get(0).State);
                    board_detail_city.setText("شهر : " + response.body().get(0).City);
                    board_detail_description.setText("توضیحات : " + Html.fromHtml(response.body().get(0).Description).toString().replaceFirst("\\s+$", ""));
                    board_detail_address.setText("آدرس : " + Html.fromHtml(response.body().get(0).BoardsAddress) + "".replaceAll(" ", ""));
                    //-----------------------------------
                    BoardInfoDlg = BoardInfoAlert.show();
                    loadingDialog.dismiss();
                    Latitude = response.body().get(0).Latitude;
                    Longitude = response.body().get(0).Longitude;
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
    public void onBindViewHolder(final UserRecyclerViewHolder holder, int position)
    {
        // holder.txt_RentedByUser_TxtBoardName.setText(DataList.get(position).BoardsTitle);//Change It
        holder.txt_RentedByUser_TxtBoardBeginDate.setText(holder.txt_RentedByUser_TxtBoardBeginDate.getText() + DataList.get(position).DateStart);
        holder.txt_RentedByUser_TxtBoardEndDate.setText(holder.txt_RentedByUser_TxtBoardEndDate.getText() + DataList.get(position).DateEnd);
        Picasso.get().load(context.getString(R.string.BoardImagesFolder) + DataList.get(position).ImageName).into(holder.img_RentedByUser_ImgBoard);
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ShowDetailDialog(DataList.get(holder.getAdapterPosition()).BoardsID);
            }
        });
    }

    public void RefreshAdapterData(ArrayList<UserModel> DataList)
    {
        this.DataList.addAll(DataList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return DataList.size();
    }

    //---------------------------------------------------------VIEW HOLDER--------------------------
    class UserRecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private TextView txt_RentedByUser_TxtBoardName;
        private TextView txt_RentedByUser_TxtBoardBeginDate;
        private TextView txt_RentedByUser_TxtBoardEndDate;
        private ImageView img_RentedByUser_ImgBoard;

        private UserRecyclerViewHolder(View itemView)
        {
            super(itemView);
            txt_RentedByUser_TxtBoardName = itemView.findViewById(R.id.RentedByUser_TxtBoardName);
            txt_RentedByUser_TxtBoardBeginDate = itemView.findViewById(R.id.RentedByUser_TxtBoardBeginDate);
            txt_RentedByUser_TxtBoardEndDate = itemView.findViewById(R.id.RentedByUser_TxtBoardEndDate);
            img_RentedByUser_ImgBoard = itemView.findViewById(R.id.RentedByUser_ImgBoard);
        }
    }
//---------------------------------------------------------VIEW HOLDER------------------------------
}
