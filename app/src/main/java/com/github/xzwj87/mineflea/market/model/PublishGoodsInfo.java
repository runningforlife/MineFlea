package com.github.xzwj87.mineflea.market.model;

import java.util.Date;
import java.util.List;

/**
 * Created by JasonWang on 2016/9/22.
 */
public class PublishGoodsInfo {

    public static final String GOODS_NAME = "name";
    public static final String GOODS_PUBLISHER = "publisher_id";
    public static final String GOODS_PRICE = "Price";
    public static final String GOODS_HIGH_PRICE = "high_price";
    public static final String GOODS_RELEASE_DATE = "release_date";
    public static final String GOODS_LOC = "location";
    public static final String GOODS_STARS = "stars";
    public static final String GOODS_IMAGES = "images";
    public static final String GOODS_NOTE = "note";

    public static final String GOODS_IMG_STRING_SEP = "-";

    private String mId;

    private String mName;

    private String mPublisherId;

    private double mHighPrice;

    private double mPrice;

    private String mNote;

    private long mReleasedDate;

    private String mLoc;

    private List<String> mImageUri;

    private int mStars;

    public PublishGoodsInfo(){
        mReleasedDate = System.currentTimeMillis();
    }

    public String getId(){
        return mId;
    }

    public void setId(String id){
        mId = id;
    }

    public String getName(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }


    public String getPublisherId(){
        return mPublisherId;
    }

    public void setPublisherId(String publisher){
        mPublisherId = publisher;
    }

    public double getHighPrice(){
        return mHighPrice;
    }

    public void setHighPrice(double price){
        mHighPrice = price;
    }

    public double getPrice(){
        return mPrice;
    }

    public void setPrice(double price){
        mPrice = price;
    }

    public void setNote(String note){
        mNote = note;
    }

    public String getNote(){
        return mNote;
    }

    public long getReleasedDate(){
        return mReleasedDate;
    }

    public void setReleasedDate(long date){
        mReleasedDate = date;
    }

    public void setLocation(String location){
        mLoc = location;
    }

    public String getLocation(){
        return mLoc;
    }

    public List<String> getImageUri(){
        return mImageUri;
    }

    public void setImageUri(List<String> uri){
        mImageUri = uri;
    }

    public void setStars(int stars){
        mStars = stars;
    }

    public int getStars(){
        return mStars;
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("-------- Goods Detail --------\n");
        sb.append("id = " + mId + "\n");
        sb.append("name = " + mName + "\n");
        sb.append("high price = " + mHighPrice + "\n");
        sb.append("lower price = " + mPrice + "\n");
        sb.append("released date = " + new Date(mReleasedDate) + "\n");
        sb.append("-------------------------------\n");

        return sb.toString();
    }
}
