package com.merc.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.merc.gmall.bean.Result;
import com.merc.gmall.bean.UmsMember;
import com.merc.gmall.bean.UmsMemberReceiveAddress;
import com.merc.gmall.service.UserService;
import com.merc.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.merc.gmall.user.mapper.UserMapper;
import com.merc.gmall.util.MD5Util;
import com.merc.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMembers = userMapper.selectAll();
        return umsMembers;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
        // 封装的参数对象
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);

        return umsMemberReceiveAddresses;
    }

    @Override
    public UmsMember login(UmsMember umsMember) {
        //若密码为空，返回Null
        if(umsMember.getPassword().equals("")){
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();

            if(jedis!=null){
                String umsMemberStr = jedis.get("user:" + umsMember.getPassword()+umsMember.getUsername() + ":info");

                if (StringUtils.isNotBlank(umsMemberStr)) {
                    // 密码正确
                    UmsMember umsMemberFromCache = JSON.parseObject(umsMemberStr, UmsMember.class);
                    return umsMemberFromCache;
                }
            }
            // 链接redis失败，开启数据库
            UmsMember umsMemberFromDb =loginFromDb(umsMember);
            if(umsMemberFromDb!=null){
                jedis.setex("user:" + umsMember.getPassword()+umsMember.getUsername() + ":info",60*60, JSON.toJSONString(umsMemberFromDb));
            }
            return umsMemberFromDb;
        } finally {
            jedis.close();
        }
    }

    @Override
    public void addUserToken(String token, String memberId) {
        Jedis jedis = redisUtil.getJedis();

        jedis.setex("user:"+memberId+":token",60*60*2,token);
        jedis.close();
    }

    @Override
    public UmsMember addOauthUser(UmsMember umsMember) {
        userMapper.insertSelective(umsMember);

        return umsMember;
    }

    @Override
    public UmsMember checkOauthUser(UmsMember umsCheck) {
        UmsMember umsMember = userMapper.selectOne(umsCheck);
        return umsMember;
    }

    @Override
    public UmsMember getOauthUser(UmsMember umsMemberCheck) {


        UmsMember umsMember = userMapper.selectOne(umsMemberCheck);
        return umsMember;
    }

    @Override
    public UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setId(receiveAddressId);
        UmsMemberReceiveAddress umsMemberReceiveAddress1 = umsMemberReceiveAddressMapper.selectOne(umsMemberReceiveAddress);
        return umsMemberReceiveAddress1;
    }

    @Override
    public Result saveUser(UmsMember user) {
        String username = user.getUsername();
        UmsMember umsMember = userMapper.findByUsername(username);
        Result result = new Result(false);
        if(umsMember==null){
            //设置为普通用户
            user.setMemberLevelId("4");
            //用户昵称
            user.setNickname(username);
            //用户状态
            user.setStatus(1);
            //创建时间
            user.setCreateTime(new Date());
            //执行密码MD5加密
            user.setPassword(MD5Util.digest(user.getPassword()));
            int success = userMapper.insert(user);
            if(success==1){
                result.setState("200");
                result.setSuccess(true);
            }
        }else {
            result.setMessage("用户已存在");
//            throw new RuntimeException("用户已存在");
        }
        return result;
    }

    private UmsMember loginFromDb(UmsMember umsMember) {

        UmsMember member = userMapper.findByUsername(umsMember.getUsername());
        if(member == null) {
            return null;
        }
        String md5Password = MD5Util.digest(umsMember.getPassword());
        if(!member.getPassword().equals(md5Password)) {
            return null;
        }
        return member;

    }
}
