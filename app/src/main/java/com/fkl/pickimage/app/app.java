package com.fkl.pickimage.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.fkl.pickimage.utils.StorageUtil;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Administrator on 2018/8/31.
 */

public class app extends Application {
    public static Context context;
    public static final int CARMERA_CODE=1001;
    public static final int ALBUM_CODE=1002;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        StorageUtil.init(context, null);

    }
    public static Context getContext() {
        return context;
    }

    public static void goToSetting(Context context){
        //go to setting view
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }
}
