package com.github.xzwj87.mineflea.market.data.repository;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import com.github.xzwj87.mineflea.market.data.ResponseCode;
import com.github.xzwj87.mineflea.market.data.cache.FileCache;
import com.github.xzwj87.mineflea.market.data.cache.FileCacheImpl;
import com.github.xzwj87.mineflea.market.data.remote.RemoteDataSource;
import com.github.xzwj87.mineflea.market.data.remote.RemoteSourceCallBack;
import com.github.xzwj87.mineflea.market.model.PublishGoodsInfo;
import com.github.xzwj87.mineflea.market.model.UserInfo;
import com.github.xzwj87.mineflea.market.presenter.BasePresenter;
import com.github.xzwj87.mineflea.market.presenter.PresenterCallback;
import com.github.xzwj87.mineflea.utils.UserInfoUtils;

import static com.github.xzwj87.mineflea.market.presenter.BasePresenter.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by JasonWang on 2016/9/20.
 */

@Singleton
public class DataRepository implements BaseRepository,RemoteSourceCallBack{
    public static final String TAG = DataRepository.class.getSimpleName();

    @Inject FileCacheImpl mCache;
    @Inject
    RemoteDataSource mCloudSrc;

    private HashMap<String,PresenterCallback> mPresenterCbs;

    private PublishGoodsInfo mGoodsInfo;

    @Inject
    public DataRepository(FileCacheImpl cache, RemoteDataSource cloudSource){
        mCloudSrc = cloudSource;
        mCache = cache;
        mPresenterCbs = new HashMap<>();
    }

    public void init(){
        mCloudSrc.setCloudCallback(this);
    }

    @Override
    public void publishGoods(PublishGoodsInfo goods) {
        Log.v(TAG,"publishGoods(): goods = " + goods);

        mGoodsInfo = goods;

        if(!mCache.isCached(goods.getId(), FileCache.CACHE_TYPE_GOODS)){
            mCache.saveToFile(goods);
        }

        mCloudSrc.publishGoods(goods);
    }

    @Override
    public void register(UserInfo userInfo) {
        mCloudSrc.register(userInfo);
    }

    @Override
    public void login(UserInfo info) {
        mCloudSrc.login(info);
    }

    @Override
    public void loginBySms(String telNumber, String authCode) {
        mCloudSrc.loginBySms(telNumber,authCode);
    }

    @Override
    public void registerBySms(String telNumber, String authCode) {
        mCloudSrc.registerBySms(telNumber,authCode);
    }

    @Override
    public void sendSmsAuthCode(String number) {
        Log.v(TAG,"sendSmsAuthCode()");
        mCloudSrc.sendAuthCode(number);
    }

    @Override
    public void resetPwdByAccount(String account) {
        if(UserInfoUtils.isTelNumberValid(account)){
            mCloudSrc.sendResetPwdBySms(account);
        }else{
            mCloudSrc.sendResetPwdEmail(account);
        }
    }

    @Override
    public void logout() {
        mCloudSrc.logOut();

        String currentUserId = getCurrentUserId();
        if(mCache.isCached(currentUserId,FileCache.CACHE_TYPE_USER)){
            mCache.delete(currentUserId,FileCache.CACHE_TYPE_USER);
        }
    }

    @Override
    public void uploadImageById(String id, String imgUri, boolean isUser, boolean showProcess) {
        if(TextUtils.isEmpty(imgUri) || TextUtils.isEmpty(id))  return;

        String type = FileCache.CACHE_TYPE_USER;
        if(!isUser){
          type = FileCache.CACHE_TYPE_GOODS;
        }

        if(!mCache.isCached(id,type)){
            mCache.saveImgToFile(imgUri,type);
        }

        mCloudSrc.uploadImg(imgUri,showProcess);
    }

    @Override
    public String getCurrentUserId() {
        return mCloudSrc.getCurrentUserId();
    }

