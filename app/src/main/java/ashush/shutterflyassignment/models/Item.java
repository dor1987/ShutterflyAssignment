package ashush.shutterflyassignment.models;

public class Item {
        private boolean isLiked;
        private int id;
        private int itemType;
        private String previewURL;


        public Item() {
        }

        public Item(int id, String previewURL) {
            this.id = id;
            this.previewURL = previewURL;
            isLiked = false;
        }


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getItemType() {
            return itemType;
        }

        public void setItemType(int itemType) {
            this.itemType = itemType;
        }

        public boolean isLiked() {
            return isLiked;
        }

        public void setLiked(boolean liked) {
            isLiked = liked;
        }

        public String getPreviewURL() {
            return previewURL;
        }

        public void setPreviewURL(String previewURL) {
            this.previewURL = previewURL;
        }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", itemType=" + itemType +
                ", previewURL='" + previewURL + '\'' +
                '}';
    }
}
