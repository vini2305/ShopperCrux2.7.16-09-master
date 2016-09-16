package com.wvs.shoppercrux.Cart;

import android.util.Log;

/**
 * Created by root on 25/8/16.
 */
public class CartList {

    public String ImageServerUrl;
    public String productName;
    public String productQuantity;
    public String productId;
    public String productPrice;
    public String totalPrice;

    public String getImageServerUrl() {
        return ImageServerUrl;
    }

    public void setImageServerUrl(String imageServerUrl) {
        imageServerUrl = imageServerUrl.replaceAll(" ", "%20");
        Log.d("Image URls","Cart image replace URL:"+imageServerUrl);
        this.ImageServerUrl = "http://shoppercrux.com/image/"+imageServerUrl;
    }

    public void setProductName(String productName) { this.productName = productName; }

    public String getProductName() { return productName; }

    public void setProductQuantity(String productQuantity) { this.productQuantity = productQuantity; }

    public String getProductQuantity() { return productQuantity; }

    public void setProductId(String productId) { this.productId = productId; }

    public String getProductId() { return productId; }

    public void setProductPrice(String productPrice) { this.productPrice = productPrice; }

    public String getProductPrice() { return productPrice; }

    public void setTotalPrice(String totalPrice) { this.totalPrice = totalPrice; }

    public String getTotalPrice() { return totalPrice; }

}
