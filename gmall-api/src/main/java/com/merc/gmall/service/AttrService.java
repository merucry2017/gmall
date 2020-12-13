package com.merc.gmall.service;

import com.merc.gmall.bean.PmsBaseAttrInfo;
import com.merc.gmall.bean.PmsBaseAttrValue;
import com.merc.gmall.bean.PmsBaseSaleAttr;

import java.util.List;
import java.util.Set;


public interface AttrService {

    List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);

    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseSaleAttr> baseSaleAttrList();

    List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> valueIdSet);

    String deleteAttrInfoById(String attrId);

    String deleteAttrValueById(String attrId);
}
