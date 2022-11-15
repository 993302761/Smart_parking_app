package com.example.myapplication.home;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.myapplication.R;
import com.example.myapplication.basic_class.Parking;
import com.example.myapplication.function.DrivingRouteOverlay;
import com.example.myapplication.function.OverlayManager;
import com.example.myapplication.function.PoiListAdapter;
import com.example.myapplication.function.PoiOverlay;
import com.example.myapplication.function.RouteLineAdapter;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Functionrealization extends Fragment   implements OnGetPoiSearchResultListener,OnGetSuggestionResultListener,AdapterView.OnItemClickListener, PoiListAdapter.OnGetChildrenLocationListener,BaiduMap.OnMarkerDragListener, OnGetRoutePlanResultListener, BaiduMap.OnMapClickListener {


    private TextView textViewend;
    private TextView textViewstart;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private boolean isFirstLoc = true;
    //是否首次定位
    private boolean first = true;

    private TextView tv_Add;
    private PoiSearch mPoiSearch;
    private BitmapDescriptor bitmap;//标点的图标
    private double markerLatitude = 0;//纬度
    private double markerLongitude = 0;//经度
    private Button button;
    private ImageButton ibLocation;//  重置定位按钮
    private Marker marker;// 标点也可以是覆盖物
    private String city;
    //当前位置纬度
    private double latitude;
    //当前位置经度
    private double longitude;

    private MyLocationData myLocationData;

    private MyLocationData locData;
    private RouteLine mRouteLine = null;
    // 驾车路线规划参数
    private DrivingRoutePlanOption mDrivingRoutePlanOption;
    // 驾车路线结果
    private DrivingRouteResult mDrivingRouteResult = null;
    // 搜索模块，也可去掉地图模块独立使用
    private RoutePlanSearch mSearch = null;

    //POI搜索
    private SuggestionSearch mSuggestionSearch = null;
    // 搜索关键字输入窗口
    private EditText mEditCity = null;
    private AutoCompleteTextView mKeyWordsViewend = null;
    private AutoCompleteTextView mKeyWordsViewstart = null;

    private ListView mSugListView;

    private boolean hasShowDialog = false;

    private OverlayManager mRouteOverlay = null;
    private boolean mUseDefaultIcon = false;
    //我的位置
    private LatLng place;

    public static AtomicInteger parking=new AtomicInteger(0);

    private CheckBox find;
    // 检索分页
    private int mLoadIndex = 0;

    private List<PoiInfo> mAllPoi;

    private BitmapDescriptor mBitmap = BitmapDescriptorFactory.fromResource(R.drawable.parking_lot);

    private ListView mPoiList;
    private RelativeLayout mPoiDetailView;

    private HashMap<String,Marker> parklist;
    private ListView mRouteListView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_function,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Start(view);
    }


    private void Start(View view) {
        initView(view);
        initViewListener();
        checkVersion();//检查版本
        POIset(view);
        Buttonset(view);
    }


    /**
     * POI检索
     */
    private void POIset(View view)
    {
        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        // 初始化view
        mSugListView = (ListView) view.findViewById(R.id.sug_list);

        //初始化textview
        mKeyWordsViewend = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
        mKeyWordsViewstart=(AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView2);
        mKeyWordsViewend.setThreshold(1);
        mKeyWordsViewstart.setThreshold(1);

        // 当输入关键字变化时，动态更新建议列表
        mKeyWordsViewend.addTextChangedListener(new MyTextWatcher());
        mKeyWordsViewstart.addTextChangedListener(new MyTextWatcher());
    }




    //POI关键字更新
    public class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() <= 0) {
                return;
            }

            // 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
            mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                    .keyword(s.toString()) // 关键字
                    .city(city)); // 城市
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @io.reactivex.annotations.NonNull String[] permissions, @io.reactivex.annotations.NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "没有定位权限！", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                } else {
                    requestLocation();
                }
        }
    }


    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }




    /**
     * 发起路线规划搜索示例
     */
    public void searchButtonProcess(View v) {
        // 重置浏览节点的路线数据
        mRouteLine = null;

        // 设置起终点信息 起点参数
        PlanNode startNode = PlanNode.withCityNameAndPlaceName(city,textViewstart.getText().toString().trim());
        // 终点参数
        PlanNode endNode = PlanNode.withCityNameAndPlaceName(city, textViewend.getText().toString().trim());

        // 开启路况
        mDrivingRoutePlanOption.trafficPolicy(DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH_AND_TRAFFIC);
        // 发起驾车路线规划
        mSearch.drivingSearch(mDrivingRoutePlanOption.from(startNode).to(endNode));
        mSugListView.setAdapter(null);

    }





    /**
     * POI获取在线建议搜索结果，得到requestSuggestion返回的搜索结果
     */
    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
            return;
        }

        List<HashMap<String, String>> suggest = new ArrayList<>();
        for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
            if (info.getKey() != null && info.getDistrict() != null && info.getCity() != null) {
                HashMap<String, String> map = new HashMap<>();
                map.put("key",info.getKey());
                map.put("city",info.getCity());
                map.put("dis",info.getDistrict());
                suggest.add(map);
            }
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity().getApplicationContext(),
                suggest,
                R.layout.item_layout,
                new String[]{"key", "city","dis"},
                new int[]{R.id.sug_key, R.id.sug_city, R.id.sug_dis});

        mSugListView.setAdapter(simpleAdapter);


        simpleAdapter.notifyDataSetChanged();
    }











    /**
     * 驾车路线结果回调
     *
     * @param result 驾车路线结果
     */
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {

        if (result != null && result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            Toast.makeText(getActivity(), "起终点或途经点地址有岐义", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            if (result.getRouteLines().size() > 1) {
                mDrivingRouteResult = result;
                if (!hasShowDialog) {
                    // 多条路线Dialog
                    SelectRouteDialog selectRouteDialog = new SelectRouteDialog(getActivity(),
                            result.getRouteLines(), RouteLineAdapter.Type.DRIVING_ROUTE);
                    selectRouteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShowDialog = false;
                        }
                    });
                    selectRouteDialog.setOnItemInDlgClickLinster(new SelectRouteDialog.OnItemInDlgClickListener() {
                        public void onItemClick(int position) {
                            // 获取选中的路线
                            mRouteLine = mDrivingRouteResult.getRouteLines().get(position);
                            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                            mBaiduMap.setOnMarkerClickListener(overlay);
                            mRouteOverlay = overlay;
                            overlay.setData(mDrivingRouteResult.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }
                    });
                    selectRouteDialog.show();
                    hasShowDialog = true;
                }
            } else if (result.getRouteLines().size() == 1) {
                mRouteLine = result.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                mRouteOverlay = overlay;
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            } else {
                Log.d("route result", "结果数<0");
                return;
            }
        }
    }




    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        private MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (mUseDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (mUseDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }











    /**
     * 检查版本
     */
    private void checkVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RxPermissions rxPermissions = new RxPermissions(getActivity());
            rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {//申请成功
                            //发起连续定位请求
                            initLocation();// 定位初始化
                        } else {//申请失败
                            Toast.makeText(getActivity(), "权限未开启", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            initLocation();// 定位初始化
        }
    }






    /**
     * Button事件处理
     */
    public void Buttonset(View view)
    {
        Button button=(Button)view.findViewById(R.id.searchBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButtonProcess(v);
            }
        });

        find.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //选定
                    searchNearbyProcess(view);
                }else{
                    mBaiduMap.clear();
                }
                parklist=new HashMap<>();

            }
        });

        mPoiList.setOnItemClickListener(this);


        mSugListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView mEndNode = view.findViewById(R.id.sug_key);
