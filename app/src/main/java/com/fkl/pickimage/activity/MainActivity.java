package com.fkl.pickimage.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.fkl.pickimage.R;
import com.fkl.pickimage.app.app;
import com.fkl.pickimage.model.MediaBean;
import com.fkl.pickimage.utils.Extras;
import com.fkl.pickimage.utils.NativeImageLoader;
import com.fkl.pickimage.utils.PictureUtils;
import com.fkl.pickimage.utils.RequestCode;
import com.fkl.pickimage.utils.SendImageHelper;
import com.fkl.pickimage.utils.StorageType;
import com.fkl.pickimage.utils.StorageUtil;
import com.fkl.pickimage.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String JPG = ".jpg";
    private GridView mPiclist;
    private List<MediaBean> mediaList = new ArrayList<>();
    private ImageShowAdpter adpter=new ImageShowAdpter();
    private Button pick_imgs;
    private Button take_photo;
    private String mPublicPhotoPath;
    private Uri uri;
    private String path;
    private List<String> pathList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //  pathList.add("/storage/emulated/0/you/upload/20180903_094454.jpg");

        mPiclist.setAdapter(adpter);
    }
    private void startTake() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断是否有相机应用
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //创建临时图片文件
            File photoFile = null;
            try {
                photoFile = PictureUtils.createPublicImageFile();
                mPublicPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //设置Action为拍照
            if (photoFile != null) {
                takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                //这里加入flag
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName()+".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, RequestCode.TAKE_PHOTO);
            }
        }
    }
    private void initView() {
        mPiclist = (GridView) findViewById(R.id.piclist);
        mPiclist.setOnItemClickListener(this);
        pick_imgs = (Button) findViewById(R.id.pick_imgs);
        pick_imgs.setOnClickListener(this);
        take_photo = (Button) findViewById(R.id.take_photo);
        take_photo.setOnClickListener(this);
    }


    private void pickImge(int from, int requestCode) {
        PickImageActivity.start(this, requestCode, from, tempFile(), true, 9,
                true, false, 0, 0);
    }

    private String tempFile() {
        String filename = StringUtil.get32UUID() + JPG;
        return StorageUtil.getWritePath(filename, StorageType.TYPE_TEMP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.PICK_IMAGE) {
            onPickImageActivityResult(requestCode, data);
        } else if (RequestCode.TAKE_PHOTO == requestCode) {
            uri = Uri.parse(mPublicPhotoPath);
            path = uri.getPath();
            Log.e("*****", "onActivityResult: "+path );
            PictureUtils.galleryAddPic(mPublicPhotoPath, this);
            MediaBean mediaBean = new MediaBean(path, null, 1);
            mediaList.add(mediaBean);
            pathList.add(path);
            adpter.notifyDataSetChanged();
        }

    }

    private void onPickImageActivityResult(int requestCode, Intent data) {
        if (data == null) {
            return;
        }
        // pathList.clear();
        boolean local = data.getBooleanExtra(Extras.EXTRA_FROM_LOCAL, false);
        if (local) {
            // 本地相册
            sendImageAfterSelfImagePicker(data);

        }
    }

    private void sendImageAfterSelfImagePicker(Intent data) {
        SendImageHelper.sendImageAfterSelfImagePicker(this, data, new SendImageHelper.Callback() {
            @Override
            public void sendImage(final File file, boolean isOrig) {
                mediaList.add(new MediaBean(file.getAbsolutePath(), null, 1));
                // gvAdapter.notifyDataSetChanged();
                pathList.add(file.getAbsolutePath());
                adpter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i < mediaList.size()) {
            Intent intent = new Intent(this, PhotoViewActivity.class);
            intent.putExtra("currentPosition", i);
             intent.putStringArrayListExtra("questionlistdataBean", (ArrayList<String>) pathList);
             startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
//        if (view == img) {
//            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
//            startActivityForResult(intent, RequestCode.TAKE_PHOTO);
//        }
        if (view==take_photo){
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                PermissionGen.with(this).permissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE})
                .addRequestCode(app.CARMERA_CODE).request();
            }else {
                startTake();
            }
        }else if (view==pick_imgs){
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                PermissionGen.with(this).permissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE})
                        .addRequestCode(app.ALBUM_CODE).request();

            }else {
                openAlbum();
            }

        }
    }
    //打开相机失败
@PermissionFail(requestCode = app.CARMERA_CODE)
public void openCameraFail(){
    Toast.makeText(this, "请打开相机权限！", Toast.LENGTH_SHORT).show();
    app.goToSetting(this);
}
    @PermissionFail(requestCode = app.ALBUM_CODE)
    public void openAlbumFail(){
        app.goToSetting(this);
        Toast.makeText(this, "请打开文件读写权限！", Toast.LENGTH_SHORT).show();
    }
    @PermissionSuccess(requestCode = app.CARMERA_CODE)
    public void openCameraSuccess(){
        startTake();
    }
    @PermissionSuccess(requestCode = app.ALBUM_CODE)
    public void openAlbumSuccess(){
       openAlbum();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }

    private void openAlbum() {
        int from = PickImageActivity.FROM_LOCAL;
        int requestCode = RequestCode.PICK_IMAGE;
        pickImge(from,requestCode);
    }

    private class ImageShowAdpter extends BaseAdapter {

        @Override
        public int getCount() {
            return mediaList.size() == 0 ? 0 : mediaList.size() ;
        }

        @Override
        public Object getItem(int i) {
            return mediaList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(MainActivity.this).inflate(R.layout.pic_item, null, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
                MediaBean mediaBean = mediaList.get(i);
                Glide.with(MainActivity.this).load(mediaBean.getPath())
                        .into(holder.mPic);

            return view;
        }


        class ViewHolder {
            public View rootView;
            public ImageView mPic;

            public ViewHolder(View rootView) {
                this.rootView = rootView;
                this.mPic = (ImageView) rootView.findViewById(R.id.pic);

            }

        }
    }

}
