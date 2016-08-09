package com.liujian.gitbub.recyclerviewcitypickerdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.liujian.gitbub.recyclerviewcitypickerdemo.adapter.CitySearchAdapter;
import com.liujian.gitbub.recyclerviewcitypickerdemo.adapter.CityShowAdapter;
import com.liujian.gitbub.recyclerviewcitypickerdemo.bean.City;
import com.liujian.gitbub.recyclerviewcitypickerdemo.db.DBManager;
import com.liujian.gitbub.recyclerviewcitypickerdemo.helper.OnCityClickListener;
import com.liujian.gitbub.recyclerviewcitypickerdemo.helper.OnLocateClickListener;
import com.liujian.gitbub.recyclerviewcitypickerdemo.utils.CurrentCityState;
import com.liujian.gitbub.recyclerviewcitypickerdemo.utils.ToastUtils;
import com.liujian.gitbub.recyclerviewcitypickerdemo.view.SideLetterBar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    public AMapLocationClient mLocationClient = null;
    public AMapLocationListener mLocationListener;
    public AMapLocationClientOption mLocationOption = null;

    private List<City> mAllCities;
    private DBManager dbManager;
    private CitySearchAdapter mCitySearchAdapter;
    private CityShowAdapter mCityShowAdapter;

    private RecyclerView recycler_all_city;
    private RecyclerView recycler_search_result;
    private ImageView back;
    private EditText et_search;
    private ImageView iv_search_clear;

    private TextView tv_letter_overlay;
    private SideLetterBar side_letter_bar;
    private LinearLayout empty_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        initData();
        initView();
        initLocation();
    }

    private void initView() {
        recycler_all_city= (RecyclerView) findViewById(R.id.recycler_all_city);
        recycler_search_result= (RecyclerView) findViewById(R.id.recycler_search_result);
        back= (ImageView) findViewById(R.id.back);
        et_search= (EditText) findViewById(R.id.et_search);
        iv_search_clear= (ImageView) findViewById(R.id.iv_search_clear);
        tv_letter_overlay= (TextView) findViewById(R.id.tv_letter_overlay);
        side_letter_bar= (SideLetterBar) findViewById(R.id.side_letter_bar);
        empty_view= (LinearLayout) findViewById(R.id.empty_view);

        recycler_all_city.setLayoutManager(new LinearLayoutManager(this));
        recycler_search_result.setLayoutManager(new LinearLayoutManager(this));
        recycler_search_result.setAdapter(mCitySearchAdapter);

        side_letter_bar.setOverlay(tv_letter_overlay);
        side_letter_bar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int position = mCityShowAdapter.getLetterPosition(letter);
                //recycler_all_city.smoothScrollToPosition(position);
                LinearLayoutManager llm = (LinearLayoutManager) recycler_all_city
                        .getLayoutManager();
                llm.scrollToPositionWithOffset(position, 0);
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                if (TextUtils.isEmpty(keyword)) {
                    iv_search_clear.setVisibility(View.GONE);
                    empty_view.setVisibility(View.GONE);
                    recycler_search_result.setVisibility(View.GONE);
                } else {
                    iv_search_clear.setVisibility(View.VISIBLE);
                    recycler_search_result.setVisibility(View.VISIBLE);
                    List<City> result = dbManager.searchCity(keyword);
                    if (result == null || result.size() == 0) {
                        empty_view.setVisibility(View.VISIBLE);
                    } else {
                        empty_view.setVisibility(View.GONE);
                        mCitySearchAdapter.changeData(result);
                    }
                }
            }
        });
        recycler_all_city.setAdapter(mCityShowAdapter);

        iv_search_clear.setOnClickListener(this);
        back.setOnClickListener(this);
    }


    private void initData() {
        dbManager = new DBManager(this);
        dbManager.copyDBFile();
        mAllCities = dbManager.getAllCities();
        mCityShowAdapter=new CityShowAdapter(this,mAllCities);
        mCitySearchAdapter=new CitySearchAdapter(this,mAllCities);
        mCityShowAdapter.setOnCityClickListener(new OnCityClickListener() {
            @Override
            public void onCityClick(String cityName) {
                ToastUtils.showToast(MainActivity.this,cityName);
            }
        });
        mCityShowAdapter.setOnLocateClickListener(new OnLocateClickListener() {
           @Override
           public void onLocateClick() {
                mCityShowAdapter.updateLocateState(CurrentCityState.LOCATING,null);
                mLocationClient.startLocation();
           }
       });
        mCitySearchAdapter.setOnCityClickListener(new OnCityClickListener() {
            @Override
            public void onCityClick(String cityName) {
                ToastUtils.showToast(MainActivity.this,cityName);
            }
        });
    }

    /**
     * 初始化高德地图SDK配置信息
     */
    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationListener=new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if(aMapLocation!=null){
                    if(0==aMapLocation.getErrorCode()){
                         String city=aMapLocation.getCity();
                         city=city.substring(0,city.length()-1);
                         //更新update信息
                         mCityShowAdapter.updateLocateState(CurrentCityState.SUCCESS,city);
                    }else{
                        //定位失败
                        Log.i(TAG, "onLocationChanged: "+aMapLocation.getErrorCode()+" "+aMapLocation.getErrorInfo());
                        mCityShowAdapter.updateLocateState(CurrentCityState.FAILED,null);
                    }
                }
            }
        };
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
       //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
       //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        if (mLocationOption.isOnceLocationLatest()) {
            mLocationOption.setOnceLocationLatest(true);
           //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
           //如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会。
        }
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //开始定位
        mLocationClient.startLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLocationClient!=null)
         mLocationClient.stopAssistantLocation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.iv_search_clear:
                iv_search_clear.setVisibility(View.GONE);
                et_search.setText("");
                empty_view.setVisibility(View.GONE);
                recycler_search_result.setVisibility(View.GONE);
                break;
        }
    }
}
