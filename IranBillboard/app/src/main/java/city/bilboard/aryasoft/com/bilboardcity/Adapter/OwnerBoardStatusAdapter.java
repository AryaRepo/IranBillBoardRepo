package city.bilboard.aryasoft.com.bilboardcity.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import city.bilboard.aryasoft.com.bilboardcity.Activities.MyApplication;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiCallBack;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.BoardModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.ChangeReservationBoardApiModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.SharedPreferencesHelper;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.hamsaa.RtlMaterialSpinner;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;
import retrofit2.Call;
import retrofit2.Response;

public class OwnerBoardStatusAdapter extends RecyclerView.Adapter<OwnerBoardStatusAdapter.RecyclerViewHolder>
{

    private ArrayList<BoardModel> DataList;
    private Context _Context;
    private Dialog mydl;
    private boolean IsBeginDateSelected = false;
    private boolean IsEndDateSelected = false;
    private int State = 0;
    private TextView txt_begin_date, txt_end_date;
    private ChangeReservationBoardApiModel ReservationBoard = null;
    private RequestApi requestApi;
    private SweetAlertDialog LoadingDialog;

    public OwnerBoardStatusAdapter(Context context, SweetAlertDialog LoadingDialog)
    {
        this.DataList = new ArrayList<>();
        this._Context = context;
        requestApi = MyApplication.GetRetrofitRequestApi();
        this.LoadingDialog = LoadingDialog;
    }

