package ashush.shutterflyassignment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

import ashush.shutterflyassignment.models.Item;
import ashush.shutterflyassignment.repositories.ItemRepository;

import static ashush.shutterflyassignment.utils.Constants.VIEWS_IN_PAGE;

public class MainActivityViewModel extends ViewModel {
    private static final String TAG = "MainActivityViewModel";
    private ItemRepository mItemRepository;
    private boolean mIsPerformingQuery;
    private String mQuery;
    private String mType;
    private int mPageNumber;
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
    private MediatorLiveData<List<Item>> mItems = new MediatorLiveData<>();

    public MainActivityViewModel() {
        mItemRepository = ItemRepository.getInstance();
        mIsPerformingQuery = false;
        initMediators();
    }
    public LiveData<List<Item>> getItems(){
        return mItems;
    }

    private void initMediators(){
        LiveData<List<Item>> itemListApiSource = mItemRepository.getItems();
        mItems.addSource(itemListApiSource, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> items) {

                if(items != null){
                    setIsPerformingQuery(false);
                    mItems.setValue(items);
                    doneQuery(items);
                }
                else{
                    doneQuery(null);
                }
            }
        });
    }
    public void searchItemsApi(String query,String type, int pageNumber){
        mQuery = query;
        mType = type;
        mPageNumber = pageNumber;

        mIsPerformingQuery = true;
        mIsQueryExhausted.setValue(false);
        mItemRepository.searchItemsApi(query, type,pageNumber);
    }

    public void searchNextPage(){
        if(!mIsPerformingQuery
                && !isQueryExhausted().getValue()){
            mIsPerformingQuery = true;
            mItemRepository.searchItemsApi(mQuery,mType,mPageNumber+1);
            mPageNumber++;
            Log.d(TAG,"Page Numb"+mPageNumber);
        }
    }


    public void setIsPerformingQuery(Boolean isPerformingQuery){
        mIsPerformingQuery = isPerformingQuery;
    }

    public boolean isPerformingQuery(){
        return mIsPerformingQuery;
    }

    public LiveData<Boolean> isQueryExhausted() {
        return mIsQueryExhausted;
    }

    private void doneQuery(List<Item> list) {
        if (list != null) {
            if (list.size() % VIEWS_IN_PAGE != 0) {
                mIsQueryExhausted.setValue(true);
            }
        } else {
            mIsQueryExhausted.setValue(true);
        }
    }

    public boolean onBackPressed(){
        if(mIsPerformingQuery){
            // cancel the query
            mItemRepository.cancelRequest();
            mIsPerformingQuery = false;
        }
        return true;
    }

}
