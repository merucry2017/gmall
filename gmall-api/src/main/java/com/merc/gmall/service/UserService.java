package com.merc.gmall.service;

import com.merc.gmall.bean.Result;
import com.merc.gmall.bean.UmsMember;
import com.merc.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {

    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);

    UmsMember login(UmsMember umsMember);

    void addUserToken(String token, String memberId);

    void deleteUserToken(String memberId);

    UmsMember addOauthUser(UmsMember umsMember);

    UmsMember checkOauthUser(UmsMember umsCheck);

    UmsMember getOauthUser(UmsMember umsMemberCheck);

    UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId);

    Result saveUser(UmsMember user);

    Result saveUmsMemberReceiveAddress(UmsMemberReceiveAddress umsMemberReceiveAddress);

    Result deleteUmsMemberReceiveAddressById(String id);

    Result modifyUmsMemberReceiveAddressById(UmsMemberReceiveAddress umsMemberReceiveAddress);

    UmsMemberReceiveAddress getUmsMemberReceiveAddressById(String id);
}
