package com.github.xzwj87.mineflea.market.presenter;

import android.os.Message;
import android.util.Log;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.github.xzwj87.mineflea.R;
import com.github.xzwj87.mineflea.app.AppGlobals;
import com.github.xzwj87.mineflea.market.data.ResponseCode;
import com.github.xzwj87.mineflea.market.data.repository.DataRepository;
import com.github.xzwj87.mineflea.market.internal.di.PerActivity;
import com.github.xzwj87.mineflea.market.model.PublishGoodsInfo;
import com.github.xzwj87.mineflea.market.model.UserInfo;
import com.github.xzwj87.mineflea.market.ui.BaseView;
import com.github.xzwj87.mineflea.market.ui.GoodsDetailView;
import com.github.xzwj87.mineflea.utils.UserPrefsUtil;

import javax.inject.Inject;

/**
 * Created by jason on 12/1/16.
 */

@PerActivity
public class GoodsDetailPresenterImpl implements GoodsDetailPresenter{
    private static final String LOG_TAG = "GoodsDetailPresenter";
    private static final String DIST_UNITS = AppGlobals.getAppContext().
            getString(R.string.dist_units);
    @Inject DataRepository mRepo;
    private GoodsDetailView mView;
    private PublishGoodsInfo mGoods;
    private UserInfo mUser; // who published the goods
    // current location
    private LatLng mCurrent;

    @Inject
    public GoodsDetailPresenterImpl(DataRepository repository){
        mRepo = repository;
        mGoods = null;
        mUser = null;
        mCurrent = UserPrefsUtil.getCurrentLocation();
    }

    @Override
    public void getGoodsInfo(String goodsId) {
        Log.v(LOG_TAG,"getGoodsInfo()");
        mRepo.getGoodsInfoById(goodsId);
    }

    @Override
    public void addToFavorites() {
        if(mGoods != null){
            mGoods.addFavorUser(mRepo.getCurrentUserId());
            mRepo.addToMyFavorites(mGoods);
            mView.updateLikes(String.valueOf(mGoods.getStars()));
        }
    }

    @Override
    public String getPublisherId() {
        if(mUser == null) return "";

        return mUser.getUserId();
    }

    @Override
    public void init() {
        mRepo.init();
        mRepo.registerCallBack(PRESENTER_GOODS_DETAIL,
                new GoodsDetailPresenterCallback());
    }

    @Override
    public void onPause() {
        mRepo.unregisterCallback(PRESENTER_GOODS_DETAIL);
    }

    @Override
    public void onDestroy() {
        mGoods = null;
        mUser = null;
    }

    @Override
    public void setView(BaseView view) {
        mView = (GoodsDetailView)view;
    }

    private class GoodsDetailPresenterCallback implements PresenterCallback{

        @Override
        public void onComplete(Message message) {
            Log.v(LOG_TAG,"onComplete()");
            int response = message.what;
            switch (response) {
                case ResponseCode.RESP_GET_GOODS_SUCCESS:
                    mGoods = (PublishGoodsInfo) message.obj;
                    mRepo.getUserInfoById(mGoods.getUserId());
                    mView.onGetGoodsInfoDone(true);
                    renderGoodsView();
                    break;
                case ResponseCode.RESP_GET_GOODS_ERROR:
                    mView.onGetGoodsInfoDone(false);
                    break;
                case ResponseCode.RESP_GET_USER_INFO_SUCCESS:
                    mUser = (UserInfo) message.obj;
                    renderUserView();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNext(Message message) {

        }

        @Override
        public void onError(Throwable e) {

        }
    }

    private void renderGoodsView(){
        Log.v(LOG_TAG,"renderGoodsView()");
        if(mGoods != null){
            mView.updateImageListPage(mGoods.getImageUri());

            String name = mGoods.getName() + "(" + mGoods.getNote() + ")";
            mView.updateGoodsName(name);

            mView.updateTitle(mGoods.getName());

            String curSymbol = AppGlobals.getAppContext().getString(R.string.currency_symbol);
            String p = curSymbol + String.valueOf(mGoods.getPrice());
            mView.updateGoodsPrice(p);

            String detail = mGoods.getLocDetail();
            String distFrom = AppGlobals.getAppContext().getString(R.string.distance_from_you);
            String d = String.valueOf((int)AMapUtils.calculateLineDistance(mGoods.getLocation(),mCurrent));
            String dist = detail + "," + distFrom + d + DIST_UNITS;
            mView.updateGoodsLocation(dist);

            String favoredNo = AppGlobals.getAppContext().getString(R.string.favored_user) +
                    " " + String.valueOf(mGoods.getStars());
            mView.updateLikes(favoredNo);
        }
    }

    private void renderUserView(){
        Log.v(LOG_TAG,"renderUserView()");
        if(mUser != null){
            mView.updateUserInfo(mUser);
        }
    }
}
