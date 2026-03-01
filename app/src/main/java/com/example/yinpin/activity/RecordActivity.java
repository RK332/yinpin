package com.example.yinpin.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yinpin.R;
import com.example.yinpin.adapter.RecordAdapter;
import com.example.yinpin.entity.Record;
import com.example.yinpin.entity.User;
import com.example.yinpin.sqlite.DBRecord;
import com.example.yinpin.utils.CurrentUserUtils;

import java.util.List;

public class RecordActivity extends AppCompatActivity {
    private ListView lvRecord;
    private TextView tvEmpty;
    private ImageView ivBack;
    private RecordAdapter adapter;
    private List<Record> recordList;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        bindView();
        initView();
    }

    private void bindView() {
        lvRecord = findViewById(R.id.lv_record);
        tvEmpty = findViewById(R.id.tv_empty);
        ivBack = findViewById(R.id.iv_back);
    }

    private void initView() {
        user = CurrentUserUtils.getCurrentUser();
        
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        refreshData();
    }

    private void refreshData() {
        recordList = DBRecord.getAll(user.getName());
        if (recordList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
        
        adapter = new RecordAdapter(this, R.layout.item_record, recordList);
        adapter.setOnDeleteListener(new RecordAdapter.OnDeleteListener() {
            @Override
            public void onDelete(int position, Record record) {
                if (DBRecord.delete(record.getUsername(), record.getId(), record.getPrice(), record.getAddress())) {
                    Toast.makeText(RecordActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    refreshData();
                } else {
                    Toast.makeText(RecordActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lvRecord.setAdapter(adapter);
    }
}