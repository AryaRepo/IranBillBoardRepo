package city.bilboard.aryasoft.com.bilboardcity.ApiModule;

import java.util.ArrayList;

import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.BoardDetailModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.BoardDetailQuickInfoModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.BoardModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.BoardsCategory;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.ChangeReservationBoardApiModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.CollectionModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.ProfileModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.RecoverPasswordApiModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.ResOfRentApiModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.ReserveBoardsApiModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.RoleModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.SimilarProductApiModel;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.SliderModel;
import city.bilboard.aryasoft.com.bilboardcity.Models.AdminModel;
import city.bilboard.aryasoft.com.bilboardcity.Models.OwnerModel;
import city.bilboard.aryasoft.com.bilboardcity.Models.SignUpModel;
import city.bilboard.aryasoft.com.bilboardcity.Models.UserModel;
import city.bilboard.aryasoft.com.bilboardcity.Models.UserProfileModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RequestApi
{

    @GET("ProductApi/Slider")
    Call<ArrayList<SliderModel>> LoadSlider();

    @GET("ProductApi/LoadFavouriteBoards")
   Call<ArrayList<BoardModel>> GetFavouriteBoards(@Query("CityCode") Integer CityCode , @Query("CollectionSkip") Integer CollectionSkip ,  @Query("CollectionTake") Integer CollectionTake );

    @GET("ProductApi/LoadBoardsByCollection")
    Call<ArrayList<CollectionModel>> GetBoardsByCollection(@Query("CityCode") Integer CityCode, @Query("CollectionSkip") Integer CollectionSkip, @Query("CollectionTake") Integer CollectionTake, @Query("ItemSkip") Integer ItemSkip, @Query("ItemTake") Integer ItemTake);

    @GET("ProductApi/LoadBoardsByCollectionId")
    Call<ArrayList<CollectionModel>> LoadBoardsByCollectionId(@Query("CityCode") Integer CityCode, @Query("CollectionSkip") Integer CollectionSkip, @Query("CollectionTake") Integer CollectionTake, @Query("ItemSkip") Integer ItemSkip, @Query("ItemTake") Integer ItemTake,@Query("CollectionTypeID") Integer CollectionTypeID );
//   @GET("ProductApi/GetProducts")
//    Call<ArrayList<BoardModel>> GetBoardsBySection(@Query("id") Integer id, @Query("PageNumber") Integer PageNumber, @Query("citycode") Integer CityCode);

    /*  @POST("AccountApi/Register")
      Call<Integer> SignUpUser(@Query("MobileNumber") String MobileNumber, @Query("Password") String Password, @Query("RecommendMobileNumber") String RecommendMobileNumber, @Query("Email") String Email);
      */
    @POST("AccountApi/Register")
    Call<Integer> SignUpUser(@Body SignUpModel User);

    @POST("AccountApi/newpassword")
    Call<Boolean> ChangePassword(@Query("MobileNumber") String MobileNumber, @Query("OldPassword") String OldPassword, @Query("NewPassword") String NewPassword);


    @POST("AccountApi/ActiveUser")
    Call<Boolean> ActiveUser(@Query("ActiveCode") Integer ActiveCode, @Query("MobileNumber") String MobileNumber);

    @GET("AccountAPI/Login")
    Call<Integer> LoginUser(@Query("MobileNumber") String MobileNumber, @Query("Password") String Password);

    @GET("ProductApi/CategoryBoards")
    Call<ArrayList<BoardsCategory>> GetBoardCategories();

    //---------------------------------------
    @GET("ProductApi/ReportRentBoardes")
    Call<ArrayList<UserModel>> GetReportRentBoards(@Query("UserId") Integer UserId, @Query("PageNumber") Integer PageNumber);

    @GET("ProductApi/ReportRentBoardesOwner")
    Call<ArrayList<OwnerModel>> GetReportRentBoardesOwner(@Query("UserId") Integer UserId, @Query("PageNumber") Integer PageNumber);

    @GET("ProductApi/BoardListOfOwner")
    Call<ArrayList<BoardModel>> GetBoardListOfOwner(@Query("UserId") Integer UserId, @Query("PageNumber") Integer PageNumber);

    @GET("ProductApi/ReserveBoardsList")
    Call<ArrayList<AdminModel>> GetReserveBoardsList(@Query("PageNumber") Integer PageNumber);

    @GET("ProductApi/GetSimilarBoards")
    Call<ArrayList<SimilarProductApiModel>> GetSimilarBoards(@Query("boardsid") Integer BoardId);

    //-------------------------------------------------
    @GET("ProductApi/Search")
    Call<ArrayList<BoardModel>> Search(@Query("str") String SearchString);


    @GET("productApi/GetBoardDetailById")
    Call<ArrayList<BoardDetailModel>> GetBoardDetail(@Query("id") Integer BoardId);

    @GET("AccountApi/GetUserRole")
    Call<RoleModel> GetUserRole(@Query("id") Integer UserID);

    @GET("ProductApi/GetProductsByCatId")
    Call<ArrayList<BoardModel>> GetBoardsByCategoryId(@Query("id") Integer CategoryId, @Query("PageNumber") Integer PageNumber);

    @POST("AccountApi/RememberPassword")
    Call<Integer> RecoveryPassword(@Body RecoverPasswordApiModel recoverPasswordApi);


    @POST("AccountApi/AddUserInfo")
    Call<Boolean> EditProfile(@Body UserProfileModel UserProfile);

    @GET("AccountAPI/RenewActiveCode")
    Call<Integer> RenewActiveCode(@Query("MobileNumber") String MobileNumber);


    @POST("ProductApi/Reservation")
    Call<ArrayList<ResOfRentApiModel>> ReserveBoards(@Body ReserveBoardsApiModel ReserveObject);


    @POST("ProductApi/ChangeReservationBoard")
    Call<Boolean> ChangeReservationBoard(@Body ChangeReservationBoardApiModel changeReservationBoardApiModel);
    //============Profile Stuff

    @GET("AccountApi/GetUserInfo")
    Call<ProfileModel> GetUserInfoProfile(@Query("id") Integer UserId);


    @GET("productApi/GetBoardDetailById")
    Call<ArrayList<BoardDetailQuickInfoModel>> GetBoardDetailQuickInfo(@Query("id") Integer BoardId);
}
