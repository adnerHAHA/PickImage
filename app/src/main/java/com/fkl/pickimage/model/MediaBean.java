package com.fkl.pickimage.model;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2018/9/3.
 */

public class MediaBean {
    private String path;
    private Bitmap bitmap;
    private int type;

    public MediaBean() {
    }

    public MediaBean(String path, Bitmap bitmap, int type) {
        this.path = path;
        this.bitmap = bitmap;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
