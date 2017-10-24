package com.liujian.gitbub.recyclerviewcitypickerdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liujian.gitbub.recyclerviewcitypickerdemo.R;
import com.liujian.gitbub.recyclerviewcitypickerdemo.helper.OnCityClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 热门城市的适配器
 * @author : liujian
 * @since  : 2016/8/8 10:34
 */
class HotCityAdapter extends RecyclerView.Adapter<HotCityAdapter.HotCityViewHolder> {
    private Context mContext;
    private List<String> mCities;
    private LayoutInflater mInflater;
    private OnCityClickListener mOnCityClickListener;

    HotCityAdapter(Context context) {
        mContext = context;
        mCities = new ArrayList<>();
        mInflater = LayoutInflater.from(mContext);
        mCities.add("北京");
        mCities.add("上海");
        mCities.add("广州");
        mCities.add("深圳");
        mCities.add("长沙");
        mCities.add("杭州");
        mCities.add("南京");
        mCities.add("天津");
        mCities.add("武汉");
        mCities.add("重庆");
    }

    public void setOnCityClickListener(OnCityClickListener onCityClickListener) {
        mOnCityClickListener = onCityClickListener;
    }


    @Override
    public HotCityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HotCityViewHolder(mInflater.inflate(R.layout.item_hot_city_gridview, parent, false));
    }

    @Override
    public void onBindViewHolder(HotCityViewHolder holder, final int position) {
        holder.tv_hot_city_name.setText(mCities.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCityClickListener != null) {
                    mOnCityClickListener.onCityClick(mCities.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCities.size();
    }

    static class HotCityViewHolder extends RecyclerView.ViewHolder {
        TextView tv_hot_city_name;

        HotCityViewHolder(View itemView) {
            super(itemView);
            tv_hot_city_name = (TextView) itemView.findViewById(R.id.tv_hot_city_name);
        }
    }
}
