package com.liujian.gitbub.recyclerviewcitypickerdemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liujian.gitbub.recyclerviewcitypickerdemo.R;
import com.liujian.gitbub.recyclerviewcitypickerdemo.bean.City;
import com.liujian.gitbub.recyclerviewcitypickerdemo.helper.OnCityClickListener;

import java.util.List;

/**
 * @project_Name: RecyclerViewCityPickerDemo
 * @package: com.liujian.gitbub.recyclerviewcitypickerdemo.adapter
 * @description: 城市搜索结果的适配器
 * @author: liujian
 * @date: 2016/8/8 10:33
 * @version: V1.0
 */
public class CitySearchAdapter extends RecyclerView.Adapter<CitySearchAdapter.CitySearchHolder>{
    private Context mContext;
    private List<City> mCities;
    private LayoutInflater mInflater;
    private OnCityClickListener mOnCityClickListener;

    public CitySearchAdapter(Context mContext,@NonNull List<City> mLists){
        this.mContext=mContext;
        this.mCities=mLists;
        mInflater=LayoutInflater.from(mContext);
    }

    public void setOnCityClickListener(OnCityClickListener onCityClickListener) {
        mOnCityClickListener = onCityClickListener;
    }

    @Override
    public CitySearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=mInflater.inflate(R.layout.item_search_result_listview,parent,false);
        return new CitySearchHolder(view);
    }

    @Override
    public void onBindViewHolder(CitySearchHolder holder, final int position) {
        holder.tv_item_result_listview_name.setText(mCities.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mOnCityClickListener!=null){
                    mOnCityClickListener.onCityClick(mCities.get(position).getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCities==null?0:mCities.size();
    }

    public void changeData(List<City> list){
        if (mCities == null){
            mCities = list;
        }else{
            mCities.clear();
            mCities.addAll(list);
        }
        notifyDataSetChanged();
    }

    static class CitySearchHolder extends RecyclerView.ViewHolder{
        private TextView tv_item_result_listview_name;
        public CitySearchHolder(View itemView) {
            super(itemView);
            tv_item_result_listview_name= (TextView) itemView.findViewById(R.id.tv_item_result_listview_name);
        }
    }
}
