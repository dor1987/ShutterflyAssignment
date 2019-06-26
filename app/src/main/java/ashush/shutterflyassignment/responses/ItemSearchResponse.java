package ashush.shutterflyassignment.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ashush.shutterflyassignment.models.Item;

public class ItemSearchResponse {

    @SerializedName("count")
    @Expose()
    private int count;

    @SerializedName("hits")
    @Expose()
    private List<Item> items;

    public int getCount() {
        return count;
    }

    public List<Item> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "ItemSearchResponse{" +
                "items=" + items +
                '}';
    }
}
