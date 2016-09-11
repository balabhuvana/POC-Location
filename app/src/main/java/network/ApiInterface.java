package network;

import model.LocationModel;
import model.StackOverflowQuestions;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by bala on 11/9/16.
 */
public interface ApiInterface {
    @GET("students")
    Call<LocationModel> getLocation();

    @POST("httppost.php")
    void postingData(SamplePostRequest mSamplePostRequest);

    @GET("/2.2/questions?order=desc&sort=creation&site=stackoverflow")
    Call<StackOverflowQuestions> loadQuestions(@Query("tagged") String tags);

}
