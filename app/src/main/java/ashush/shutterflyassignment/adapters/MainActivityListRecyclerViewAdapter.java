package ashush.shutterflyassignment.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import ashush.shutterflyassignment.R;
import ashush.shutterflyassignment.models.Item;

import static ashush.shutterflyassignment.utils.Constants.EXHAUSTED_TYPE;
import static ashush.shutterflyassignment.utils.Constants.ITEM_TYPE;
import static ashush.shutterflyassignment.utils.Constants.LOADING_TYPE;


public class MainActivityListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private List<Item> mItemList;
    private OnItemClickListener mOnItemClickListener;

    public MainActivityListRecyclerViewAdapter(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;

        switch (i){

            case ITEM_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item, viewGroup, false);
                return new ItemViewHolder(view);
            }

            case LOADING_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_list_item, viewGroup, false);
                return new LoadingViewHolder(view);
            }

            case EXHAUSTED_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_exhausted, viewGroup, false);
                return new SearchExhaustedViewHolder(view);
            }

            default:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item, viewGroup, false);
                return new ItemViewHolder(view);
            }
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
       int itemViewType = getItemViewType(i);
       if(itemViewType == ITEM_TYPE) {

           ((ItemViewHolder)viewHolder).mLikeImage.setVisibility(mItemList.get(i).isLiked() ? View.VISIBLE : View.INVISIBLE);
           ((ItemViewHolder)viewHolder).mDontLikeImage.setVisibility(mItemList.get(i).isLiked() ? View.INVISIBLE : View.VISIBLE);

           //Set images
           RequestOptions defaultOptions = new RequestOptions()
                   .error(R.drawable.ic_launcher_background);

            Glide.with(viewHolder.itemView.getContext())
                   .setDefaultRequestOptions(defaultOptions)
                   .load(mItemList.get(i).getPreviewURL())
                   .into(((ItemViewHolder)viewHolder).mImage);



           //Set onClickListener
            ((ItemViewHolder) viewHolder).parentLayout.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemclick(mItemList.get(i),i,mItemList.get(i).isLiked());
                }
            });
       }
    }
/*
    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if(holder.getItemViewType() == ITEM_TYPE)
            Glide.with(holder.itemView.getContext()).clear(((ItemViewHolder)holder).mImage);
    }
*/
    @Override
    public int getItemViewType(int position) {
        if(mItemList.get(position).getItemType() == LOADING_TYPE){
            return LOADING_TYPE;
        }
        else if(mItemList.get(position).getItemType() == EXHAUSTED_TYPE){
            return EXHAUSTED_TYPE;
        }
        else if(position == mItemList.size() - 1
                && position != 0
                && !(mItemList.get(position).getItemType() == EXHAUSTED_TYPE)){
            return LOADING_TYPE;
        }
        else{
            return ITEM_TYPE;
        }
    }

    public void setQueryExhausted(){
        hideLoading();
        Item exhaustedItem = new Item();
        exhaustedItem.setItemType(EXHAUSTED_TYPE);
        mItemList.add(exhaustedItem);
        notifyDataSetChanged();
    }

    private void hideLoading(){
        if(isLoading()){
            for(Item item: mItemList){
                if(item.getItemType()== LOADING_TYPE){
                    mItemList.remove(item);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void displayLoading(){
        if(!isLoading()){
            Item item = new Item();
            item.setItemType(LOADING_TYPE);
            List<Item> loadingList = new ArrayList<>();
            loadingList.add(item);
            mItemList = loadingList;
            notifyDataSetChanged();
        }
    }

    private boolean isLoading(){
        if(mItemList != null){
            if(mItemList.size() > 0){
                if(mItemList.get(mItemList.size() - 1).getItemType() == LOADING_TYPE){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        if(mItemList != null){
            return mItemList.size();
        }
        return 0;
    }

    public void setItems(List<Item> items){
        mItemList = items;
        notifyDataSetChanged();
    }


    protected class ItemViewHolder extends RecyclerView.ViewHolder {
        protected ImageView mImage;
        private ImageView mLikeImage;
        private ImageView mDontLikeImage;

        protected RelativeLayout parentLayout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
              mImage = itemView.findViewById(R.id.image);
              mLikeImage = itemView.findViewById(R.id.heart_image);
              mDontLikeImage = itemView.findViewById(R.id.heart_image_empty);
              parentLayout = itemView.findViewById(R.id.item_parent_layout);
        }
    }

    protected class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    protected class SearchExhaustedViewHolder extends RecyclerView.ViewHolder {

        public SearchExhaustedViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
