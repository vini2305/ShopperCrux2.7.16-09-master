package com.wvs.shoppercrux.ProductDescription;

/**
 * Created by JUNED on 6/16/2016.
 */
public class GetDataAdapter {

    public String productId;
    public String productName;
    public String productImage;
    private double productPrice;
    private String productDesription;

    public String getProductId() { return  productId; }

    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        productImage = productImage.replaceAll(" ", "%20");
        this.productImage = "http://shoppercrux.com/image/"+productImage;
    }

    public String getProductDesription() {
        return productDesription;
    }

    public void setProductDesription(String productDesription) {
        this.productDesription = productDesription;
    }
//    public void setImageServerUrl(String imageServerUrl) {
//        this.ImageServerUrl = "http://prachodayat.in/shoppercrux/image/"+imageServerUrl;
//    }

}