    @Override
    public UserInfo getCurrentUser() {
        String id = getCurrentUserId();
        if(mCache.isCached(id,FileCache.CACHE_TYPE_USER)){
            return mCache.getUserCache(id);
        }

        return mCloudSrc.getCurrentUser();
    }

    @Override
    public void updateCurrentUserInfo(String key, String val) {
        mCloudSrc.updateCurrentUserInfo(key,val);

        if(mCache.isExpired(getCurrentUserId(),FileCache.CACHE_TYPE_USER)
            && mCache.isCached(getCurrentUserId(),FileCache.CACHE_TYPE_USER)){
            mCache.updateFile(getCurrentUser());
        }
    }

    @Override
    public void getUserInfoById(String id) {
        if (mCache.isCached(id, FileCache.CACHE_TYPE_USER) &&
                !mCache.isExpired(id, FileCache.CACHE_TYPE_USER)) {
            UserInfo userInfo = mCache.getUserCache(id);
            PresenterCallback callback = mPresenterCbs.get(PRESENTER_USER_DETAIL);
            if(callback != null){
                final Message message = new Message();
                message.obj = userInfo;
                message.what = ResponseCode.RESP_GET_USER_INFO_SUCCESS;

                callback.onComplete(message);
            }
        }else {
            mCloudSrc.getUserInfoById(id);
        }

    }

    @Override
    public void getAllGoods() {
        Log.v(TAG,"getAllGoods()");

        List<PublishGoodsInfo> goodsList = mCache.getAllGoodsCache();
        if(goodsList.size() > 0){
            final Message msg = new Message();
            msg.obj = goodsList;
            msg.what = ResponseCode.RESP_GET_GOODS_LIST_SUCCESS;

            PresenterCallback callback = mPresenterCbs.get(PRESENTER_GOODS_LIST);
            if(callback != null){
                callback.onComplete(msg);
            }
        }

        mCloudSrc.getAllGoods();
    }

    @Override
    public void getGoodsListByUserId(String id) {
        if(!mCache.isExpired(id,FileCache.CACHE_TYPE_USER) &&
                mCache.isCached(id,FileCache.CACHE_TYPE_USER)){
            UserInfo userInfo = mCache.getUserCache(id);
            List<String> goodsIdList = userInfo.getGoodsList();
            List<PublishGoodsInfo> goodsList = new ArrayList<>();

            /*
             * we may want to update one by one instead of get it all together
             */
            for(int i = 0; i < goodsIdList.size(); ++i){
                String goodsId = goodsIdList.get(i);
                if(mCache.isCached(goodsId,FileCache.CACHE_TYPE_GOODS)
                        && !mCache.isExpired(goodsId,FileCache.CACHE_TYPE_GOODS)) {
                    goodsList.add(mCache.getGoodsCache(goodsId));
                //Todo: if cache miss, get it from cloud
                }else{
                    mCloudSrc.getGoodsById(goodsId);
                }
            }

            PresenterCallback callback = mPresenterCbs.get(PRESENTER_GOODS);
            if(callback != null){
                final Message message = new Message();
                message.obj = goodsIdList;
                message.what = ResponseCode.RESP_GET_GOODS_LIST_SUCCESS;
                callback.onComplete(message);
            }
        }else{
            mCloudSrc.getGoodsListByUserId(id);
        }
    }

    @Override
    public void queryFavorGoodsListByUserId(String id) {
        mCloudSrc.queryFavoriteGoodsList(id);
    }

    @Override
    public void queryUserFollowerListByUserId(String id) {

    }

    @Override
    public void queryUserFolloweeListByUserId(String id) {

    }

    @Override
    public void registerCallBack(@PRESENTER_TYPE String type, PresenterCallback callback) {
        if(!mPresenterCbs.containsKey(type)) {
            mPresenterCbs.put(type, callback);
        }
    }

    @Override
    public void unregisterCallback(@PRESENTER_TYPE String type) {
        if(mPresenterCbs.containsKey(type)){
            mPresenterCbs.remove(type);
        }
    }

