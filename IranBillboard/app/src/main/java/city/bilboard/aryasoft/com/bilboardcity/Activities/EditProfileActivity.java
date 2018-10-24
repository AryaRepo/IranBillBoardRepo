package city.bilboard.aryasoft.com.bilboardcity.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiCallBack;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.ProfileModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.ServiceGenerator;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.RequestApi;
import city.bilboard.aryasoft.com.bilboardcity.Models.CityModel;
import city.bilboard.aryasoft.com.bilboardcity.Models.StateCityModel;
import city.bilboard.aryasoft.com.bilboardcity.Models.UserProfileModel;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.MiladiToShamsi;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.SharedPreferencesHelper;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditProfileActivity extends AppCompatActivity
{
    private Spinner Profile_sp_states = null;
    private Spinner Profile_Sp_City = null;
    //--==============================
    private Button btn_chooseImage = null;
    private Button btn_ChooseBirthDate = null;
    private TextView txt_BirthDate = null;
    private EditText Profile_Edt_Name = null;
    private EditText Profile_Edt_Family = null;
    private EditText Profile_Edt_FirstPhoneNumber = null;
    private EditText Profile_Edt_SecondPhoneNumber = null;
    private EditText Profile_edt_Mobile = null;
    private EditText Profile_edt_Email = null;
    private EditText Profile_edt_Address = null;
    private EditText Profile_edt_about_me = null;
    //===============
    private RequestApi requestApi = null;
    private ArrayList<StateCityModel> StateCityList = null;
    private ArrayAdapter<String> CityAdapter = null;
    private int StateCode = -1;
    private int CityCode = -1;
    ArrayList<String> CityList = null;
    private byte[] Picture = null;
    private de.hdodenhof.circleimageview.CircleImageView Profile_img_profile = null;
    private ProfileModel profileModel = null;
    private String BirthDay= "";
    private int CityCodePosition = 0;
    private SweetAlertDialog LoadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestApi = ServiceGenerator.createService(RequestApi.class);
        setContentView(R.layout.manage_profile);
        LoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText("لطفا کمی صبر کنید...");
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        InitViews();
        initEvents();
        LoadUserInfo();
    }

    private void initEvents()
    {
        btn_chooseImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!(ActivityCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED))
                {
                    requestCameraPermission();
                }
                else
                {
                    OpenGallery();
                }
            }
        });
        btn_ChooseBirthDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PersianDatePickerDialog picker = new PersianDatePickerDialog(EditProfileActivity.this)
                        .setPositiveButtonString("باشه")
                        .setNegativeButton("بیخیال")
                        .setMinYear(1320)
                        .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                        .setActionTextColor(Color.GRAY)
                        .setListener(new Listener()
                        {
                            @Override
                            public void onDateSelected(PersianCalendar persianCalendar)
                            {
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                BirthDay = dateFormat.format(persianCalendar.getTime()) + "";
                                try
                                {
                                    if (!BirthDay.isEmpty())
                                    {
                                        Date UserBirth = dateFormat.parse(BirthDay);
                                        txt_BirthDate.setText(new MiladiToShamsi().getPersianDate(UserBirth) + "");
                                    }

                                } catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onDismissed()
                            {

                            }
                        });

                picker.show();
            }
        });
        Button profile_btn_SaveChanges = findViewById(R.id.Profile_btn_SaveChanges);
        profile_btn_SaveChanges.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (HelperModule.IsFillValidation(Profile_Edt_Name, Profile_Edt_Family, Profile_Edt_FirstPhoneNumber, Profile_edt_Address, Profile_edt_Mobile))
                {
                    if (BirthDay.isEmpty())
                    {
                        Toast.makeText(EditProfileActivity.this, "لطفا تاریخ تولدتان را انتخاب کنید.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ProfileSaveChange();
                }
                else
                {
                    Toast.makeText(EditProfileActivity.this, "لطفا موارد خواسته شده را پر کنید.", Toast.LENGTH_SHORT).show();
                }
                //------------------------------------------------------
            }


        });
    }

    private void OpenGallery()
    {
        Intent ImagePickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ImagePickerIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(ImagePickerIntent, "انتخاب عکس"), 1);
    }

    private void ProfileSaveChange()
    {
        UserProfileModel UserProfile = new UserProfileModel();
        UserProfile.UserID = SharedPreferencesHelper.ReadInt("UserID");
        UserProfile.Fname = Profile_Edt_Name.getText().toString();
        UserProfile.Lname = Profile_Edt_Family.getText().toString();
        UserProfile.FirstPhoneNumber = HelperModule.arabicToDecimal(Profile_Edt_FirstPhoneNumber.getText().toString());
        UserProfile.SecondPhoneNumber = HelperModule.arabicToDecimal(Profile_Edt_SecondPhoneNumber.getText().toString());
        UserProfile.MobileNumber = HelperModule.arabicToDecimal(Profile_edt_Mobile.getText().toString());
        UserProfile.Email = Profile_edt_Email.getText().toString();
        UserProfile.BirthDate = HelperModule.arabicToDecimal(BirthDay);
        UserProfile.StateCode = StateCode;
        UserProfile.CityCode = CityCode;
        UserProfile.Description = Profile_edt_about_me.getText().toString();
        UserProfile.UserAddress = Profile_edt_Address.getText().toString();
        if (profileModel.ImageName.isEmpty())
        {
            if (Picture == null)
            {
                UserProfile.ImageName = "nophoto.png";
            }
            else
            {
                UserProfile.ImageName = Base64.encodeToString(Picture, Base64.DEFAULT);
            }
        }
        else
        {
            if (Picture == null)
            {
                UserProfile.ImageName = "old-photo";
            }
            else
            {
                UserProfile.ImageName = Base64.encodeToString(Picture, Base64.DEFAULT);
            }
        }
        LoadingDialog.show();
        Call<Boolean> AddUserInfo = requestApi.EditProfile(UserProfile);
        AddUserInfo.enqueue(new ApiCallBack<Boolean>(EditProfileActivity.this, AddUserInfo)
        {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response)
            {
                if (response.body())
                {
                    Toast.makeText(EditProfileActivity.this, "ویرایش پروفایل باموفقیت انجام شد.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    Toast.makeText(EditProfileActivity.this, "خطا در ویرایش پروفایل!!!", Toast.LENGTH_SHORT).show();
                }
                LoadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t)
            {
                LoadingDialog.dismiss();
                super.onFailure(call, t);
            }
        });
    }

    private void InitViews()
    {
        Profile_edt_about_me = findViewById(R.id.Profile_edt_about_me);
        Profile_Edt_Name = findViewById(R.id.Profile_Edt_Name);
        Profile_Edt_Family = findViewById(R.id.Profile_Edt_Family);
        btn_ChooseBirthDate = findViewById(R.id.btn_choose_birth_date);
        txt_BirthDate = findViewById(R.id.txt_birth_date);
        Profile_Edt_FirstPhoneNumber = findViewById(R.id.Profile_Edt_FirstPhoneNumber);
        Profile_Edt_SecondPhoneNumber = findViewById(R.id.Profile_Edt_SecondPhoneNumber);
        Profile_edt_Mobile = findViewById(R.id.Profile_edt_Mobile);
        Profile_edt_Email = findViewById(R.id.Profile_edt_Email);
        Profile_edt_Address = findViewById(R.id.Profile_edt_Address);
        Profile_sp_states = findViewById(R.id.Profile_sp_states);
        Profile_Sp_City = findViewById(R.id.Profile_sp_city);
        Profile_img_profile = findViewById(R.id.Profile_img_profile);
        btn_chooseImage = findViewById(R.id.btn_chooseImage);
        InitStateCity();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                if (data != null)
                {
                    try
                    {
                        Picture = HelperModule.ConvertImageToByteArray(data, EditProfileActivity.this);
                        Profile_img_profile.setImageURI(data.getData());
                    } catch (Exception e)
                    {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
    }

    private void InitStateCity()
    {
        CityList = new ArrayList<>();
        StateCityList = new ArrayList<>();
        StateCityList = HelperModule.GetStateCityList(this);
        //---------------------------------------------------------
        final String[] StateNames = new String[StateCityList.size()];
        for (int i = 0; i < StateCityList.size(); ++i)
        {
            StateNames[i] = StateCityList.get(i).StateName;
        }
        ArrayAdapter<String> StateAdapter = new ArrayAdapter<>(EditProfileActivity.this, R.layout.my_spinner_item, StateNames);
        CityAdapter = new ArrayAdapter<>(EditProfileActivity.this, R.layout.my_spinner_item);
        Profile_sp_states.setAdapter(StateAdapter);
        Profile_Sp_City.setAdapter(CityAdapter);
        //-----------------------------
        Profile_sp_states.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                StateCode = StateCityList.get(position).StateCode;
                CityList.clear();
                CityAdapter.clear();
                for (int p = 0; p < StateCityList.get(position).CityCollection.size(); ++p)
                {
                    CityList.add(StateCityList.get(position).CityCollection.get(p).CityName);
                }
                CityAdapter.addAll(CityList);
                CityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
        Profile_Sp_City.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                CityCode = StateCityList.get(Profile_sp_states.getSelectedItemPosition()).CityCollection.get(position).CityCode;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

    }

    private void LoadUserInfo()
    {
        profileModel = new Gson().fromJson(getIntent().getStringExtra("UserInfo"), new TypeToken<ProfileModel>()
        {
        }.getType());
        if (profileModel == null)
        {
            profileModel = new ProfileModel();
        }
        Profile_Edt_Name.setText(profileModel.Fname);
        Profile_Edt_Family.setText(profileModel.Lname);
        Profile_Edt_FirstPhoneNumber.setText(profileModel.FirstPhoneNumber);
        Profile_Edt_SecondPhoneNumber.setText(profileModel.SecondPhoneNumber);
        Profile_edt_Mobile.setText(profileModel.MobileNumber);
        Profile_edt_Email.setText(profileModel.Email);
        Profile_edt_about_me.setText(profileModel.Description);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try
        {
            if (!profileModel.BirthDate.isEmpty())
            {
                BirthDay = profileModel.BirthDate;
                Date UserBirth = dateFormat.parse(profileModel.BirthDate);
                txt_BirthDate.setText(new MiladiToShamsi().getPersianDate(UserBirth) + "");
            }
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        if (profileModel.ImageName.isEmpty())
        {
            Picasso.get().load(R.drawable.user_profile).into(Profile_img_profile);
        }
        else if (profileModel.ImageName.equals("nophoto.png"))
        {
            Picture = null;
        }
        else
        {
            Picasso.get().load(getResources().getString(R.string.ProfileImageFolder) + profileModel.ImageName).into(Profile_img_profile);
        }
        Profile_edt_Address.setText(profileModel.UserAddress);
        //----------------------------------------------------------------
        for (int i = 0; i < StateCityList.size(); ++i)
        {
            if (StateCityList.get(i).StateCode == profileModel.StateCode)
            {
                Profile_sp_states.setSelection(i);
                StateCode = profileModel.StateCode;
                //---------------------------------
                for (int j = 0; j < StateCityList.get(i).CityCollection.size(); ++j)
                {
                    if (StateCityList.get(i).CityCollection.get(j).CityCode == profileModel.CityCode)
                    {
                        CityCodePosition = j;
                        break;
                    }

                }
                //--------------------------------

            }

        }
        Profile_Sp_City.postDelayed(new Runnable()
        {
            public void run()
            {
                CityAdapter.notifyDataSetChanged();
                Profile_Sp_City.setSelection(CityCodePosition);
            }
        }, 1000);
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void requestCameraPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else
        {
            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode != 1)
        {
            return;
        }
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            OpenGallery();
        }

    }
}
