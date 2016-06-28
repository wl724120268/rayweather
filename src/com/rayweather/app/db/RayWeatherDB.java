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
	 * 数据库名
	 */
	public static final String DB_NAME = "ray_weather";

	/**
	 * 数据库版本
	 */
	public static final int VERSION = 1;
	private static RayWeatherDB rayWeatherDB;
	private  SQLiteDatabase db;

	/**
	 * 将构造方法私有化
	 */
	private RayWeatherDB(Context context) {
		RayWeatherOpenHelper dbHelper = new RayWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * 获取RayWeatherDB实例
	 */

	public synchronized static RayWeatherDB getInstance(Context context) {
		if (rayWeatherDB == null) {
			rayWeatherDB = new RayWeatherDB(context);
			Log.d("RayWeatherDB", "获取RayWeaetherDB实例");
		}

		return rayWeatherDB;
	}

	/**
	 * 将Province实例存储到数据库
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
			Log.d("RayWeatherDB", "已将Province实例储存到数据库");
		}
	}

	/**
	 * 从数据库读取全国所有省份信息
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
				Log.d("RayWeatherDB", "从数据库读取全国省份信息");
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}

	/**
	 * 将City实例储存到数据库
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
			Log.d("RayWeatherDB", "已将City实例储存到数据库");
		}
	}

	/**
	 * 从数据读取某省下所有城市信息
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
				Log.d("RayWeatherDB", "从数据库读取全国市区信息");
			} while (cursor.moveToNext());
		}
		return list;
	}

	/**
	 * 将County实例存储到数据库
	 */
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityID());
			db.insert("County", null, values);
			Log.d("RayWeatherDB", "已将County实例储存到数据库");
		}
	}

	/**
	 * 从数据库读取某城市下所有的县信息
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
				Log.d("RayWeatherDB", "从数据库读取全国县信息");
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}
}