//                mSugListView.setAdapter(null);
                if(textViewend.isFocused()) {
                    textViewend.setText(mEndNode.getText().toString());
                    textViewend.clearFocus();
                }
                else {
                    textViewstart.setText(mEndNode.getText().toString());
                    textViewstart.clearFocus();

                }

            }
        });

        /**
         * 为百度地图增加监听函数
         */
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mSugListView.setAdapter(null);
                showPoiDetailView(false);
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }
        });
    }




    /**
     * 响应周边搜索按钮点击事件
     *
     * @param v    检索Button
     */
    public void searchNearbyProcess(View v) {

        LatLng latLng;
        int radius;
        radius = 1000;
        latLng = new LatLng(latitude, longitude);

        // 配置请求参数
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                .keyword("停车场") // 检索关键字
                .location(latLng) // 经纬度
                .radius(radius) // 检索半径 单位： m
                .pageNum(mLoadIndex) // 分页编号
                .radiusLimit(false)
                .scope(1);
        // 发起检索
        mPoiSearch.searchNearby(nearbySearchOption);
    }


    /**
     * 初始化
     */
    private void initView(View view) {

        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getActivity().getApplicationContext());
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mMapView.removeViewAt(1);// 删除百度地图Logo
        parklist=new HashMap<>();
        mBaiduMap=mMapView.getMap();
        tv_Add = view.findViewById(R.id.tv_Add0);
        textViewend=view.findViewById(R.id.autoCompleteTextView);
        textViewstart=view.findViewById(R.id.autoCompleteTextView2);
        find=view.findViewById(R.id.find);
        mPoiList=view.findViewById(R.id.poi_list);
        mPoiDetailView = view.findViewById(R.id.poi_detail);

        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.setOnMarkerDragListener(this);
        // 创建poi检索实例，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
