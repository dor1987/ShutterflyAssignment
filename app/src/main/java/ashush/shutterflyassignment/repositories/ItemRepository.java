package ashush.shutterflyassignment.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ashush.AppExecutors;
import ashush.shutterflyassignment.models.Item;
import ashush.shutterflyassignment.requests.ServiceGenerator;
import ashush.shutterflyassignment.responses.ItemSearchResponse;
import ashush.shutterflyassignment.utils.Constants;
import retrofit2.Call;
import retrofit2.Response;

import static ashush.shutterflyassignment.utils.Constants.NETWORK_TIMEOUT;
import static ashush.shutterflyassignment.utils.Constants.VIEWS_IN_PAGE;

public class ItemRepository {
    private static final String TAG = "ItemRepository";
    private static ItemRepository instance;
    private MutableLiveData<List<Item>> mItems;
    private RetrieveItemsRunnable mRetrieveItemsRunnable;


    public static ItemRepository getInstance() {
        if (instance == null) {
            instance = new ItemRepository();
        }
        return instance;
    }

    public ItemRepository() {
        mItems = new MutableLiveData<>();
    }

    public LiveData<List<Item>> getItems() {
        return mItems;
    }

    public void searchItemsApi(String query, String type, int pageNumber) {
        if (pageNumber == 0) {
            pageNumber = 1;
        }

        if (mRetrieveItemsRunnable != null) {
            mRetrieveItemsRunnable = null;
        }
        mRetrieveItemsRunnable = new RetrieveItemsRunnable(query, type, pageNumber);
        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveItemsRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }


    public void cancelRequest(){
        if(mRetrieveItemsRunnable != null){
            mRetrieveItemsRunnable.cancelRequest();
        }
    }

    private class RetrieveItemsRunnable implements Runnable {

        private String query;
        private String type;
        private int pageNumber;
        boolean cancelRequest;

        public RetrieveItemsRunnable(String query, String type, int pageNumber) {
            this.query = query;
            this.type = type;
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getItemsFromApi(query, type, pageNumber).execute();
                if (cancelRequest) {
                    return;
                }
                if (response.code() == 200) {
                    List<Item> list = new ArrayList<>(((ItemSearchResponse) response.body()).getItems());
                    if (pageNumber == 1) {
                        mItems.postValue(list);
                    } else {
                        List<Item> currentItems = mItems.getValue();
                        currentItems.addAll(list);
                        mItems.postValue(currentItems);
                    }
                } else {
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error);
                    mItems.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mItems.postValue(null);
            }

        }
        private Call<ItemSearchResponse> getItemsFromApi(String query, String type, int pageNumber){
            return ServiceGenerator.getItemApi().searchItems(
                    Constants.API_KEY,
                    query,
                    type,
                    String.valueOf(pageNumber),
                    String.valueOf(VIEWS_IN_PAGE)
            );
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the search request.");
            cancelRequest = true;
        }
    }

}