    public void onImgUploadComplete(Message msg) {

        Iterator iterator = mPresenterCbs.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            PresenterCallback callback = (PresenterCallback)entry.getValue();
            if(callback != null){
                callback.onComplete(msg);
            }
        }
    }

    @Override
    public void onGetUserInfoDone(Message msg) {
        PresenterCallback listener = mPresenterCbs.get(PRESENTER_USER_DETAIL);
        if(listener != null){
            listener.onComplete(msg);
        }
    }

    @Override
    public void onGetGoodsInfoDone(Message msg) {

    }

    @Override
    public void onGetGoodsListDone(Message msg) {
        PresenterCallback callback = mPresenterCbs.get(PRESENTER_GOODS_LIST);
        if(callback != null) {
            callback.onComplete(msg);
        }
    }

    @Override
    public void onGetUserFolloweeDone(Message message) {
        PresenterCallback callback = mPresenterCbs.get(PRESENTER_FOLLOWEE);
        if(callback != null) {
            callback.onComplete(message);
        }
    }

    @Override
    public void onGetUserFollowerDone(Message message) {
        PresenterCallback callback = mPresenterCbs.get(PRESENTER_FOLLOWER);
        if(callback != null) {
            callback.onComplete(message);
        }
    }

    @Override
    public void onResetPwdByEmailDone(Message message) {
        PresenterCallback callback = mPresenterCbs.get(PRESENTER_LOGIN);
        if(callback != null){
            callback.onComplete(message);
        }
    }

    @Override
    public void onResetPwdByTelDone(Message message) {
        PresenterCallback callback = mPresenterCbs.get(PRESENTER_LOGIN);
        if(callback != null){
            callback.onComplete(message);
        }
    }


    @Override
    public void publishComplete(Message message) {
        Log.v(TAG,"publishComplete(): goods id = " + message.obj);

        if(message.obj != null) {
            mGoodsInfo.setId((String) message.obj);
        }

        PresenterCallback listener = mPresenterCbs.get(PRESENTER_PUBLISH);
        if(listener != null){
            listener.onComplete(message);
        }
    }

    @Override
    public void registerComplete(Message message) {
        Log.v(TAG,"registerComplete(): message " + message.obj);

        if(message.obj != null){
            mCache.saveToFile((UserInfo)message.obj);
        }

        PresenterCallback listener = mPresenterCbs.get(PRESENTER_REGISTER);
        if(listener != null){
            listener.onComplete(message);
        }
    }

    @Override
    public void updateProcess(int count) {
        Log.v(TAG,"updateProcess(): count = " + count);

        PresenterCallback listener = mPresenterCbs.get(PRESENTER_PUBLISH);
        if(listener != null){
            final Message message = new Message();
            message.obj = count;
            listener.onNext(message);
        }
/*        if(mPresenterCb != null) {
            mPresenterCb.updateUploadProcess(count);
        }*/
    }

    @Override
    public void loginComplete(Message message) {
        UserInfo user;

        if(message.obj != null) {
            user = (UserInfo) message.obj;
            String imgUrl = user.getHeadIconUrl();
            String imgName = URLUtil.guessFileName(imgUrl,null,null);
            String cacheImg;
            if(!mCache.isImageCached(imgName,FileCache.CACHE_TYPE_USER)){
                cacheImg  = mCache.saveImgToFile(imgUrl,FileCache.CACHE_TYPE_USER);
            }else{
                cacheImg = mCache.getImageFilePath(imgName,FileCache.CACHE_TYPE_USER);
            }

            if(!mCache.isCached(user.getUserId(),FileCache.CACHE_TYPE_USER) ||
                    mCache.isExpired(user.getUserId(),FileCache.CACHE_TYPE_USER)) {
                mCache.saveToFile(user);
            }

            Log.v(TAG,"cached image path = " + cacheImg);

            user.setHeadIconUrl(cacheImg);
        }

        PresenterCallback listener = mPresenterCbs.get(PRESENTER_LOGIN);
        if(listener != null){
            listener.onComplete(message);
        }
    }
}
