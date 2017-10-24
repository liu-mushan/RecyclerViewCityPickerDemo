package com.liujian.gitbub.recyclerviewcitypickerdemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liujian.gitbub.recyclerviewcitypickerdemo.R;
import com.liujian.gitbub.recyclerviewcitypickerdemo.bean.City;
import com.liujian.gitbub.recyclerviewcitypickerdemo.helper.OnCityClickListener;
import com.liujian.gitbub.recyclerviewcitypickerdemo.helper.OnLocateClickListener;
import com.liujian.gitbub.recyclerviewcitypickerdemo.utils.CurrentCityState;
import com.liujian.gitbub.recyclerviewcitypickerdemo.utils.PinyinUtils;

import java.util.HashMap;
import java.util.List;

/**
 * 城市展示列表的适配器
 * @author : liujian
 * @since  : 2016/8/8 10:32
 */
public class CityShowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<City> mCities;
    /**
     * 存放拼音首字母和下标的Map集合
     */
    private HashMap<String, Integer> letterIndexes;

    private OnCityClickListener mOnCityClickListener;
    private OnLocateClickListener mOnLocateClickListener;

    private int localState = CurrentCityState.LOCATING;
    private String localCity;

    private static final int VIEW_TYPE_FIRST = 111;
    private static final int VIEW_TYPE_SECONED = 222;
    private static final int VIEW_TYPE_THREAD = 333;

    public CityShowAdapter(Context context, @NonNull List<City> cities) {
        mContext = context;
        mCities = cities;
        mInflater = LayoutInflater.from(mContext);
        letterIndexes = new HashMap<>();
        //为集合添加
        mCities.add(0, new City("定位", "0"));
        mCities.add(1, new City("热门", "1"));
        //得到定位,热门,和每个首字母的position,方便于右边的字母选择点击然后RecyclerView跳转
        for (int i = 0; i < mCities.size(); i++) {
            //当前城市拼音首字母
            String currentLetter = PinyinUtils.getFirstLetter(mCities.get(i).getPinyin());
            //上个首字母，如果不存在设为""
            String previousLetter = i >= 1 ? PinyinUtils.getFirstLetter(mCities.get(i - 1).getPinyin()) : "";
            if (!TextUtils.equals(currentLetter, previousLetter)) {
                letterIndexes.put(currentLetter, i);
            }
        }
    }

    /**
     * 更新定位状态
     *
     * @param state
     */
    public void updateLocateState(int state, String city) {
        this.localState = state;
        this.localCity = city;
        notifyDataSetChanged();
    }

    /**
     * 获取字母索引的位置
     *
     * @param letter
     * @return
     */
    public int getLetterPosition(String letter) {
        Integer integer = letterIndexes.get(letter);
        return integer == null ? -1 : integer;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=null;
        if(viewType==VIEW_TYPE_FIRST){
            view=mInflater.inflate(R.layout.view_locate_city,parent,false);
            return new LocateCityHolder(view);
        }else if(viewType==VIEW_TYPE_SECONED){
            view=mInflater.inflate(R.layout.view_hot_city,parent,false);
            return new HotCityHolder(view);
        }
        view=mInflater.inflate(R.layout.item_city_listview,parent,false);
        return new CityShowHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(VIEW_TYPE_FIRST==getItemViewType(position)){
            switch (localState){
                case CurrentCityState.LOCATING:
                    ((LocateCityHolder)holder).tv_located_city.setText(mContext.getString(R.string.locating));
                    break;
                case CurrentCityState.FAILED:
                    ((LocateCityHolder)holder).tv_located_city.setText(mContext.getString(R.string.located_failed));
                    break;
                case CurrentCityState.SUCCESS:
                    ((LocateCityHolder)holder).tv_located_city.setText(localCity);
                    break;
            }

            ((LocateCityHolder)holder).layout_locate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     if(localState==CurrentCityState.FAILED){
                         if(mOnLocateClickListener!=null)
                             mOnLocateClickListener.onLocateClick();
                     }else if(localState==CurrentCityState.SUCCESS){
                         if(mOnCityClickListener!=null)
                             mOnCityClickListener.onCityClick(localCity);
                     }
                }
            });

        }else if(VIEW_TYPE_SECONED==getItemViewType(position)){
            HotCityAdapter adapter=new HotCityAdapter(mContext);
            ((HotCityHolder)holder).gridview_hot_city.setLayoutManager(new GridLayoutManager(mContext,3));
            ((HotCityHolder)holder).gridview_hot_city.setAdapter(adapter);
            //设置禁止滑动
            ((HotCityHolder)holder).gridview_hot_city.setNestedScrollingEnabled(false);
            adapter.setOnCityClickListener(mOnCityClickListener);
        }else{
           if(position>=1){
               final String city = mCities.get(position).getName();
               ((CityShowHolder)holder).tv_item_city_listview_name.setText(city);
               String currentLetter = PinyinUtils.getFirstLetter(mCities.get(position).getPinyin());
               String previousLetter = position >= 1 ? PinyinUtils.getFirstLetter(mCities.get(position - 1).getPinyin()) : "";
               if (!TextUtils.equals(currentLetter, previousLetter)){
                   ((CityShowHolder)holder).tv_item_city_listview_letter.setVisibility(View.VISIBLE);
                   ((CityShowHolder)holder).tv_item_city_listview_letter.setText(currentLetter);
               }else{
                   ((CityShowHolder)holder).tv_item_city_listview_letter.setVisibility(View.GONE);
               }
               ((CityShowHolder)holder).tv_item_city_listview_name.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (mOnCityClickListener != null){
                           mOnCityClickListener.onCityClick(city);
                       }
                   }
               });
           }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_FIRST;
        } else if (position == 1) {
            return VIEW_TYPE_SECONED;
        }
        return VIEW_TYPE_THREAD;
    }

    @Override
    public int getItemCount() {
        return mCities.size();
    }

    static class LocateCityHolder extends RecyclerView.ViewHolder {
        LinearLayout layout_locate;
        TextView tv_located_city;
        public LocateCityHolder(View itemView) {
            super(itemView);
            layout_locate= (LinearLayout) itemView.findViewById(R.id.layout_locate);
            tv_located_city= (TextView) itemView.findViewById(R.id.tv_located_city);
        }
    }

    static class HotCityHolder extends RecyclerView.ViewHolder {
        RecyclerView gridview_hot_city;
        public HotCityHolder(View itemView) {
            super(itemView);
            gridview_hot_city= (RecyclerView) itemView.findViewById(R.id.gridview_hot_city);
        }
    }

    static class CityShowHolder extends RecyclerView.ViewHolder {
        TextView tv_item_city_listview_letter;
        TextView tv_item_city_listview_name;
        public CityShowHolder(View itemView) {
            super(itemView);
            tv_item_city_listview_name= (TextView) itemView.findViewById(R.id.tv_item_city_listview_name);
            tv_item_city_listview_letter= (TextView) itemView.findViewById(R.id.tv_item_city_listview_letter);
        }
    }


    public void setOnLocateClickListener(OnLocateClickListener onLocateClickListener) {
        mOnLocateClickListener = onLocateClickListener;
    }

    public void setOnCityClickListener(OnCityClickListener onCityClickListener) {
        mOnCityClickListener = onCityClickListener;
    }
}
