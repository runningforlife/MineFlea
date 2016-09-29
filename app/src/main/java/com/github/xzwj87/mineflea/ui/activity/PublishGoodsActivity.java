package com.github.xzwj87.mineflea.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import com.github.xzwj87.mineflea.R;
import com.github.xzwj87.mineflea.model.GoodsModel;
import com.github.xzwj87.mineflea.presenter.PublishGoodsPresenter;
import com.github.xzwj87.mineflea.presenter.PublishGoodsPresenterImpl;
import com.github.xzwj87.mineflea.ui.PublishGoodsView;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jason on 9/27/16.
 */

public class PublishGoodsActivity extends AppCompatActivity implements PublishGoodsView{
    public static final String TAG = PublishGoodsActivity.class.getSimpleName();

    @BindView(R.id.goods_icon) ImageButton mIbGoodsIcon;
    @BindView(R.id.et_goods_name) EditText mEtGoodsName;
    @BindView(R.id.et_goods_high_price) EditText mEtHighPrice;
    @BindView(R.id.et_goods_low_price) EditText mEtLowPrice;
    @BindView(R.id.et_depreciation_rate) EditText mEtDepRate;
    @BindView(R.id.et_note) EditText mEtNote;

    private GoodsModel mGoods;
    private PublishGoodsPresenter mPresenter;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);

        setContentView(R.layout.activity_publish_goods);

        init();
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onPause(){
        super.onPause();

        mPresenter.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        mPresenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_publish_goods,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        switch (id){
            case R.id.action_ok:
                getGoodsData();

                publishGoods();

                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @OnClick(R.id.ib_goods_icon)
    private void uploadGoodsIcon(){
        Log.v(TAG,"uploadGoodsIcon()");

    }

    @Override
    public void publishGoods(){
        Log.v(TAG,"publishGoods()");

    }

    private void getGoodsData(){
        mGoods.setName(mEtGoodsName.getText().toString());

        mGoods.setDepreciationRate(Double.parseDouble(mEtDepRate.getText().toString()));

        mGoods.setHighPrice(Double.parseDouble(mEtHighPrice.getText().toString()));

        mGoods.setLowerPrice(Double.parseDouble(mEtLowPrice.getText().toString()));

        mGoods.setReleasedDate(new Date());
    }

    private void init(){
        mPresenter = new PublishGoodsPresenterImpl(this);
    }
}
