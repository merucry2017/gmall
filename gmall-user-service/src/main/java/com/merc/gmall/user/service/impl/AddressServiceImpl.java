package com.merc.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.merc.gmall.bean.HatArea;
import com.merc.gmall.bean.HatCity;
import com.merc.gmall.bean.HatProvince;
import com.merc.gmall.service.AddressService;
import com.merc.gmall.user.mapper.AddressAreaMapper;
import com.merc.gmall.user.mapper.AddressCityMapper;
import com.merc.gmall.user.mapper.AddressProvinceMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressProvinceMapper addressProvinceMapper;

    @Autowired
    AddressCityMapper addressCityMapper;

    @Autowired
    AddressAreaMapper addressAreaMapper;

    // 获取所有省份
    @Override
    public List<HatProvince> getAllHatProvince() {
        List<HatProvince> hatProvinces = addressProvinceMapper.selectAll();
        return hatProvinces;
    }

    // 根据省份获取城市
    @Override
    public List<HatCity> getAllHatCityByFather(String father) {
        HatCity hatCity = new HatCity();
        hatCity.setFather(father);
        List<HatCity> hatCities = addressCityMapper.select(hatCity);
        return hatCities;
    }

    // 根据城市获取市区
    @Override
    public List<HatArea> getAllHatAreaByFather(String father) {
        HatArea hatArea = new HatArea();
        hatArea.setFather(father);
        List<HatArea> hatAreas = addressAreaMapper.select(hatArea);
        return hatAreas;
    }
}
