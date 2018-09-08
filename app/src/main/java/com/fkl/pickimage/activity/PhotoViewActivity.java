package com.fkl.pickimage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.fkl.pickimage.R;
import com.fkl.pickimage.adapter.MyImageAdapter;
import com.fkl.pickimage.view.PhotoViewPager;

import java.util.List;

public class PhotoViewActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG = PhotoViewActivity.class.getSimpleName();
    private PhotoViewPager mViewPager;
    private int currentPosition;
    private MyImageAdapter adapter;
    private TextView mTvImageCount;
    private List<String> Urls;
    private ImageView ivBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        initView();
        initData();
    }

    private void initView() {
        mViewPager = (PhotoViewPager) findViewById(R.id.view_pager_photo);
        mTvImageCount = (TextView) findViewById(R.id.tv_image_count);
        ivBack=(ImageView)findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
       // mTvSaveImage = (TextView) findViewById(R.id.tv_save_image_photo);

    }

    private void initData() {
        Intent intent = getIntent();
        currentPosition = intent.getIntExtra("currentPosition", 0);
//        HomeQuestionListModel.DataBeanX DataBean = ((HomeQuestionListModel.DataBeanX) );
       Urls= (List<String>) intent.getSerializableExtra("questionlistdataBean");
//        Urls = DataBean.getAttach().getImage().getOri();
        adapter = new MyImageAdapter(Urls, this);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(currentPosition, false);
        mTvImageCount.setText(currentPosition+1 + "/" + Urls.size());
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
                mTvImageCount.setText(currentPosition + 1 + "/" + Urls.size());
            }
        });
    }

    @Override
    public void onClick(View view) {
       if (ivBack==view){
           finish();
       }
    }
}
