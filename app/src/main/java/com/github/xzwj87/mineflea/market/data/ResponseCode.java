package com.github.xzwj87.mineflea.market.data;

/**
 * Created by jason on 9/28/16.
 */

public class ResponseCode {

    public static final int RESP_SUCCESS = 0x00;

    public static final int RESP_NETWORK_ERROR = 0x10;

    public static final int RESP_NETWORK_NOT_CONNECTED = 0x20;

    public static final int RESP_DATABASE_SQL_ERROR = 0x30;

    public static final int RESP_AV_SAVED_FAILURE = 0x40;

    public static final int RESP_REGISTER_SUCCESS = 0x50;

    public static final int RESP_REGISTER_FAIL = 0x60;

    public static final int RESP_LOGIN_SUCCESS = 0x70;

    public static final int RESP_LOGIN_FAIL = 0x71;

    public static final int RESP_LOGIN_INVALID_EMAIL = 0x72;

    public static final int RESP_LOGIN_INVALID_PHONE_NUMBER = 0x73;

    public static final int RESP_LOGIN_INVALID_PASSWORD = 0x74;

    public static final int RESP_IMAGE_UPLOAD_SUCCESS = 0x80;

    public static final int RESP_IMAGE_UPLOAD_ERROR = 0x81;

    public static final int RESP_FILE_NOT_FOUND = 0x90;

    public static final int RESP_GET_USER_INFO_SUCCESS = 0x100;

    public static final int RESP_GET_USER_INFO_ERROR = 0x101;

    public static final int RESP_GET_GOODS_LIST_SUCCESS = 0x200;

    public static final int RESP_GET_GOODS_LIST_ERROR = 0x201;

    public static final int RESP_GET_ALL_GOODS_CACHE_SUCCESS = 0x202;

    public static final int RESP_GET_ALL_GOODS_CACHE_FAIL = 0x203;

    public static final int RESP_GET_GOODS_SUCCESS = 0x210;

    public static final int RESP_GET_GOODS_ERROR = 0x211;

    public static final int RESP_PUBLISH_GOODS_SUCCESS = 0x220;

    public static final int RESP_PUBLISH_GOODS_ERROR = 0x221;

    public static final int RESP_QUERY_FAVORITE_GOODS_LIST_SUCCESS = 0x302;

    public static final int RESP_QUERY_FAVORITE_GOODS_LIST_ERROR = 0x303;

    public static final int RESP_QUERY_FOLLOWEES_SUCCESS = 0x304;

    public static final int RESP_QUERY_FOLLOWEES_ERROR = 0x305;

    public static final int RESP_QUERY_FOLLOWERS_SUCESS = 0x306;

    public static final int RESP_QUERY_FOLLOWERS_ERROR = 0x307;

    public static final int RESP_RESET_PWD_BY_EMAIL_SUCCESS = 0x400;

    public static final int RESP_RESET_PWD_BY_EMAIL_FAIL = 0x401;

    public static final int RESP_RESET_PWD_BY_SMS_SUCCESS = 0x402;

    public static final int RESP_RESET_PWD_BY_SMS_FAIL = 0x403;

    public static final int RESP_SEND_SMS_CODE_SUCCESS = 0x404;

    public static final int RESP_SEND_SMS_CODE_FAIL = 0x405;

    public static final int RESP_SEND_EMAIL_SUCCESS = 0x406;

    public static final int RESP_SEND_EMAIL_FAIL = 0x407;

    public static final int RESP_PHONE_NUMBER_VERIFIED_SUCCESS = 0x501;

    public static final int RESP_PHONE_NUMBER_VERIFIED_ERROR = 0x502;


    @Override
    public String toString(){
        return ResponseCode.class.getSimpleName();
    }

    public String codeToString(int code){
        switch (code){

        }

        return null;
    }
}
