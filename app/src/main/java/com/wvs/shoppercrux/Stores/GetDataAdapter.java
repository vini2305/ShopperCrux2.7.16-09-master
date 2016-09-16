package com.wvs.shoppercrux.Stores;

/**
 * Created by JUNED on 6/16/2016.
 */
public class GetDataAdapter {

    public String ImageServerUrl;
    public String ImageTitleName;
    private String SellerID;
    private String sellerAddress;

    public String getSellerAddress() { return  sellerAddress; }

    public void setSellerAddress(String sellerAddress) { this.sellerAddress=sellerAddress; }

    public String getSellerID() {
        return SellerID;
    }

    public void setSellerID(String sellerID) {
        SellerID = sellerID;
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

}
