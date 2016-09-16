package com.wvs.shoppercrux.classes;

/**
 * Created by Varuni on 23-07-2016.
 */
public class NavDrawerItem {
    private String title;
    private String icon;

    public  NavDrawerItem() {

    }

    public NavDrawerItem( String title,String icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
