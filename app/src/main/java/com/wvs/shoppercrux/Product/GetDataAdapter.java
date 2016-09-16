package com.wvs.shoppercrux.Product;

/**
 * Created by JUNED on 6/16/2016.
 */
public class GetDataAdapter {

    public String ImageServerUrl;
    public String ImageTitleName;
    public String productId,storeName;
    private double price;
    public String sellerId;

    public String getProductId() {
        return productId;
    }

    public String setProductId(String productId) {
        this.productId = productId;
        return productId;
    }

    public String getImageServerUrl() {
        return ImageServerUrl;
    }

    public void setImageServerUrl(String imageServerUrl) {
        imageServerUrl = imageServerUrl.replaceAll(" ", "%20");
        this.ImageServerUrl = "http://shoppercrux.com/image/"+imageServerUrl;
    }

    public String getImageTitleName() {
        return ImageTitleName;
    }

    public void setImageTitleNamee(String Imagetitlename) {
        this.ImageTitleName = Imagetitlename;
    }

    public String getStoreName() { return storeName; }

    public void setStoreName(String storeName) { this.storeName = storeName; }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

}
