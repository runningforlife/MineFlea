package com.github.xzwj87.mineflea.market.data.repository;

import com.github.xzwj87.mineflea.market.model.PublishGoodsInfo;
import com.github.xzwj87.mineflea.market.model.UserInfo;
import com.github.xzwj87.mineflea.market.presenter.BasePresenter;
import com.github.xzwj87.mineflea.market.presenter.PresenterCallback;

import java.util.List;

/**
 * Created by jason on 10/11/16.
 */

public interface BaseRepository {
    /**
     * publish a goods
     */
    void publishGoods(PublishGoodsInfo goods);

    /*
     * register user
     */
    void register(UserInfo userInfo,String authCode);

    /*
     * login
     */
    void login(UserInfo info);

    /*
     * login by SMS auth code
     */
    void loginBySms(String telNumber, String pwd);

    /*
     * register by SMS auth code
     */
    void registerBySms(String telNumber,String authCode);

    /*
     * send SMS auth code
     */
    void sendSmsAuthCode(String number);


    /*
     * reset password by SMS code
     */
    void resetPwdBySms(String authCode,String newPwd);


    /*
     * send SMS or email to reset password
     */
    void getAuthCodeByAccount(String account);

    /*
     * logout
     */
    void logout();

    /*
     * upload image by user id or goods id
     */
    void uploadImageById(String imgUri,boolean isUser,boolean showProcess);

    /*
     * upload image list
     */
    void uploadImages(List<String> imgList, boolean showProgress);

    /*
     * get current user id
     */
    String getCurrentUserId();

    /*
     * get current user information
     */
    UserInfo getCurrentUser();

    /*
     * update current user info
     */
    void updateCurrentUserInfo(String key, String val);

    /*
     * update goods info
     */
    void updateGoodsInfo(String id,String key,List<String> val);

    /*
     * add a goods to my favorite list
     */
    void addToMyFavorites(PublishGoodsInfo goodsInfo);

    /*
     * get user info by id
     */
    void getUserInfoById(String id);

    /*
     * get all goods
     */
    void getAllGoods();

    /*
     * follow a user
     */
    void follow(String userId);


    /*
     * unfollow a user
     */
    void unFollow(String userId);


    /*
     * get goods info by id
     */
    void getGoodsInfoById(String goodsId);

    /*
     * get goods list by user id
     */
    void getGoodsListByUserId(String id);

    /*
     * query favorite goods list by user id
     */
    void queryFavorGoodsListByUserId(String id);

    /*
     * query user follower by id
     */
    void queryUserFollowerListByUserId(String id);

    /*
     * query user followee by id
     */
    void queryUserFolloweeListByUserId(String id);

    /*
     * register presenter callback
     */
    void registerCallBack(@BasePresenter.PRESENTER_TYPE String type,PresenterCallback callback);

    /*
     *  unregister presenter callback
     */
    void unregisterCallback(@BasePresenter.PRESENTER_TYPE String type);
}
