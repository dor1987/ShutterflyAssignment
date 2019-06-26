package ashush.shutterflyassignment.adapters;


import android.content.Context;

import ashush.shutterflyassignment.models.Item;

public interface OnItemClickListener {
    void onItemclick(Item item,int position, boolean isLiked);
}
