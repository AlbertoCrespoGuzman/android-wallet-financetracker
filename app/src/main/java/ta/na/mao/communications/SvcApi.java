package ta.na.mao.communications;

/**
 * Created by alberto on 18/06/15.
 */


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ta.na.mao.communications.http.auth.AndroiduserLogin;
import ta.na.mao.communications.http.auth.AndroiduserLoginResponse;
import ta.na.mao.communications.http.auth.ResponseMessage;
import ta.na.mao.communications.http.auth.get.getUser;
import ta.na.mao.database.models.Goal;
import ta.na.mao.database.models.Installment;
import ta.na.mao.database.models.Transaction;
import ta.na.mao.database.models.User;
import ta.na.mao.database.models.UserDetails;
import ta.na.mao.database.models.productprice.FixedVariableCost;
import ta.na.mao.database.models.productprice.ProductPrice;
import ta.na.mao.database.models.productprice.Raw;
import ta.na.mao.database.models.serviceprice.FixedCost;
import ta.na.mao.database.models.serviceprice.LabourCost;
import ta.na.mao.database.models.serviceprice.LabourTax;
import ta.na.mao.database.models.serviceprice.ServicePrice;
import ta.na.mao.database.models.serviceprice.VariableCost;


import java.util.List;


public interface SvcApi {
    String REGISTER_ANDROID_USER = "users/sign-up";
    String LOGIN_ANDROID_USER = "login";
    String SIGN_IN_FACEBOOK = "users/signin/facebook";
    String LOGOUT_ANDROID_USER = "api/logout";
    String FORGOT_PASSWORD_USER = "users/resetPassword";
    String USERDETAILS = "api/users/details";
    String TRANSACTION = "api/transaction";
    String TRANSACTIONS = "api/transactions";
    String INSTALLMENT = "api/installment";
    String INSTALLMENTS = "api/installments";
    String GOAL = "api/goal";
    String GOALS = "api/goals";

    String SERVICEPRICES = "api/serviceprices";
    String SERVICEPRICE = "api/serviceprice";
    String FIXEDCOST_SERVICEPRICES = "api/serviceprice/fixedcosts";
    String FIXEDCOST_SERVICEPRICE = "api/serviceprice/fixedcost";
    String LABOURCOST_SERVICEPRICES = "api/serviceprice/labourcosts";
    String LABOURCOST_SERVICEPRICE = "api/serviceprice/labourcost";
    String LABOURTAX_SERVICEPRICES = "api/serviceprice/labourtax";
    String LABOURTAX_SERVICEPRICE = "api/serviceprice/labourtax";
    String VARIABLECOST_SERVICEPRICES = "api/variablecost_serviceprices";
    String VARIABLECOST_SERVICEPRICE = "api/variablecost_serviceprice";

    String PRODUCTPRICES = "api/productprices";
    String PRODUCTPRICE = "api/productprice";
    String RAW_PRODUCTPRICES = "api/productprice/raw";
    String RAW_PRODUCTPRICE = "api/productprice/raw";
    String FIXEDVARIABLECOST_PRODUCTPRICES = "api/productprice/fixedvariablecost";
    String FIXEDVARIABLECOST_PRODUCTPRICE = "api/productprice/fixedvariablecost";

    @POST(REGISTER_ANDROID_USER)
    public Call<List<ResponseMessage>> registerAndroiduser(@Body User au);
    @POST(LOGIN_ANDROID_USER)
    public Call<Void> loginAndroiduser(@Body AndroiduserLogin au);
    @POST(SIGN_IN_FACEBOOK)
    public Call<Void> loginUsingFacebook(@Body AndroiduserLogin au);
    @POST(FORGOT_PASSWORD_USER)
    public Call<Void> postForgotPassword(@Body User user);
    @FormUrlEncoded
    @POST(LOGOUT_ANDROID_USER)
    public Call<AndroiduserLoginResponse> postLogout(@Field("device_id") String device_id);
    @GET(USERDETAILS)
    public Call<UserDetails> getUserDetails();
    @POST(USERDETAILS)
    Call<UserDetails> postUserDetails(@Body UserDetails userDetails);
    @GET(TRANSACTIONS)
    Call<List<Transaction>> getTransactions();
    @POST(TRANSACTION)
    Call<Transaction> postTransaction(@Body Transaction transaction);
    @GET(INSTALLMENTS)
    Call<List<Installment>> getInstallments();
    @POST(INSTALLMENT)
    Call<Installment> postInstallment(@Body Installment installment);
    @GET(GOALS)
    Call<List<Goal>> getGoals();
    @POST(GOAL)
    Call<Goal> postGoal(@Body Goal goal);
    @DELETE(TRANSACTION)
    Call<Boolean> deleteTransaction(@Query("transaction_id") long transaction_id);
    @DELETE(INSTALLMENT)
    Call<Boolean> deleteInstallment(@Query("installment_id") long installment_id);
    @DELETE(GOAL)
    Call<Boolean> deleteGoal(@Query("goal_id") long goal_id);
    /*
                SERVICE PRICE
     */
    @GET(SERVICEPRICES)
    Call<List<ServicePrice>> getServiceprices();
    @POST(SERVICEPRICE)
    Call<ServicePrice> postServicePrice(@Body ServicePrice servicePrice);
    @DELETE(SERVICEPRICE)
    Call<Boolean> deleteServicePrice(@Query("serviceprice_id") long serviceprice_id);

