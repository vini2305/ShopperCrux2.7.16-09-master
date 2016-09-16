package com.wvs.shoppercrux.Wishlist;

/**
 * Created by root on 30/8/16.
 */
public class WishListPojo {

    public String prodName;
    public String prodImage;
    public String prodId;

    public String getProdImage() {
        return prodImage;
    }

    public void setProdImage(String prodImage) {
        prodImage = prodImage.replaceAll(" ", "%20");
        this.prodImage = "http://shoppercrux.com/image/"+prodImage;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

}
