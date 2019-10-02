package com.haguepharmacy.utils;



import com.haguepharmacy.model.DrugResponse;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface Api {

    //String BASE_URL = "http://192.168.0.102/survey_v2/";
    String BASE_URL = "http://www.api.bijoylab.com/";




    @GET("get.php")
    public Call<DrugResponse> getDrugList();


//   // @FormUrlEncoded
//    @POST("api/get-questions")
//    Call<List<QuestionResponse>> get_questions(
//
//    );
//
//    @FormUrlEncoded
//    @POST("api/save-question-answer")
//    Call<String> save_question_answer(
//            @Field("data") String data
//    );


//
//
//    @FormUrlEncoded
//    @POST("api/requisitiondeliveryslipcreate")
//    Call<OnomodonResponse> requisitiondeliveryslipcreate(
//            @Field("auth_data") String auth_data,
//            @Field("data") String data
//    );
//
//
//
//    @FormUrlEncoded
//        @POST("api/requisitionapplicationstore")
//        Call<CommonResponse> requisitionapplicationstore(
//            @Field("auth_data") String auth_data,
//            @Field("data") String data
//    );
//
//
//    @FormUrlEncoded
//        @POST("api/requisitionapprovedquantity")
//        Call<CommonResponse> requisitionapprovedquantity(
//            @Field("auth_data") String auth_data,
//            @Field("data") String data
//    );
//

    @Multipart
    @POST("api/user_photogallery/add")
    public Call<ResponseBody>photogallery (
            // @Header("Authorization") String authHeader,
            @Part MultipartBody.Part file,
            // @Part("image") RequestBody image,
            @Part("title") RequestBody jsondata,
            @Part("user_id") RequestBody user_id
    );

    @Multipart
    @POST("put.php")
    Call<ResponseBody> submitPrescription(
            @Part("customer_mobile") RequestBody customer_mobile,
            @Part("customer_address") RequestBody customer_address,
            @Part MultipartBody.Part prescription

    );


    @FormUrlEncoded
    @POST("put.php")
    Call<ResponseBody> submitDrug(
//            @Part("customer_mobile") RequestBody customer_mobile,
//            @Part("customer_address") RequestBody customer_address,
//            @Part MultipartBody.Part prescription,
//            @Part("sub_total") RequestBody sub_total,
//            @Part("service_charge") RequestBody service_charge,
//            @Part("discount") RequestBody discount,
//            @Part("net_total") RequestBody net_total,
//            @Part("drugs") RequestBody drugs

            @Field("customer_mobile") String customer_mobile,
            @Field("customer_address") String customer_address,
            @Field("sub_total") String sub_total,
            @Field("service_charge") String service_charge,
            @Field("discount") String discount,
            @Field("net_total") String net_total,
            @Field("drugs") String drugs

    );


    @Multipart
    @POST("put.php")
    Call<ResponseBody> submitDrugMultipart(
            @Part("customer_mobile") RequestBody customer_mobile,
            @Part("customer_address") RequestBody customer_address,
            @Part MultipartBody.Part prescription,
            @Part("sub_total") RequestBody sub_total,
            @Part("service_charge") RequestBody service_charge,
            @Part("discount") RequestBody discount,
            @Part("net_total") RequestBody net_total,
            @Part("drugs") RequestBody drugs

    );


//
//
//    @FormUrlEncoded
//    @POST("api/public-employee")
//    Call<EmployResponse> public_employee(
//            @Field("region_id") String region_id,
//            @Field("unit_id") String unit_id,
//            @Field("zone_id") String zone_id,
//            @Field("designation_id") String designation_id,
//            @Field("like") String like
//    );
//
//
//    @FormUrlEncoded
//    @POST("api/assigncomplain")
//    Call<AssigncomplainResponse> assigncomplain(
//            @Field("auth_data") String auth_data,
//            @Field("application_no") String complain_application_id
//    );
//
//    @FormUrlEncoded
//    @POST("api/get-filter-all-data")
//    Call<String> planAlldata_filter(
//            @Field("division_id") String division_id,
//            @Field("district_id") String district_id
//    );
//
//     @FormUrlEncoded
//        @POST("api/user")
//        Call<String> planlogin(
//             @Field("email") String email,
//             @Field("password") String password
//     );
//    @FormUrlEncoded
//    @POST("api/get-all-data")
//    Call<String> planAlldata(
//            @Field("division_id") String division_id,
//            @Field("district_id") String district_id
//    );

//    @GET
//    public Call<SebaAbedonServiceInfoResponse> serviceapplyData(@Url String url);

//
//
//    @GET
//    public Call<RequsitionApproveSinglaDataResponse> getSingeDataReqIssue(@Url String url);
//
//    @GET
//    public Call<StockTransferApproveSinglaDataResponse> getSingeDataStockTransferApprove(@Url String url);
//
//
//     @GET
//    public Call<StockTransferIssueSinglaDataResponse> getSingleDataStockTransferIssue(@Url String url);
//
//
//
//
// @GET
//    public Call<StockTransferApproveSinglaDataResponse> getSingeDataStockTransferRecommandApprove(@Url String url);
//
//
//        @GET
//            public Call<LoanRecommendReqApproveSinglaDataResponse> getSingeDataLoanRecommandApprove(@Url String url);
//
//
//    @GET
//    public Call<LoanRecommendReqApproveSinglaDataResponse> getSingeDataLoanStatus(@Url String url);
//
//
//
//
//
//     @GET
//    public Call<CurrentStockResponse> currentStockReportFilter(@Url String url);
//
//    @GET
//        public Call<PurchaseReportResponse> purchaseReportFilter(@Url String url);
//
//        @GET
//        public Call<DirectionsResponse> getRout(@Url String url);

//
//
//
////    @Multipart
////    @POST("uploadPhoto")
////    Call<LoinResponse> postImage(@Part MultipartBody.Part images, @Part("user_id") String user_id);
////
////    @Multipart
////    @POST("uploadPhoto")
////    Call<LoinResponse> uploadImage(@Part("images") RequestBody file, @Part("user_id") RequestBody desc);
//
//
//    @GET("ApiController/next/CurrentStockReport/credential")
//    Call<ReportCommonDataResponse> getAllRecords();
//
//    @GET("ApiController/next/PurchaseReport/credential")
//    Call<ReportCommonDataResponse> getAllRecordsPurchaseReport();
//



//    @GET("api/get-occupation")
//    Call<List<ScRelation>> getOccupation(
//
//    );

//    @GET("ApiController/next/ProductRequisitionApproved/edit/")
//    Call<Info_FuelGenResponse> getFuel(
//            @Query("password") String password
//
//    );


//    @GET("get_technology_names")
//    Call<Info_GetTechnologyNames> getTechnologyName(
//            @Query("email") String email,
//            @Query("password") String password
//
//    );
//
//    @GET("technology_wise_generation_report")
//    Call<Info_TechWiseGenReportResponse> getTechnologyNameReport(
//            @Query("email") String email,
//            @Query("password") String password
//
//    );





}
