package com.merc.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.merc.gmall.bean.UmsMember;
import com.merc.gmall.bean.UmsMemberReceiveAddress;
import com.merc.gmall.service.UserService;
import com.merc.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.merc.gmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

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
}