//        Toast.makeText(MainActivity3.this,latitude+","+longitude,Toast.LENGTH_LONG).show();

    }



    /**
     * 定位初始化
     */
    public void initLocation(){
        LocationClientOption option = new LocationClientOption();
        // 打开gps
        option.setOpenGps(true);
        // 设置坐标类型
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //设置需要地址信息
        option.setIsNeedAddress(true);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //开启交通图
        mBaiduMap.setTrafficEnabled(true);
        // 定位初始化
        mLocationClient = new LocationClient(getActivity());
        MyLocationListener myListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myListener);

        mBaiduMap.setMyLocationData(myLocationData);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }




    /**
     * 初始化控件监听
     */
    public void initViewListener() {
        // 创建路线规划Option   // 设置参数前创建
        mDrivingRoutePlanOption = new DrivingRoutePlanOption();
        // 时间优先策略，  默认时间优先
        mDrivingRoutePlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_TIME_FIRST);
    }



    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            // MapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            tv_Add.setText(location.getAddrStr());
            latitude = location.getLatitude();    //获取纬度信息
            longitude = location.getLongitude();    //获取经度信息
            city = location.getCity();              //获取城市

            if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(location);
            }

            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// 设置定位数据的精度信息，单位：米
                    .direction(location.getDirection()) // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            // 设置定位数据, 只有先允许定位图层后设置数据才会生效
