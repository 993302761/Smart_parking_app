package com.example.myapplication.home;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.mapapi.search.core.RouteLine;
import com.example.myapplication.R;
import com.example.myapplication.function.RouteLineAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChoseCarDialog extends Dialog  {
    private ArrayAdapter<String> mRouteLines;
    private ListView carList;
    public OnItemInDlgClickListener onItemInDlgClickListener;



    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        super.setOnDismissListener(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtive_choise);
        carList = (ListView) findViewById(R.id.transitList0);
        carList.setAdapter(mRouteLines);
        carList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemInDlgClickListener.onItemClick(position);
                dismiss();
            }
        });
    }



    public ChoseCarDialog(@NonNull Context context) {
        super(context);
    }

    public ChoseCarDialog(Context context, ArrayAdapter<String> routeLines, RouteLineAdapter.Type type) {
        super(context, 0);
        mRouteLines  = routeLines;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void setOnDismissListener(OnItemInDlgClickListener itemListener) {
        onItemInDlgClickListener = itemListener;
    }


    // 响应DLg中的List item 点击
    static interface OnItemInDlgClickListener {
        public void onItemClick(int position);
    }
}
