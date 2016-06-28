package com.rayweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.rayweather.app.model.City;
import com.rayweather.app.model.County;
import com.rayweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RayWeatherDB {
	/**
	 * ���ݿ���
	 */
	public static final String DB_NAME = "ray_weather";

	/**
	 * ���ݿ�汾
	 */
	public static final int VERSION = 1;
	private static RayWeatherDB rayWeatherDB;
	private  SQLiteDatabase db;

	/**
	 * �����췽��˽�л�
	 */
	private RayWeatherDB(Context context) {
		RayWeatherOpenHelper dbHelper = new RayWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * ��ȡRayWeatherDBʵ��
	 */

	public synchronized static RayWeatherDB getInstance(Context context) {
		if (rayWeatherDB == null) {
			rayWeatherDB = new RayWeatherDB(context);
			Log.d("RayWeatherDB", "��ȡRayWeaetherDBʵ��");
		}

		return rayWeatherDB;
	}

	/**
	 * ��Provinceʵ���洢�����ݿ�
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
			Log.d("RayWeatherDB", "�ѽ�Provinceʵ�����浽���ݿ�");
		}
	}

	/**
	 * �����ݿ��ȡȫ������ʡ����Ϣ
	 */
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				list.add(province);
				Log.d("RayWeatherDB", "�����ݿ��ȡȫ��ʡ����Ϣ");
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}

	/**
	 * ��Cityʵ�����浽���ݿ�
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
			Log.d("RayWeatherDB", "�ѽ�Cityʵ�����浽���ݿ�");
		}
	}

	/**
	 * �����ݶ�ȡĳʡ�����г�����Ϣ
	 */
	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
				Log.d("RayWeatherDB", "�����ݿ��ȡȫ��������Ϣ");
			} while (cursor.moveToNext());
		}
		return list;
	}

	/**
	 * ��Countyʵ���洢�����ݿ�
	 */
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityID());
			db.insert("County", null, values);
			Log.d("RayWeatherDB", "�ѽ�Countyʵ�����浽���ݿ�");
		}
	}

	/**
	 * �����ݿ��ȡĳ���������е�����Ϣ
	 */
	public List<County> loadCounties(int cityID) {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?",
				new String[] { String.valueOf(cityID) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				county.setCityID(cityID);
				list.add(county);
				Log.d("RayWeatherDB", "�����ݿ��ȡȫ������Ϣ");
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}
}