//            place= new LatLng(latitude, longitude);
//            try {
//                mapMaker();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if(first) {
//                Toast.makeText(getActivity(), "附近1km内的空车位：" + parking.get(), Toast.LENGTH_LONG).show();
//                first=false;
//            }
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(latLng).zoom(20.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }

        }
    }





    /**
     * 车位标记(存在问题)
     */
    private void mapMaker() throws JSONException, InterruptedException {

        final String[] res = new String[1];
        try {

            MediaType js = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();       //创建HTTP客户端
            Request request = new Request.Builder()
                    .url("http://192.168.75.252:8080/Map/point")
                    .post(RequestBody.create(js,""))
                    .build();        //创建HTTP请求
            Response response=client.newCall(request).execute();  //执行发送的指令
            if(response.isSuccessful()){
                res[0] = response.body().string();
            }else {
                Log.d("数据：", "获取失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();

        JSONArray jsonArray=new JSONArray(res[0]);

        for (int i=0;i<jsonArray.length();i++)
        {
            JSONObject jsonObject= jsonArray.getJSONObject(i);
            Parking record = gson.fromJson(jsonObject.toString(),Parking.class);

            if(parklist.get(record.getPark_id())==null  && record.getIsusig()==true) {

                    Log.i("数据：", record.getPark_id() + "  " + record.getIsusig());

                    //定义Marker坐标点
                    LatLng point = new LatLng(record.getLatitude(), record.getLongitude());
                    //构建Marker图标
                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.parking_lot);
                    //构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions()
                            .position(point) //必传参数
                            .icon(bitmap) //必传参数
                            .draggable(false)//不可拖拽
                            //设置平贴地图，在地图中双指下拉查看效果
                            .flat(true)
                            .alpha(0.8f);
                    //在地图上添加Marker，并显示
                    Marker mMarker = (Marker) mBaiduMap.addOverlay(option);

                    parklist.put(record.getPark_id(), mMarker);
                    distance(point);
            }
            else if(parklist.get(record.getPark_id())!=null&&record.getIsusig()==false)
            {
                Marker mMarker = parklist.get(record.getPark_id());
                mMarker.remove();
                parklist.remove(record.getPark_id());

            }

        }

    }





    public boolean distance(LatLng point)
    {
        MarkerOptions ooA = new MarkerOptions().position(place);

        MarkerOptions ooB = new MarkerOptions().position(point);
        //获取与车位的距离
        double dis = DistanceUtil. getDistance(ooA.getPosition(), ooB.getPosition());

        if (dis<1000) {
            parking.set(parking.get()+1);
        }
        return true;

    }






    private void navigateTo(BDLocation bdLocation) {
        if (isFirstLoc) {
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(update);
            isFirstLoc = false;
        }
    }



    /**
     * 获取周边poi检索结果
     *
     * @param result poi查询结果
     */
    @Override
    public void onGetPoiResult(final PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(getActivity(), "未找到结果", Toast.LENGTH_LONG).show();
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            showPoiDetailView(true);
//            mBaiduMap.clear();
            // 监听 View 绘制完成后获取view的高度
            mPoiDetailView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int padding = 50;
                    // 添加poi
                    PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(result);
                    overlay.addToMap();
                    // 获取 view 的高度
                    int PaddingBootom = mPoiDetailView.getMeasuredHeight();
                    // 设置显示在规定宽高中的地图地理范围
                    overlay.zoomToSpanPaddingBounds(padding,padding,padding,PaddingBootom);
                    // 加载完后需要移除View的监听，否则会被多次触发
                    mPoiDetailView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            })  ;
            int radius = 1000;
            mAllPoi = result.getAllPoi();
            PoiListAdapter poiListAdapter = new PoiListAdapter(getActivity(), mAllPoi);
            poiListAdapter.setOnGetChildrenLocationListener(this);
            mPoiList.setAdapter(poiListAdapter);
            showPoiDetailView(true);

            return;
        }

        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";

            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }

            strInfo += "找到结果";
            Toast.makeText(getActivity(), strInfo, Toast.LENGTH_LONG).show();
        }
    }




    @Override
    public void getChildrenLocation(LatLng childrenLocation) {
        addPoiLoction(childrenLocation);
    }

    private  class MyPoiOverlay extends PoiOverlay {
        MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);

            ArrayAdapter<String> ss=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1);
            ss.add("津A.00002");
            ss.add("津A.00004");
            ss.add("津A.00242");


            // 多条路线Dialog
            ChoseCarDialog choseCarDialog = new ChoseCarDialog(getActivity(), ss, RouteLineAdapter.Type.DRIVING_ROUTE);
            choseCarDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                }
            });
            choseCarDialog.setOnDismissListener(new ChoseCarDialog.OnItemInDlgClickListener() {
                public void onItemClick(int position) {
                    Toast.makeText(getActivity(), "预约成功，请于十分钟内到达"+poi.address,Toast.LENGTH_SHORT).show();
                    mBaiduMap.clear();
                    mSugListView.setAdapter(null);
                    showPoiDetailView(false);
                    PoiInfo poiInfo = mAllPoi.get(position);
                    addPoiLoction(poiInfo.getLocation());
                }
            });
            choseCarDialog.show();
            return true;
        }
    }



    /**
     * poilist 点击处理
     *
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PoiInfo poiInfo = mAllPoi.get(position);
        if (poiInfo.getLocation() == null) {
            return;
        }

        addPoiLoction(poiInfo.getLocation());
    }



    /**
     * 更新到子节点的位置
     *
     * @param latLng 子节点经纬度
     */
    private void addPoiLoction(LatLng latLng){
        mBaiduMap.clear();
        showPoiDetailView(false);
        OverlayOptions markerOptions = new MarkerOptions().position(latLng).icon(mBitmap);
        mBaiduMap.addOverlay(markerOptions);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng);
        builder.zoom(18);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }


    /**
     * 是否展示详情 view
     *
     */
    private void showPoiDetailView(boolean whetherShow) {
        if (whetherShow) {
            mPoiDetailView.setVisibility(View.VISIBLE);

        } else {
            mPoiDetailView.setVisibility(View.GONE);

        }
    }


    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }


    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        // 隐藏当前InfoWindow
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {

    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        // 释放检索对象
        if (mSearch != null) {
            mSearch.destroy();
        }
        mBaiduMap.clear();
        mMapView.onDestroy();
        // 释放检索对象
        mPoiSearch.destroy();

        mLocationClient.stop();
        //释放检索实例
        mSuggestionSearch.destroy();
        //关闭交通图
        mBaiduMap.setTrafficEnabled(false);
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // 在activity执行onDestroy时必须调用mMapView.onDestroy()
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }


}
