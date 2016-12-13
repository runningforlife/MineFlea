package com.github.xzwj87.mineflea.market.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.model.LatLng;
import com.github.xzwj87.mineflea.R;
import com.github.xzwj87.mineflea.market.model.PublishGoodsInfo;
import com.github.xzwj87.mineflea.market.presenter.DiscoverGoodsPresenterImpl;
import com.github.xzwj87.mineflea.market.ui.DiscoverGoodsView;
import com.github.xzwj87.mineflea.market.ui.activity.GoodsDetailActivity;
import com.github.xzwj87.mineflea.market.ui.activity.HomeActivity;
import com.github.xzwj87.mineflea.market.ui.activity.PublishGoodsActivity;
import com.github.xzwj87.mineflea.market.ui.adapter.DiscoverGoodsAdapter;
import com.github.xzwj87.mineflea.utils.NetConnectionUtils;
import com.github.xzwj87.mineflea.utils.SharePrefsHelper;
import com.github.xzwj87.mineflea.utils.UserPrefsUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.github.xzwj87.mineflea.market.ui.activity.HomeActivity.REQUEST_PUBLISH;

/**
 * Created by jason on 10/9/16.
 */

public class DiscoverTabFragment extends BaseFragment
            implements DiscoverGoodsAdapter.DiscoverGoodsCallback,DiscoverGoodsView {
    public static final String TAG = DiscoverTabFragment.class.getSimpleName();

    private DiscoverGoodsAdapter mRvAdapter;
    @Inject DiscoverGoodsPresenterImpl mPresenter;

    @BindView(R.id.discover_recycler_view)
    RecyclerView mRvDiscover;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSrlDiscover;

    private AMapLocationClient mLocClient;

    public DiscoverTabFragment() {
    }

    public static DiscoverTabFragment newInstance() {
        DiscoverTabFragment fragment = new DiscoverTabFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedSate) {
        View root = inflater.inflate(R.layout.fragment_discover_tab, container, false);
        ButterKnife.bind(this, root);

        init();

        return root;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        Log.v(TAG,"onAttach()");
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.v(TAG,"onResume()");

        startLocating();
    }

    @Override
    public void onPause(){
        super.onPause();

        mLocClient.stopLocation();

        mPresenter.onPause();

        if(mSrlDiscover.isRefreshing()){
            mSrlDiscover.setRefreshing(false);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        mPresenter.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_mine_flea, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_publish:
                startPublishActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info){
        super.onCreateContextMenu(menu,v,info);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){

        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int request,int result, Intent data){
        Log.v(TAG,"onActivityResult(): result = " + result);

    }

    private void init() {
        mRvAdapter = new DiscoverGoodsAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvDiscover.setLayoutManager(layoutManager);
        setSwipeLayout();

        mRvAdapter.setCallback(this);
        mRvDiscover.setAdapter(mRvAdapter);
        // current location
        LatLng loc = UserPrefsUtil.getCurrentLocation();
        if(loc != null) {
            mRvAdapter.setCurrentLoc(loc);
        }

        mPresenter.setView(this);
        mPresenter.init();
    }

    //设置下拉刷新
    private void setSwipeLayout() {
        String themeColor = SharePrefsHelper.getInstance(getContext())
                .getThemeColor();
        mSrlDiscover.setColorSchemeColors(Color.parseColor(themeColor));
        mSrlDiscover.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getGoodsList();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
        String id = mPresenter.getItemAtPos(position).getId();
        intent.putExtra(PublishGoodsInfo.GOODS_ID,id);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View item,int pos) {

    }

    @Override
    public PublishGoodsInfo getItemAtPos(int pos) {
        return mPresenter.getItemAtPos(pos);
    }

    @Override
    public int getItemCount() {
        return mPresenter.getItemCount();
    }

    @Override
    public String getPublisherNickName(int pos) {
        return mPresenter.getPublisherNickName(pos);
    }

    @Override
    public void addToMyFavor(int pos) {
        Log.v(TAG,"addToMyFavor()");
        mPresenter.addToMyFavor(pos);
    }

    @Override
    public void onGetGoodsListDone(boolean success) {
        Log.v(TAG,"onGetGoodsListDone(): " + (success ? "success" : "fail"));
        mRvAdapter.notifyDataSetChanged();

        //mSrlDiscover.setEnabled(false);
        if(mSrlDiscover.isRefreshing()){
            mSrlDiscover.setRefreshing(false);

            if(success){
                showToast(getString(R.string.get_goods_list_success));
            }else{
                showToast(getString(R.string.error_get_goods_list));
            }
        }
    }

    @Override
    public void updateLikesView(int pos, int likes) {
        Log.v(TAG,"updateLikesView()");
        mRvAdapter.updateLikesView(pos,likes);
    }

    @Override
    public void finishView() {
        // empty
    }

    private void startLocating(){
        Log.v(TAG,"startLocating()");
        mLocClient = new AMapLocationClient(getActivity());
        AMapLocationClientOption locOptions = new AMapLocationClientOption();
        locOptions.setNeedAddress(true);
        locOptions.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locOptions.setHttpTimeOut(10*1000);
        locOptions.setInterval(50*1000);
        mLocClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if(aMapLocation != null){
                    if(aMapLocation.getErrorCode() == 0){
                        if(mRvAdapter != null){
                            mRvAdapter.setCurrentLoc(new LatLng(aMapLocation.getLatitude(),
                                    aMapLocation.getLongitude()));
                            // save current location
                            LatLng loc = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                            UserPrefsUtil.updateCurrentLocation(loc);
                        }
                    }else{
                        Log.e("AmapError","location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        });

        mLocClient.startLocation();
    }

    private void startPublishActivity(){
        if(!UserPrefsUtil.isLogin()){
            showToast(getString(R.string.need_to_login));
            return;
        }

        if(NetConnectionUtils.isNetworkConnected()) {
            Intent intent = new Intent(getContext(), PublishGoodsActivity.class);
            startActivityForResult(intent,REQUEST_PUBLISH);
        }else{
            showToast(getString(R.string.hint_no_network_connection));
        }
    }
}
