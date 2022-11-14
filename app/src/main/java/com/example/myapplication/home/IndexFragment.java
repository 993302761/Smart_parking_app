package com.example.myapplication.home;


import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.gson.Gson;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherHourlyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class IndexFragment extends Fragment {

    private TextView weather;
    private TextView temperature;
    private TextView humidity;
    private TextView visibility;
    private TextView precipitation;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_index,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWeather();
        initViews(view);
    }

    private void initWeather() {
        HeConfig.init("HE2110292042101627","703a814da83942969e52d95855979b7d");
        HeConfig.switchToDevService();
    }

    private void initViews(View view) {

        weather=view.findViewById(R.id.weather);
        temperature=view.findViewById(R.id.temperature);
        visibility=view.findViewById(R.id.visibility);
        humidity=view.findViewById(R.id.humidity);
        precipitation=view.findViewById(R.id.precipitation);
        getWeather();
    }



    private void getWeather() {
        QWeather.getWeatherNow(getActivity(), "CN101030100", Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "getWeather onError: " + e);
            }

            @Override
            public void onSuccess(WeatherNowBean weatherBean) {
                Log.i(TAG, "getWeather onSuccess: " + new Gson().toJson(weatherBean));
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                String weather0=null;
                String humidity0=null;
                String temperature0=null;
                String precipitation0=null;
                String visibility0=null;
                if (Code.OK == weatherBean.getCode()) {
                    WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                    WeatherDailyBean dailyBean;
                    weather0=now.getText();
                    humidity0=now.getHumidity();
                    temperature0=now.getTemp();
                    precipitation0=now.getPrecip();
                    visibility0=now.getVis();
                } else {
                    //在此查看返回数据失败的原因
                    Code code = weatherBean.getCode();
                    Log.i(TAG, "failed code: " + code);
                }
                weather.setText(weather0);
                humidity.setText(humidity0);
                visibility.setText(visibility0+"公里");
                temperature.setText(temperature0+"℃");
                precipitation.setText(precipitation0+"毫米");
            }
        });

    }
}



