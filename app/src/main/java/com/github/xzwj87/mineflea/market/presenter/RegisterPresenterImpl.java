package com.github.xzwj87.mineflea.market.presenter;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.github.xzwj87.mineflea.app.AppGlobals;
import com.github.xzwj87.mineflea.market.data.ResponseCode;
import com.github.xzwj87.mineflea.market.data.repository.DataRepository;
import com.github.xzwj87.mineflea.market.internal.di.PerActivity;
import com.github.xzwj87.mineflea.market.model.UserInfo;
import com.github.xzwj87.mineflea.market.ui.BaseView;
import com.github.xzwj87.mineflea.market.ui.RegisterView;
import com.github.xzwj87.mineflea.utils.SharePrefsHelper;
import com.github.xzwj87.mineflea.utils.UserInfoUtils;
import com.github.xzwj87.mineflea.utils.UserPrefsUtil;

import javax.inject.Inject;

/**
 * Created by jason on 10/14/16.
 */

@PerActivity
public class RegisterPresenterImpl implements RegisterPresenter{
    public static final String TAG = RegisterPresenterImpl.class.getSimpleName();

    @Inject
    DataRepository mRepository;
    private RegisterView mView;
    private UserInfo mUserInfo;
    // save a temp phone number value
    private String mPhoneNumber;
    private String mAuthCode;

    @Inject
    public RegisterPresenterImpl(DataRepository repository){
        mRepository = repository;
    }

    @Override
    public void setView(BaseView view) {
        mView = (RegisterView)view;
    }

    @Override
    public void setUserNickName(String name) {
        mUserInfo.setNickName(name);
    }

    @Override
    public String getUserNickName() {
        return mUserInfo.getNickName();
    }

    @Override
    public void setUserEmail(String email) {
        // just keep user name/email the same
        mUserInfo.setUserName(email);
        mUserInfo.setUerEmail(email);
    }

    @Override
    public String getUserEmail() {
        return mUserInfo.getUserEmail();
    }

    @Override
    public void setUserPwd(String pwd) {
        mUserInfo.setUserPwd(pwd);
    }

    @Override
    public String getUserPwd() {
        return mUserInfo.getUserPwd();
    }

    @Override
    public void setUserIconUrl(String url) {
        Log.v(TAG,"setUserIconUrl(): " + url);
        mUserInfo.setHeadIconUrl(url);
    }

    @Override
    public void setSmsAuthCode(String authCode) {
        if(!TextUtils.isEmpty(authCode)) {
            mAuthCode = authCode;
        }
    }

    @Override
    public String getTelNumber() {
        return mPhoneNumber;
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

        if(!UserInfoUtils.isPasswordValid(mUserInfo.getUserPwd())){
            mView.showPwdInvalidMsg();
            return false;
        }

        if(TextUtils.isEmpty(mUserInfo.getHeadIconUrl())){
            mView.showHeadIconNullMsg();
        }

        return true;
    }

    @Override
    public String getUserIconUrl() {
        return mUserInfo.getHeadIconUrl();
    }

    @Override
    public void getSmsAuthCode(String telNumber) {
        Log.v(TAG,"getSmsAuthCode()");
        if(UserInfoUtils.isTelNumberValid(telNumber)) {
            mPhoneNumber = telNumber;
            mUserInfo.setUserTelNumber(mPhoneNumber);
            mRepository.sendSmsAuthCode(telNumber);
        }else{
            mView.showTelInvalidMsg();
        }
    }

    @Override
    public void signUpBySms() {
        Log.v(TAG,"signUpBySms()");
        if(!TextUtils.isEmpty(mAuthCode)) {

            //mRepository.registerBySms(mPhoneNumber, mAuthCode);
            return;
        }
        throw new IllegalArgumentException("auth code should not be empty");
    }

    @Override
    public void registerAndVerify() {
        Log.v(TAG,"registerAndVerify()");
        if(mUserInfo != null) {
/*            mRepository.updateCurrentUserInfo(UserInfo.USER_NICK_NAME, mUserInfo.getNickName());
            mRepository.updateCurrentUserInfo(UserInfo.USER_NAME, mUserInfo.getUserName());
            mRepository.updateCurrentUserInfo(UserInfo.UER_EMAIL, mUserInfo.getUserEmail());
            mRepository.updateCurrentUserInfo(UserInfo.USER_PWD, mUserInfo.getUserPwd());
            */
            // upload head icon
            mRepository.uploadImageById(mUserInfo.getHeadIconUrl(), true, false);
            mRepository.register(mUserInfo,mAuthCode);
            mView.showProgress(true);
        }
    }

    @Override
    public void init() {
        mUserInfo = new UserInfo();

        mRepository.init();
        //mRepository.setPresenterCallback(this);
        mRepository.registerCallBack(PRESENTER_REGISTER,new RegisterPresenterCallback());
    }

    @Override
    public void onPause() {
        //sIsUserDataSaved = false;
    }

    @Override
    public void onDestroy() {
        mUserInfo = null;
        mRepository.unregisterCallback(PRESENTER_REGISTER);
    }

    private void saveUserInfo(){
        UserPrefsUtil.saveUserLoginInfo(mUserInfo);
    }

    private class RegisterPresenterCallback implements PresenterCallback {

        @Override
        public void onComplete(Message message) {
            Log.v(TAG,"onComplete(): id = " + message.obj);

            int response = message.what;
            switch (response) {
                case ResponseCode.RESP_REGISTER_SUCCESS:
                    if (message.obj != null) {
                        mUserInfo.setUserId(((UserInfo) message.obj).getUserId());
                        // now we are sign up
                        SharePrefsHelper.getInstance(AppGlobals.getAppContext())
                                .updateLogState(true);
                    }
                    mView.onRegisterComplete(true);
                    saveUserInfo();
                    mView.showProgress(false);
                    break;
                case ResponseCode.RESP_REGISTER_FAIL:
                    mView.onRegisterComplete(false);
                    mView.showProgress(false);
                    break;
                case ResponseCode.RESP_IMAGE_UPLOAD_SUCCESS:
                    mRepository.updateCurrentUserInfo(UserInfo.USER_HEAD_ICON,(String)message.obj);
                    mView.onRegisterComplete(true);
                    mView.showProgress(false);
                    break;
                case ResponseCode.RESP_IMAGE_UPLOAD_ERROR:
                    mView.onRegisterComplete(true);
                    mView.showProgress(false);
                    // update icon URL
                    break;
                case ResponseCode.RESP_PHONE_NUMBER_VERIFIED_ERROR:
                    mView.showProgress(false);
                    break;
                case ResponseCode.RESP_NETWORK_NOT_CONNECTED:
                    mView.showNoNetConnectionMsg();
                    break;
                default:
                    break;
            }

            //mView.finishView();
        }

        @Override
        public void onNext(Message message) {
            Log.v(TAG,"onNext(): event = " + message.obj);
            if(message.obj != null) {
                mUserInfo.setHeadIconUrl((String)message.obj);
            }else{
                mUserInfo.setHeadIconUrl("");
            }

            mRepository.updateCurrentUserInfo(UserInfo.USER_HEAD_ICON,mUserInfo.getHeadIconUrl());
        }

        @Override
        public void onError(Throwable e) {

        }
    }
}
