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
import city.bilboard.aryasoft.com.bilboardcity.Models.AdminModel;
import city.bilboard.aryasoft.com.bilboardcity.R;
import retrofit2.Call;
import retrofit2.Response;


public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminRecyclerViewHolder>
{

    private ArrayList<AdminModel> DataList ;
    private Context AppContext;
    private cn.pedant.SweetAlert.SweetAlertDialog loadingDialog;
    private android.app.Dialog BoardInfoDlg;
    private RequestApi requestApi;
    private android.support.v7.app.AlertDialog.Builder BoardInfoAlert;
    private  String Latitude;
    private String Longitude;
    public AdminAdapter(Context context, cn.pedant.SweetAlert.SweetAlertDialog loadingDialog,RequestApi requestApi)
    {
        this.DataList = new ArrayList<>();
        this.AppContext = context;
        this.requestApi = requestApi;
        this.loadingDialog = loadingDialog;
        this.BoardInfoAlert = new android.support.v7.app.AlertDialog.Builder(context);
    }

    @Override
    public AdminRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View RecyclerItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_list_layout, parent, false);
        return new AdminAdapter.AdminRecyclerViewHolder(RecyclerItemView);
    }

    private void ShowDetailDialog(int BoardId)
    {
        if (!loadingDialog.isShowing())
        {
            View AlertView = View.inflate(this.AppContext, R.layout.board_detail_layout, null);
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
                    AppContext.startActivity(new Intent(AppContext,BoardMapActivityDetail.class).putExtra("Latitude",Latitude).putExtra("Longitude",Longitude));
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
            getBoardDetailQuickInfo.enqueue(new ApiCallBack<ArrayList<BoardDetailQuickInfoModel>>(this.AppContext, getBoardDetailQuickInfo)
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
                    Latitude=response.body().get(0).Latitude;
                    Longitude=response.body().get(0).Longitude;
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
    public void onBindViewHolder(final AdminRecyclerViewHolder holder, int position)
    {
        holder.txtBillboardPhone_Admin.setText("تلفن رزرو کننده : " + DataList.get(position).MobileNumber);
        Picasso.get().load(AppContext.getString(R.string.BoardImagesFolder) + DataList.get(position).ImageName).into(holder.imgBillboardPhoto_Admin);
        holder.btn_board_detail_admin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ShowDetailDialog(DataList.get(holder.getAdapterPosition()).BoardsID);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return DataList.size();
    }

    public void RefreshAdapterData(ArrayList<AdminModel> DataList)
    {
        this.DataList.addAll(DataList);
        this.notifyDataSetChanged();
    }


    //---------------------------------------------------------VIEW HOLDER--------------------------
    class AdminRecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private TextView txtBillboardPhone_Admin;
        private ImageView imgBillboardPhoto_Admin;
        private Button btn_board_detail_admin;
        AdminRecyclerViewHolder(View itemView)
        {
            super(itemView);
            btn_board_detail_admin= itemView.findViewById(R.id.btn_board_detail_admin);
            txtBillboardPhone_Admin = itemView.findViewById(R.id.txt_billboard_phone_number_admin);
            imgBillboardPhoto_Admin = itemView.findViewById(R.id.img_billboard_photo_admin);
        }
    }
//---------------------------------------------------------VIEW HOLDER------------------------------
}
