package ashush.shutterflyassignment.requests;

import ashush.shutterflyassignment.utils.Constants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static ItemApi itemApi = retrofit.create(ItemApi.class);

    public static ItemApi getItemApi(){
        return itemApi;
    }
}
