package com.wvs.shoppercrux.classes;

/**
 * Created by root on 8/8/16.
 */
public class CatDrawerItem {
    private boolean showNotify;
    private String title;


    public CatDrawerItem() {

    }

    public CatDrawerItem(boolean showNotify, String title) {
        this.showNotify = showNotify;
        this.title = title;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
