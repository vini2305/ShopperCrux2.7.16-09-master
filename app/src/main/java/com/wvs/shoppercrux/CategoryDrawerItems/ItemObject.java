package com.wvs.shoppercrux.CategoryDrawerItems;

import com.google.gson.annotations.SerializedName;

public class ItemObject {

    @SerializedName("name")
    private String categoryName;
    @SerializedName("category_id")
    private String categoryId;


    public ItemObject(String categoryName, String categoryId, String songAuthor) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }
}
