package ashush.shutterflyassignment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;

import ashush.shutterflyassignment.adapters.MainActivityListRecyclerViewAdapter;
import ashush.shutterflyassignment.adapters.OnItemClickListener;
import ashush.shutterflyassignment.models.Item;
import ashush.shutterflyassignment.repositories.ItemRepository;

import static ashush.shutterflyassignment.utils.Constants.DEFUALT_PAGE_NUMBER;
import static ashush.shutterflyassignment.utils.Constants.IMAGE_TYPE;
import static ashush.shutterflyassignment.utils.Constants.QUERY;
import static ashush.shutterflyassignment.utils.Constants.SHARED_PREFS;
import static ashush.shutterflyassignment.utils.Constants.SPAN_COUNT;
import static ashush.shutterflyassignment.utils.Constants.TRUE;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {
    private static final String TAG = "MainActivity";

    private MainActivityViewModel mMainActivityViewModel;
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private MainActivityListRecyclerViewAdapter mAdapter;
    private HashMap<Integer,Boolean> isLikedMap;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        mSearchView = findViewById(R.id.search_view);
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        mMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        initRecyclerView();
        initSearchView();
        subscribeObservers();
        mMainActivityViewModel.searchItemsApi(QUERY,IMAGE_TYPE,DEFUALT_PAGE_NUMBER);

    }

    private void subscribeObservers(){
        mMainActivityViewModel.getItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> items) {
                if(items != null){
                    mAdapter.setItems(items);
                    isLikedMap = readFromSP();
                    updateLikes(items);
                }
            }
        });

        mMainActivityViewModel.isQueryExhausted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Log.d(TAG, "onChanged: the query is exhausted..." + aBoolean);
                if(aBoolean) {
                    mAdapter.setQueryExhausted();
                }
            }
        });


    }

    private void initRecyclerView() {
        mAdapter = new MainActivityListRecyclerViewAdapter(this);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this,SPAN_COUNT);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if(!mRecyclerView.canScrollVertically(TRUE)){
                    // search the next page
                    mMainActivityViewModel.searchNextPage();
                }
            }
        });
    }

    private void initSearchView(){
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                mAdapter.displayLoading();
                mMainActivityViewModel.searchItemsApi(s,IMAGE_TYPE,1);
                mSearchView.clearFocus();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mMainActivityViewModel.onBackPressed()) {
            super.onBackPressed();
        }
    }
    @Override
    public void onItemclick(Item item, int position, boolean isLiked) {
        item.setLiked(!isLiked);

        if (!isLiked) {
            isLikedMap.put(item.getId(), isLiked);
        } else {
            isLikedMap.remove(item.getId());
        }

        insertToSP(isLikedMap);
        mAdapter.notifyItemChanged(position);

    }


    private void insertToSP(HashMap<Integer, Boolean> jsonMap) {
        String jsonString = new Gson().toJson(jsonMap);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("map", jsonString);
        editor.apply();
    }

    private HashMap<Integer, Boolean> readFromSP(){
        String defValue = new Gson().toJson(new HashMap<Integer, Boolean>());
        String json = sharedPreferences.getString("map",defValue);
        TypeToken<HashMap<Integer, Boolean>> token = new TypeToken<HashMap<Integer, Boolean>>() {};
        HashMap<Integer, Boolean> retrievedMap = new Gson().fromJson(json,token.getType());
        return retrievedMap;
    }

    private void updateLikes(List<Item> list){
        for (Item item : list){
            if(isLikedMap.get(item.getId())!= null)
                   // && isLikedMap.get(item.getId()))
            {
                item.setLiked(true);
            }
        }

    }
}