    public void RefreshAdapterData(ArrayList<BoardModel> DataList)
    {
        this.DataList.addAll(DataList);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View RecyclerItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_data_item_layout, parent, false);
        return new RecyclerViewHolder(RecyclerItemView);
        //-------------------
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position)
    {
        holder.txtBillboardName.setText(DataList.get(position).BoardsTitle);
        holder.txtBillboardScore.setText(DataList.get(position).AveragePoint + " امتیاز ");
        holder.txtBillboardPrice.setText(DataList.get(position).Price + " تومان ");
        holder.txtBillboardType.setText(DataList.get(position).CategoryBoardTitle);
        Picasso.get().load(_Context.getString(R.string.BoardImagesFolder) + DataList.get(position).ImageName).into(holder.imgBillboardPhoto);
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

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (DataList.get(holder.getAdapterPosition()).SituationID == 1 || DataList.get(holder.getAdapterPosition()).SituationID == 2 || DataList.get(holder.getAdapterPosition()).SituationID == 3 || DataList.get(holder.getAdapterPosition()).SituationID == 4)
                {
                    showSetStatusDialog(DataList.get(holder.getAdapterPosition()).BoardsID, DataList.get(holder.getAdapterPosition()).SituationID);
                }
                else
                {
                    Toast.makeText(_Context, "تغییر وضعیت برای این تابلو مجاز نیست." + DataList.get(holder.getAdapterPosition()).SituationID, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showSetStatusDialog(final int BoardId, final int situationID)
    {
        IsBeginDateSelected = false;
        IsEndDateSelected = false;
        android.support.v7.app.AlertDialog.Builder AlertDefineBoardCondition = new android.support.v7.app.AlertDialog.Builder(_Context);
        AlertDefineBoardCondition.setCancelable(true);
        mydl = new Dialog(_Context);
        View AlertView = View.inflate(_Context, R.layout.dialog_define_board_condition, null);
        //--------
        final RtlMaterialSpinner sp_status_list = AlertView.findViewById(R.id.sp_status_list);
        final String[] Status = new String[]{"آزاد", "رزرو در حال بررسی (معلق)", "اجاره داده شده توسط مالک(در اختیار)"};
        txt_begin_date = AlertView.findViewById(R.id.txt_begin_date);
        txt_end_date = AlertView.findViewById(R.id.txt_end_date);
        final Button btn_begin_date = AlertView.findViewById(R.id.btn_begin_date);
        final Button btn_end_date = AlertView.findViewById(R.id.btn_end_date);
        ArrayAdapter<String> Adp = new ArrayAdapter<>(_Context, R.layout.my_spinner_item, Status);
        sp_status_list.setAdapter(Adp);
        ReservationBoard = new ChangeReservationBoardApiModel();
        sp_status_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                switch (i)
                {
                    case -1:
                        btn_begin_date.setVisibility(View.INVISIBLE);
                        btn_end_date.setVisibility(View.INVISIBLE);
                        break;
                    case 0:
                        btn_begin_date.setVisibility(View.INVISIBLE);
                        btn_end_date.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        btn_begin_date.setVisibility(View.VISIBLE);
                        btn_end_date.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        btn_begin_date.setVisibility(View.VISIBLE);
                        btn_end_date.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        //--------------------

        Button dlg_Btn_SaveChanges = AlertView.findViewById(R.id.dlg_Btn_SaveChanges);
        AlertDefineBoardCondition.setView(AlertView);
        mydl = AlertDefineBoardCondition.show();
        dlg_Btn_SaveChanges.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (sp_status_list.getSelectedItemPosition() == 0)
                {
                    Toast.makeText(_Context, "لطفا وضعیت را انتخاب کنید.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (sp_status_list.getSelectedItemPosition() != 1)
                {
                    if (!IsBeginDateSelected || !IsEndDateSelected)
                    {
                        Toast.makeText(_Context, "لطفا تاریخ شروع و پایان را مشخص کنید.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                switch (sp_status_list.getSelectedItemPosition())
                {
                    case 1:
                        State = 1;
                        ReservationBoard.startdate = "";
                        ReservationBoard.enddate = "";
                        break;
                    case 2:
                        State = 3;
                        break;
                    case 3:
                        State = 4;
                        break;
                }
                ReservationBoard.situationid = State;
                if (situationID == State)
                {
                    Toast.makeText(_Context, "لطفا وضعیت دیگری غیر از وضعیت فعلی انتخاب کنید", Toast.LENGTH_SHORT).show();
                    return;
                }
                ReservationBoard.boardsid = BoardId;
                ReservationBoard.UserId = SharedPreferencesHelper.ReadInt("UserID");
                //--------------------------------------
                LoadingDialog.show();

                Call<Boolean> ChangeReservationBoard = requestApi.ChangeReservationBoard(ReservationBoard);

                ChangeReservationBoard.enqueue(new ApiCallBack<Boolean>(_Context, ChangeReservationBoard)
                {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response)
                    {
                        LoadingDialog.dismiss();
                        if (response.isSuccessful())
                        {
                            if (response.body())
                            {
                                Toast.makeText(_Context, "وضعیت تابلوی مورد نظر تغییر یافت", Toast.LENGTH_SHORT).show();
                                //------------------------
                                RefreshBoards();
                            }
                            else
                            {
                                Toast.makeText(_Context, "مشکلی در ثبت وضعیت پیش آمده.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        mydl.dismiss();
                        //------------------------------------------------
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t)
                    {
                        Toast.makeText(_Context, t.getMessage()+"", Toast.LENGTH_SHORT).show();
                        mydl.dismiss();
                        LoadingDialog.dismiss();
                        super.onFailure(call, t);
                    }

                });


            }
        });
        btn_begin_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                SetBeginDateForBoard();
            }
        });
        btn_end_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                SetEndDateForBoard();
            }
        });

    }

    private void RefreshBoards()
    {
        LoadingDialog.show();
        Call<ArrayList<BoardModel>> getBoardListOfOwner = requestApi.GetBoardListOfOwner(SharedPreferencesHelper.ReadInt("UserID"), 0);
        getBoardListOfOwner.enqueue(new ApiCallBack<ArrayList<BoardModel>>(_Context, getBoardListOfOwner)
        {
            @Override
            public void onResponse(Call<ArrayList<BoardModel>> call, Response<ArrayList<BoardModel>> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body() != null && response.body().size() > 0)
                    {
                        DataList.clear();
                        DataList.addAll(response.body());
                        notifyDataSetChanged();
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

    private void SetBeginDateForBoard()
    {
        IsBeginDateSelected = false;
        PersianDatePickerDialog picker = new PersianDatePickerDialog(_Context)
                .setPositiveButtonString("باشه")
                .setNegativeButton("بیخیال")
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setActionTextColor(Color.GRAY)
                .setListener(new Listener()
                {
                    @Override
                    public void onDateSelected(PersianCalendar persianCalendar)
                    {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        ReservationBoard.startdate = HelperModule.arabicToDecimal(dateFormat.format(persianCalendar.getTime()) + "");
                        IsBeginDateSelected = true;
                        txt_begin_date.setText("تاریخ شروع :" + persianCalendar.getPersianLongDate() + "");
                    }

                    @Override
                    public void onDismissed()
                    {
                        ReservationBoard.startdate = "";
                        IsBeginDateSelected = false;
                        txt_begin_date.setText("");
                        txt_end_date.setText("");
                    }
                });

        picker.show();
    }

    private void SetEndDateForBoard()
    {
        IsEndDateSelected = false;
        PersianDatePickerDialog picker = new PersianDatePickerDialog(_Context)
                .setPositiveButtonString("باشه")
                .setNegativeButton("بیخیال")
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setActionTextColor(Color.GRAY)
                .setListener(new Listener()
                {
                    @Override
                    public void onDateSelected(PersianCalendar persianCalendar)
                    {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        ReservationBoard.enddate = HelperModule.arabicToDecimal(dateFormat.format(persianCalendar.getTime()) + "");
                        IsEndDateSelected = true;
                        txt_end_date.setText("تاریخ پایان :" + persianCalendar.getPersianLongDate() + "");
                        txt_begin_date.setVisibility(View.VISIBLE);
                        txt_end_date.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onDismissed()
                    {
                    }
                });

        picker.show();
    }

    @Override
    public int getItemCount()
    {
        return DataList.size();
    }

    //---------------------------------------------------------VIEW HOLDER--------------------------
    class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private TextView txtBillboardName;
        private TextView txtBillboardState;
        private TextView txtBillboardScore;
        private TextView txtBillboardType;
        private ImageView imgBillboardPhoto;
        private TextView txtBillboardPrice;

        RecyclerViewHolder(View itemView)
        {
            super(itemView);
            txtBillboardName = itemView.findViewById(R.id.txt_board_name);
            txtBillboardState = itemView.findViewById(R.id.txt_board_state);
            txtBillboardScore = itemView.findViewById(R.id.txt_board_score);
            txtBillboardType = itemView.findViewById(R.id.txt_board_type);
            imgBillboardPhoto = itemView.findViewById(R.id.img_board_photo);
            txtBillboardPrice = itemView.findViewById(R.id.txt_board_price);

        }
    }
//---------------------------------------------------------VIEW HOLDER------------------------------
}
