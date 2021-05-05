package com.merc.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.merc.gmall.annotations.LoginRequired;
import com.merc.gmall.bean.Result;
import com.merc.gmall.bean.UmsMember;
import com.merc.gmall.service.UserService;
import com.merc.gmall.util.CookieUtil;
import com.merc.gmall.util.HttpclientUtil;
import com.merc.gmall.util.JwtUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {


    @Reference
    UserService userService;

    @ApiOperation(value = "返回首页",notes = "author:hxq")
    @GetMapping("/")
    @LoginRequired(loginSuccess = false)
    public void getIndex(HttpServletResponse resp) throws IOException {
        resp.sendRedirect("http://localhost:8083/index");
    }

    @RequestMapping("vlogin")
    public String vlogin(String code, HttpServletRequest request, HttpServletResponse response){

        // 授权码换取access_token
        // 换取access_token
        // client_secret=f043fe09dcab7e9b90cdd7491e282a8f
        // client_id=2173054083
        String s3 = "https://api.weibo.com/oauth2/access_token?";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id","3139581877");
        paramMap.put("client_secret","81625e745d23bddb18fb471603a1236c");
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri","http://127.0.0.1:8085/vlogin");
        // 授权有效期内可以使用，没新生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期
        paramMap.put("code",code);
        String access_token_json = HttpclientUtil.doPost(s3, paramMap);

        Map<String,Object> access_map = JSON.parseObject(access_token_json,Map.class);

        // access_token换取用户信息
        String uid = (String)access_map.get("uid");
        String access_token = (String)access_map.get("access_token");
        String show_user_url = "https://api.weibo.com/2/users/show.json?access_token="+access_token+"&uid="+uid;
        String user_json = HttpclientUtil.doGet(show_user_url);
        Map<String,Object> user_map = JSON.parseObject(user_json,Map.class);

        // 将用户信息保存数据库，用户类型设置为微博用户
        UmsMember umsMember = new UmsMember();
        umsMember.setSourceType("2");
        umsMember.setAccessCode(code);
        umsMember.setAccessToken(access_token);
        umsMember.setSourceUid((String)user_map.get("idstr"));
        umsMember.setCity((String)user_map.get("location"));
        umsMember.setNickname((String)user_map.get("screen_name"));

        String g = "0";
        String gender = (String)user_map.get("gender");
        if(gender.equals("m")){
            g = "1";
        }
        umsMember.setGender(g);

        UmsMember umsCheck = new UmsMember();
        umsCheck.setSourceUid(umsMember.getSourceUid());
        // 检查该用户(社交用户)以前是否登陆过系统
        UmsMember umsMemberCheck = userService.checkOauthUser(umsCheck);

        if(umsMemberCheck==null){
            umsMember.setCreateTime(new Date());
            umsMember.setStatus(1);
            umsMember = userService.addOauthUser(umsMember);
        }else{
            umsMember = umsMemberCheck;
        }

        // 生成jwt的token，并且重定向到首页，携带该token
        String token = null;
        // rpc的主键返回策略失效
        String memberId = umsMember.getId();
        String nickname = umsMember.getNickname();
        Map<String,Object> userMap = new HashMap<>();
        // 是保存数据库后主键返回策略生成的id
        userMap.put("memberId",memberId);
        userMap.put("nickname",nickname);

        // 通过nginx转发的客户端ip
        String ip = request.getHeader("x-forwarded-for");
        if(StringUtils.isBlank(ip)){
            ip = request.getRemoteAddr();// 从request中获取ip
            if(StringUtils.isBlank(ip)){
                ip = "127.0.0.1";
            }
        }

        // 按照设计的算法对参数进行加密后，生成token
        token = JwtUtil.encode("2019gmall0105", userMap, ip);

        // 将token存入redis一份
        userService.addUserToken(token,memberId);


        return "redirect:http://search.gmall.com:8083/index?token="+token+"&nickname=aaa";
    }


    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token,String currentIp,HttpServletRequest request){

        // 通过jwt校验token真假
        Map<String,String> map = new HashMap<>();

        Map<String, Object> decode = JwtUtil.decode(token, "2019gmall0105", currentIp);

        if(decode!=null){
            map.put("status","success");
            map.put("memberId",(String)decode.get("memberId"));
            map.put("nickname",(String)decode.get("nickname"));
        }else{
            map.put("status","fail");
        }


        return JSON.toJSONString(map);
    }


    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request, HttpServletResponse response) {

        String token = "";

        // 调用用户服务验证用户名和密码
        UmsMember umsMemberLogin = userService.login(umsMember);

        if(umsMemberLogin!=null){
            // 登录成功

            // 用jwt制作token
            String memberId = umsMemberLogin.getId();
            String nickname = umsMemberLogin.getNickname();
            Map<String,Object> userMap = new HashMap<>();
            userMap.put("memberId",memberId);
            userMap.put("nickname",nickname);
            request.setAttribute("memberId",memberId);
            request.setAttribute("nickname",nickname);

            String ip = request.getHeader("x-forwarded-for");// 通过nginx转发的客户端ip
            if(StringUtils.isBlank(ip)){
                ip = request.getRemoteAddr();// 从request中获取ip
                if(StringUtils.isBlank(ip)){
                    ip = "127.0.0.1";
                }
            }

            // 按照设计的算法对参数进行加密后，生成token
            token = JwtUtil.encode("2019gmall0105", userMap, ip);
            // 将token存入redis一份
            userService.addUserToken(token,memberId);
            String result = token + "&nickname=" + nickname;
            return result;
        }else{
            // 登录失败
            token = "fail";
            return token;
        }
    }

    @RequestMapping("index")
    public String index(String ReturnUrl, ModelMap map){

        map.put("ReturnUrl",ReturnUrl);

        return "index";
    }

    @RequestMapping("go_register")
    public String go_register(String ReturnUrl, ModelMap map){

        map.put("ReturnUrl",ReturnUrl);
        return "register";
    }

    @RequestMapping("do_register")
    @ResponseBody
    public Result do_register(String username, String password){
        UmsMember user = new UmsMember(username, password);
        Result result = userService.saveUser(user);
        return result;
    }
}
