package com.merc.gmall.service;

import com.merc.gmall.bean.HatArea;
import com.merc.gmall.bean.HatCity;
import com.merc.gmall.bean.HatProvince;

import java.util.List;

public interface AddressService {

    List<HatProvince> getAllHatProvince();

    List<HatCity> getAllHatCityByFather(String father);

    List<HatArea> getAllHatAreaByFather(String father);
}
