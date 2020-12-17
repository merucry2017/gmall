package com.merc.gmall.passport.controller;

import com.alibaba.fastjson.JSON;
import com.merc.gmall.util.HttpclientUtil;

import java.util.HashMap;
import java.util.Map;

public class TestOauth2 {

    public static String getCode(){

        // 1 获得授权码
        // 2161515525
        // http://passport.gmall.com:8085/vlogin
        //3d43bbbd6f8441d181852057ca0e9b0f

        String s1 = HttpclientUtil.doGet("https://api.weibo.com/oauth2/authorize?client_id=2161515525&response_type=code&redirect_uri=http://passport.gmall.com:8085/vlogin");

        System.out.println(s1);

        // 在第一步和第二部返回回调地址之间,有一个用户操作授权的过程

        // 2 返回授权码到回调地址

        return null;
    }

    public static String getAccess_token(){
        // 换取access_token
        // client_secret=a79777bba04ac70d973ee002d27ed58c
        // client_id=187638711
        String s3 = "https://api.weibo.com/oauth2/access_token?";//?client_id=187638711&client_secret=a79777bba04ac70d973ee002d27ed58c&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8085/vlogin&code=CODE";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id","2161515525");
        paramMap.put("client_secret","d74916ccda6649fcacdb0521c56a3d55");
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri","http://passport.gmall.com:8085/vlogin");
        paramMap.put("code","f25a4073561d32f1d660cf7bc0b550e6");// 授权有效期内可以使用，没新生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期
//        String access_token_json = HttpclientUtil.doPost(s3, paramMap);
        String access_token_json = "";
       Map<String,String> access_map = JSON.parseObject(access_token_json,Map.class);
//
       System.out.println(access_map.get("access_token"));
       System.out.println(access_map.get("uid"));

        return access_map.get("access_token");
    }

    public static Map<String,String> getUser_info(){

        // 4 用access_token查询用户信息
        String s4 = "https://api.weibo.com/2/users/show.json?access_token=2.00Imeq1HdvUR3C6fa6e08c160YsC_S&uid=6721006344";
        String user_json = HttpclientUtil.doGet(s4);
        Map<String,String> user_map = JSON.parseObject(user_json,Map.class);

        System.out.println(user_map.get("1"));

        return user_map;
    }


    public static void main(String[] args) {

        getUser_info();
//        getAccess_token();
    }
}
