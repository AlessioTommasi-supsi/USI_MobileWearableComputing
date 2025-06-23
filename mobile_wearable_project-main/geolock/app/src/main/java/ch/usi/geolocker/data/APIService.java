package ch.usi.geolocker.data;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIService {
    @GET("get-spots.php")
    Call<List<Spot>> getSpots();

    @POST("spot.php")
    Call<Spot> addSpot(@Body Spot spot);
}
