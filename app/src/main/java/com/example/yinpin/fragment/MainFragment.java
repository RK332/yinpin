package com.example.yinpin.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.yinpin.R;
import com.example.yinpin.activity.DetailActivity;
import com.example.yinpin.adapter.StuffAdapter;
import com.example.yinpin.entity.Stuff;
import com.example.yinpin.sqlite.DBStuff;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private ListView lvStuff;
    private EditText etSearch;
    private ImageView ivSearch;

    private List<Stuff> stuffList;

    private StuffAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        bindView(v);
        initView();
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = etSearch.getText().toString();
                setSearch(str);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次Fragment可见时强制刷新数据
        refreshData();
    }
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && adapter != null) {
            // Fragment变为可见时刷新数据
            refreshData();
        }
    }

    private void bindView(View v) {
        lvStuff = v.findViewById(R.id.lv_stuff);
        etSearch = v.findViewById(R.id.et_search);
        ivSearch = v.findViewById(R.id.iv_search);
    }

    private void initView() {
        refreshData();
    }
    
    private void refreshData() {
        if (getContext() == null) {
            return;
        }
        // 强制重新从数据库加载数据
        List<Stuff> allStuff = DBStuff.getAll();
        Log.d(TAG, "加载数据，原始商品数量: " + (allStuff != null ? allStuff.size() : 0));
        
        // 过滤掉所有旧的汉堡相关数据
        stuffList = new ArrayList<>();
        if (allStuff != null) {
            for (Stuff stuff : allStuff) {
                String name = stuff.getName();
                String kind = stuff.getKind();
                String title = stuff.getTitle();
                
                // 检查是否是旧的汉堡数据
                boolean isOldBurgerData = name.contains("堡") || name.contains("鸡腿") || name.contains("牛肉") ||
                                         kind.contains("堡") || kind.contains("鸡肉") || kind.contains("牛肉") ||
                                         title.contains("汉堡") || title.contains("鸡腿") || title.contains("面包") ||
                                         name.contains("不素") || name.contains("牛气") || name.contains("麦辣") ||
                                         name.contains("板烧") || name.contains("双层") || name.contains("吉士");
                
                if (isOldBurgerData) {
                    Log.w(TAG, "过滤掉旧的汉堡数据: " + name);
                    // 同时从数据库删除这个旧数据
                    DBStuff.deleteById(stuff.getId());
                    continue;
                }
                
                // 只保留饮品相关的数据
                stuffList.add(stuff);
                Log.d(TAG, "保留商品: " + name + ", 分类: " + kind);
            }
        }
        
        Log.d(TAG, "过滤后商品数量: " + stuffList.size());
        
        // 如果数据为空，等待Application重新初始化
        if (stuffList.isEmpty()) {
            Log.w(TAG, "数据为空，等待Application初始化");
            // 延迟一下再刷新，给Application时间初始化
            if (lvStuff != null) {
                lvStuff.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                    }
                }, 500);
            }
            return;
        }
        
        if (adapter == null) {
            adapter = new StuffAdapter(getContext(), R.layout.item_stuff, stuffList);
            lvStuff.setAdapter(adapter);
            lvStuff.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (stuffList != null && i < stuffList.size()) {
                        Intent intent = new Intent();
                        intent.putExtra("id", (stuffList.get(i)).getId());
                        intent.setClass(getContext(), DetailActivity.class);
                        startActivity(intent);
                    }
                }
            });
        } else {
            adapter.setData(stuffList);
        }
    }

    private void setSearch(String str) {
        List<Stuff> arrayTemp = DBStuff.getAll();
        List<Stuff> array = new ArrayList<>();
        for (Stuff t : arrayTemp) {
            if (t.getName().contains(str) || t.getTitle().contains(str)) {
                array.add(t);
            }
        }
        if (array.size() == 0) {
            Toast.makeText(getContext(), "未搜索到关键字商品", Toast.LENGTH_SHORT).show();
            array = arrayTemp;
        }
        stuffList = array;
        adapter.setData(stuffList);
    }

}