    @GET(FIXEDCOST_SERVICEPRICES)
    Call<List<FixedCost>> getFixedCostServiceprice();
    @POST(FIXEDCOST_SERVICEPRICE)
    Call<FixedCost> postFixedCostServicePrice(@Body FixedCost fixedCost);
    @DELETE(FIXEDCOST_SERVICEPRICE)
    Call<Boolean> deleteFixedCostServicePrice(@Query("fixedcost_serviceprice_id") long fixedcost_serviceprice_id);

    @GET(LABOURCOST_SERVICEPRICES)
    Call<List<LabourCost>> getLabourCostServiceprice();
    @POST(LABOURCOST_SERVICEPRICE)
    Call<LabourCost> postLabourCostServicePrice(@Body LabourCost labourCost);
    @DELETE(LABOURCOST_SERVICEPRICE)
    Call<Boolean> deleteLabourCostServicePrice(@Query("labourcost_serviceprice_id") long labourcost_serviceprice_id);

    @GET(LABOURTAX_SERVICEPRICES)
    Call<List<LabourTax>> getLabourTaxServiceprice();
    @POST(LABOURTAX_SERVICEPRICE)
    Call<LabourTax> postLabourTaxServicePrice(@Body LabourTax labourTax);
    @DELETE(LABOURTAX_SERVICEPRICE)
    Call<Boolean> deleteLabourTaxServicePrice(@Query("labourtax_serviceprice_id") long labourtax_serviceprice_id);

    @GET(VARIABLECOST_SERVICEPRICES)
    Call<List<VariableCost>> getVariableCostServiceprice();
    @POST(VARIABLECOST_SERVICEPRICE)
    Call<VariableCost> postVariableCostServicePrice(@Body VariableCost variableCost);
    @DELETE(VARIABLECOST_SERVICEPRICE)
    Call<Boolean> deleteVariableCostServicePrice(@Query("variablecost_serviceprice_id") long variablecost_serviceprice_id);

    /*
                PRODUCT PRICE
     */
    @GET(PRODUCTPRICES)
    Call<List<ProductPrice>> getProductPrices();
    @POST(PRODUCTPRICE)
    Call<ProductPrice> postProductPrice(@Body ProductPrice productPrice);
    @DELETE(PRODUCTPRICE)
    Call<Boolean> deleteProductPrice(@Query("productprice_id") long productprice_id);

    @GET(RAW_PRODUCTPRICES)
    Call<List<Raw>> getRawProductPrices();
    @POST(RAW_PRODUCTPRICE)
    Call<Raw> postRawProductPrice(@Body Raw raw);
    @DELETE(RAW_PRODUCTPRICE)
    Call<Boolean> deleteRawProductPrice(@Query("raw_productprice_id") long raw_productprice_id);

    @GET(FIXEDVARIABLECOST_PRODUCTPRICES)
    Call<List<FixedVariableCost>> getFixedVariableCostProductPrices();
    @POST(FIXEDVARIABLECOST_PRODUCTPRICE)
    Call<FixedVariableCost> postFixedVariableCostProductPrice(@Body FixedVariableCost fixedVariableCost);
    @DELETE(FIXEDVARIABLECOST_PRODUCTPRICE)
    Call<Boolean> deleteFixedVariableCostProductPrice(@Query("fixedvariablecost_productprice_id") long fixedvariablecost_productprice_id);
}
