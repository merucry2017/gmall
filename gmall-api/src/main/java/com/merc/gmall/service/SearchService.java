package com.merc.gmall.service;

import com.merc.gmall.bean.PmsSearchParam;
import com.merc.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

public interface SearchService {
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
