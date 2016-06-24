package com.rayweather.app.util;

import android.text.TextUtils;

import com.rayweather.app.db.RayWeatherDB;
import com.rayweather.app.model.City;
import com.rayweather.app.model.County;
import com.rayweather.app.model.Province;

public class Utility {
	/*
	 * �����ʹ�����������ص�ʡ������
	 */
	public synchronized static boolean handleProvincesResponse(RayWeatherDB rayWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvines = response.split(",");
			if(allProvines !=null&&allProvines.length>0){
				for(String p : allProvines){
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//���������������ݴ洢��Province��
					rayWeatherDB.saveProvince(province);
				}
			}return true;
		}
		return false;
	}
	public  static boolean handleCitiesResponse(RayWeatherDB rayWeatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			if(allCities !=null&&allCities.length>0){
				for(String c : allCities){
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//���������������ݴ洢��City��
					rayWeatherDB.saveCity(city);
				}
			}return true;
		}
		return false;
	}
	
	public  static boolean handleCountiesResponse(RayWeatherDB rayWeatherDB,String response,int cityID){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			if(allCounties !=null&&allCounties.length>0){
				for(String c : allCounties){
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityID(cityID);
					//���������������ݴ洢��County��
					rayWeatherDB.saveCounty(county);
				}
			}return true;
		}
		return false;
	}
}
