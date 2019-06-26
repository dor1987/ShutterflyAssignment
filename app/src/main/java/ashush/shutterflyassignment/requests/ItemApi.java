package ashush.shutterflyassignment.requests;

import ashush.shutterflyassignment.responses.ItemSearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ItemApi {

    // GET ITEMS REQUEST
    @GET("api")
    Call<ItemSearchResponse> searchItems(
            @Query("key") String key,
            @Query("q") String query,
            @Query("image_type") String type,
            @Query("page") String page,
            @Query("per_page") String per_page
    );

}
