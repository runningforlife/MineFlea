package com.github.xzwj87.mineflea.market.presenter;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.github.xzwj87.mineflea.market.data.repository.MineFleaRepository;
import com.github.xzwj87.mineflea.market.internal.di.PerActivity;
import com.github.xzwj87.mineflea.market.model.UserInfo;
import com.github.xzwj87.mineflea.market.ui.BaseView;
import com.github.xzwj87.mineflea.market.ui.RegisterView;
import com.github.xzwj87.mineflea.utils.UserInfoUtils;
import com.github.xzwj87.mineflea.utils.UserPrefsUtil;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by jason on 10/14/16.
 */

@PerActivity
public class RegisterPresenterImpl extends RegisterPresenter{
    public static final String TAG = RegisterPresenterImpl.class.getSimpleName();

    private MineFleaRepository mRepository;
    private RegisterView mView;
    private UserInfo mUserInfo;

    @Inject
    public RegisterPresenterImpl(@Named("dataRepository") MineFleaRepository repository){
        mRepository = repository;
    }

    @Override
    public void setView(BaseView view) {
        mView = (RegisterView)view;
    }

    @Override
    public void register() {
        Log.v(TAG,"register()");
        if(!TextUtils.isEmpty(mUserInfo.getHeadIconUrl())) {
            mRepository.uploadImage(mUserInfo.getHeadIconUrl(), false);
        }else{
            mRepository.register(mUserInfo);
        }
    }

    @Override
    public void onImgUploadComplete(Message message) {
        Log.v(TAG,"onImgUploadComplete(): icon id = " + message.obj);
        if(message.obj != null) {
            mUserInfo.setHeadIconUrl((String)message.obj);
        }else{
            mUserInfo.setHeadIconUrl("");
        }

        mRepository.register(mUserInfo);
    }

    @Override
    public void setUserNickName(String name) {
        mUserInfo.setNickName(name);
    }

    @Override
    public void setUserEmail(String email) {
        // just keep user name/email the same
        mUserInfo.setUserName(email);
        mUserInfo.setUerEmail(email);
    }

    @Override
    public void setUserTel(String tel) {
        mUserInfo.setUserTelNumber(tel);
    }

    @Override
    public void setUserPwd(String pwd) {
        mUserInfo.setUserPwd(pwd);
    }

    @Override
    public void setUserIconUrl(String url) {
        mUserInfo.setHeadIconUrl(url);
    }

    @Override
    public boolean validUserInfo() {

        if(!UserInfoUtils.isNameValid(mUserInfo.getUserName())){
            mView.showNameInvalidMsg();
            return false;
        }

        if(!UserInfoUtils.isEmailValid(mUserInfo.getUserEmail())){
            mView.showEmailInvalidMsg();
            return false;
        }

        if(!UserInfoUtils.isTelNumberValid(mUserInfo.getUserTelNumber())){
            mView.showTelInvalidMsg();
            return false;
        }

        if(!UserInfoUtils.isPasswordValid(mUserInfo.getUserPwd())){
            mView.showPwdInvalidMsg();
            return false;
        }

        if(TextUtils.isEmpty(mUserInfo.getHeadIconUrl())){
            mView.showHeadIconNullDialog();
        }

        return true;
    }

    @Override
    public String getUserIconUrl() {
        return mUserInfo.getHeadIconUrl();
    }

    @Override
    public void init() {
        mUserInfo = new UserInfo();
        mRepository.init();
        mRepository.setPresenterCallback(this);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onRegisterComplete(Message message) {
        Log.v(TAG,"onRegisterComplete(): user id " + (String)message.obj);

        if(message.obj != null){
            mView.onRegisterComplete(true);
            mUserInfo.setUserId((String)message.obj);
            mUserInfo.setLoginState(true);
        }else {
            mView.onRegisterComplete(false);
            mUserInfo.setLoginState(false);
        }

        saveUserInfo();
        mView.finishView();
    }

    private void saveUserInfo(){
        UserPrefsUtil.saveUserLoginInfo(mUserInfo);
    }
}
