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
    //??????????????????
    private boolean first = true;

    private TextView tv_Add;
    private PoiSearch mPoiSearch;
    private BitmapDescriptor bitmap;//???????????????
    private double markerLatitude = 0;//??????
    private double markerLongitude = 0;//??????
    private Button button;
    private ImageButton ibLocation;//  ??????????????????
    private Marker marker;// ???????????????????????????
    private String city;
    //??????????????????
    private double latitude;
    //??????????????????
    private double longitude;

    private MyLocationData myLocationData;

    private MyLocationData locData;
    private RouteLine mRouteLine = null;
    // ????????????????????????
    private DrivingRoutePlanOption mDrivingRoutePlanOption;
    // ??????????????????
    private DrivingRouteResult mDrivingRouteResult = null;
    // ???????????????????????????????????????????????????
    private RoutePlanSearch mSearch = null;

    //POI??????
    private SuggestionSearch mSuggestionSearch = null;
    // ???????????????????????????
    private EditText mEditCity = null;
    private AutoCompleteTextView mKeyWordsViewend = null;
    private AutoCompleteTextView mKeyWordsViewstart = null;

    private ListView mSugListView;

    private boolean hasShowDialog = false;

    private OverlayManager mRouteOverlay = null;
    private boolean mUseDefaultIcon = false;
    //????????????
    private LatLng place;

    public static AtomicInteger parking=new AtomicInteger(0);

    private CheckBox find;
    // ????????????
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
        checkVersion();//????????????
        POIset(view);
        Buttonset(view);
    }


    /**
     * POI??????
     */
    private void POIset(View view)
    {
        // ????????????????????????????????????????????????????????????
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        // ?????????view
        mSugListView = (ListView) view.findViewById(R.id.sug_list);

        //?????????textview
        mKeyWordsViewend = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
        mKeyWordsViewstart=(AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView2);
        mKeyWordsViewend.setThreshold(1);
        mKeyWordsViewstart.setThreshold(1);

        // ??????????????????????????????????????????????????????
        mKeyWordsViewend.addTextChangedListener(new MyTextWatcher());
        mKeyWordsViewstart.addTextChangedListener(new MyTextWatcher());
    }




    //POI???????????????
    public class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() <= 0) {
                return;
            }

            // ??????????????????????????????????????????????????????onSuggestionResult()?????????
            mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                    .keyword(s.toString()) // ?????????
                    .city(city)); // ??????
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
                    Toast.makeText(getActivity(), "?????????????????????", Toast.LENGTH_LONG).show();
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
     * ??????????????????????????????
     */
    public void searchButtonProcess(View v) {
        // ?????????????????????????????????
        mRouteLine = null;

        // ????????????????????? ????????????
        PlanNode startNode = PlanNode.withCityNameAndPlaceName(city,textViewstart.getText().toString().trim());
        // ????????????
        PlanNode endNode = PlanNode.withCityNameAndPlaceName(city, textViewend.getText().toString().trim());

        // ????????????
        mDrivingRoutePlanOption.trafficPolicy(DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH_AND_TRAFFIC);
        // ????????????????????????
        mSearch.drivingSearch(mDrivingRoutePlanOption.from(startNode).to(endNode));
        mSugListView.setAdapter(null);

    }





    /**
     * POI???????????????????????????????????????requestSuggestion?????????????????????
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
     * ????????????????????????
     *
     * @param result ??????????????????
     */
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {

        if (result != null && result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // ?????????????????????????????????????????????????????????????????????????????????
            // result.getSuggestAddrInfo()
            Toast.makeText(getActivity(), "????????????????????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(getActivity(), "????????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            if (result.getRouteLines().size() > 1) {
                mDrivingRouteResult = result;
                if (!hasShowDialog) {
                    // ????????????Dialog
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
                            // ?????????????????????
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
                Log.d("route result", "?????????<0");
                return;
            }
        }
    }




    // ??????RouteOverly
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
     * ????????????
     */
    private void checkVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RxPermissions rxPermissions = new RxPermissions(getActivity());
            rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {//????????????
                            //????????????????????????
                            initLocation();// ???????????????
                        } else {//????????????
                            Toast.makeText(getActivity(), "???????????????", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            initLocation();// ???????????????
        }
    }






    /**
     * Button????????????
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
                    //??????
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
         * ?????????????????????????????????
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
     * ????????????????????????????????????
     *
     * @param v    ??????Button
     */
    public void searchNearbyProcess(View v) {

        LatLng latLng;
        int radius;
        radius = 1000;
        latLng = new LatLng(latitude, longitude);

        // ??????????????????
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                .keyword("?????????") // ???????????????
                .location(latLng) // ?????????
                .radius(radius) // ???????????? ????????? m
                .pageNum(mLoadIndex) // ????????????
                .radiusLimit(false)
                .scope(1);
        // ????????????
        mPoiSearch.searchNearby(nearbySearchOption);
    }


    /**
     * ?????????
     */
    private void initView(View view) {

        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getActivity().getApplicationContext());
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mMapView.removeViewAt(1);// ??????????????????Logo
        parklist=new HashMap<>();
        mBaiduMap=mMapView.getMap();
        tv_Add = view.findViewById(R.id.tv_Add0);
        textViewend=view.findViewById(R.id.autoCompleteTextView);
        textViewstart=view.findViewById(R.id.autoCompleteTextView2);
        find=view.findViewById(R.id.find);
        mPoiList=view.findViewById(R.id.poi_list);
        mPoiDetailView = view.findViewById(R.id.poi_detail);

        // ??????????????????????????????????????????
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.setOnMarkerDragListener(this);
        // ??????poi???????????????????????????????????????
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
//        Toast.makeText(MainActivity3.this,latitude+","+longitude,Toast.LENGTH_LONG).show();

    }



    /**
     * ???????????????
     */
    public void initLocation(){
        LocationClientOption option = new LocationClientOption();
        // ??????gps
        option.setOpenGps(true);
        // ??????????????????
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //????????????????????????
        option.setIsNeedAddress(true);
        // ??????????????????
        mBaiduMap.setMyLocationEnabled(true);
        //???????????????
        mBaiduMap.setTrafficEnabled(true);
        // ???????????????
        mLocationClient = new LocationClient(getActivity());
        MyLocationListener myListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myListener);

        mBaiduMap.setMyLocationData(myLocationData);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }




    /**
     * ?????????????????????
     */
    public void initViewListener() {
        // ??????????????????Option   // ?????????????????????
        mDrivingRoutePlanOption = new DrivingRoutePlanOption();
        // ?????????????????????  ??????????????????
        mDrivingRoutePlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_TIME_FIRST);
    }



    /**
     * ??????SDK????????????
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            // MapView ???????????????????????????????????????
            if (location == null || mMapView == null) {
                return;
            }
            tv_Add.setText(location.getAddrStr());
            latitude = location.getLatitude();    //??????????????????
            longitude = location.getLongitude();    //??????????????????
            city = location.getCity();              //????????????

            if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(location);
            }

            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// ????????????????????????????????????????????????
                    .direction(location.getDirection()) // ?????????????????????????????????????????????????????????0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            // ??????????????????, ??????????????????????????????????????????????????????
//            place= new LatLng(latitude, longitude);
//            try {
//                mapMaker();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if(first) {
//                Toast.makeText(getActivity(), "??????1km??????????????????" + parking.get(), Toast.LENGTH_LONG).show();
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
     * ????????????(????????????)
     */
    private void mapMaker() throws JSONException, InterruptedException {

        final String[] res = new String[1];
        try {

            MediaType js = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();       //??????HTTP?????????
            Request request = new Request.Builder()
                    .url("http://192.168.75.252:8080/Map/point")
                    .post(RequestBody.create(js,""))
                    .build();        //??????HTTP??????
            Response response=client.newCall(request).execute();  //?????????????????????
            if(response.isSuccessful()){
                res[0] = response.body().string();
            }else {
                Log.d("?????????", "????????????");
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

                    Log.i("?????????", record.getPark_id() + "  " + record.getIsusig());

                    //??????Marker?????????
                    LatLng point = new LatLng(record.getLatitude(), record.getLongitude());
                    //??????Marker??????
                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.parking_lot);
                    //??????MarkerOption???????????????????????????Marker
                    OverlayOptions option = new MarkerOptions()
                            .position(point) //????????????
                            .icon(bitmap) //????????????
                            .draggable(false)//????????????
                            //?????????????????????????????????????????????????????????
                            .flat(true)
                            .alpha(0.8f);
                    //??????????????????Marker????????????
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
        //????????????????????????
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
     * ????????????poi????????????
     *
     * @param result poi????????????
     */
    @Override
    public void onGetPoiResult(final PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(getActivity(), "???????????????", Toast.LENGTH_LONG).show();
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            showPoiDetailView(true);
//            mBaiduMap.clear();
            // ?????? View ?????????????????????view?????????
            mPoiDetailView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int padding = 50;
                    // ??????poi
                    PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(result);
                    overlay.addToMap();
                    // ?????? view ?????????
                    int PaddingBootom = mPoiDetailView.getMeasuredHeight();
                    // ???????????????????????????????????????????????????
                    overlay.zoomToSpanPaddingBounds(padding,padding,padding,PaddingBootom);
                    // ????????????????????????View????????????????????????????????????
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
            // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            String strInfo = "???";

            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }

            strInfo += "????????????";
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
            ss.add("???A.00002");
            ss.add("???A.00004");
            ss.add("???A.00242");


            // ????????????Dialog
            ChoseCarDialog choseCarDialog = new ChoseCarDialog(getActivity(), ss, RouteLineAdapter.Type.DRIVING_ROUTE);
            choseCarDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                }
            });
            choseCarDialog.setOnDismissListener(new ChoseCarDialog.OnItemInDlgClickListener() {
                public void onItemClick(int position) {
                    Toast.makeText(getActivity(), "???????????????????????????????????????"+poi.address,Toast.LENGTH_SHORT).show();
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
     * poilist ????????????
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
     * ???????????????????????????
     *
     * @param latLng ??????????????????
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
     * ?????????????????? view
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
        // ????????????InfoWindow
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
        // ?????????????????????
        // ??????????????????
        if (mSearch != null) {
            mSearch.destroy();
        }
        mBaiduMap.clear();
        mMapView.onDestroy();
        // ??????????????????
        mPoiSearch.destroy();

        mLocationClient.stop();
        //??????????????????
        mSuggestionSearch.destroy();
        //???????????????
        mBaiduMap.setTrafficEnabled(false);
        // ??????????????????
        mBaiduMap.setMyLocationEnabled(false);
        // ???activity??????onDestroy???????????????mMapView.onDestroy()
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //???activity??????onResume?????????mMapView. onResume ()?????????????????????????????????
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //???activity??????onPause?????????mMapView. onPause ()?????????????????????????????????
        mMapView.onPause();
    }


}
