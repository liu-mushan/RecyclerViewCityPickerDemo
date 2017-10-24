package com.liujian.gitbub.recyclerviewcitypickerdemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 城市名称和拼音的封装
 * @author : liujian
 * @since : 2016/8/8 10:16
 */
public class City implements Parcelable {
    private String name;
    private String pinyin;

    public City(String name, String pinyin) {
        this.pinyin = pinyin;
        this.name = name;
    }

    public City() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", pinyin='" + pinyin + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.pinyin);
    }


    protected City(Parcel in) {
        this.name = in.readString();
        this.pinyin = in.readString();
    }

    public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {
        @Override
        public City createFromParcel(Parcel source) {
            return new City(source);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };
}
