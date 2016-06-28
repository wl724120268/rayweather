package com.rayweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.rayweather.app.R;
import com.rayweather.app.db.RayWeatherDB;
import com.rayweather.app.model.City;
import com.rayweather.app.model.County;
import com.rayweather.app.model.Province;
import com.rayweather.app.util.HttpCallbackListener;
import com.rayweather.app.util.HttpUtil;

import com.rayweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.DownloadManager.Query;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private RayWeatherDB rayWeatherDB;
	private List<String> dataList = new ArrayList<String>();

	/**
	 * 省列表
	 */
	private List<Province> provinceList;

	/**
	 * 市列表
	 */
	private List<City> cityList;

	/**
	 * 县列表
	 */
	private List<County> countyList;

	/**
	 * 选中的省份
	 */
	private Province selectedProvince;

	/**
	 * 选中的城市
	 */
	private City selectedCity;

	/**
	 * 当前选中级别
	 */
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selecte", false)){
			Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
			Log.d("ChooseAreaActivity", "一开始就跳转到WeatherActivity");
			startActivity(intent);
			
			finish();
			return;
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);

		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		rayWeatherDB = RayWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
				}else if(currentLevel == LEVEL_COUNTY){
					String countyCode = countyList.get(index).getCountyCode();	
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("county_code", countyCode);  //重点
					Log.d("ChooseAreaActivity", "最后跳到WeatherActivity");
					startActivity(intent);
					finish();
				}

			}
		});
		queryProvinces(); // 加载省级数据
	}

	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有查询再去服务器查询
	 */
	private void queryProvinces() {
		provinceList = rayWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
			Log.d("ChooseAreaActivity", "从数据库查询Province");
		} else {
			queryFromServer(null, "province");
			Log.d("ChooseAreaActivity", "pro queryFromServer");
		}
	}

	/**
	 * 查询全省所有的市，优先从数据库查询，如果没有查询再去服务器查询
	 */
	private void queryCities() {
		cityList = rayWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			Log.d("ChooseAreaActivity", "从数据库查询City");
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
			Log.d("ChooseAreaActivity", "city queryFromServer");
		}
	}

	/**
	 * 查询选中市内所有的县，优先从数据库查询，如果没有查询再去服务器查询
	 */
	private void queryCounties() {
		countyList = rayWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
			Log.d("ChooseAreaActivity", "从数据库查询County");
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
			Log.d("ChooseAreaActivity", "county queryFromServer");
		}
	}

	/**
	 * 根据传入的代号和类型从服务器上查询省市县数据
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(rayWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(rayWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(rayWeatherDB,
							response, selectedCity.getId());
				}
				if (result) {
					// 通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();

							}
						}
					});

				}
			}

			@Override
			public void onError(Exception e) {
				// 通过runOnUiThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}

				});
			}
		});
	}
	
	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog(){
		if(progressDialog ==null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog(){
		if(progressDialog !=null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 捕获back按键，根据当前级别来判断，此时应该返回市列表，省列表，还是直接退出
	 */
	@Override
	public void onBackPressed() {
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else {
			finish();
		}
	}
	
}
