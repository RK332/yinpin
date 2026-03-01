package com.example.yinpin.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yinpin.R;
import com.example.yinpin.entity.User;
import com.example.yinpin.utils.CurrentUserUtils;

public class InfoActivity extends AppCompatActivity {
    private TextView tvName, tvAddress;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        bindView();
        initView();
    }

    private void bindView() {
        tvName = findViewById(R.id.tv_name);
        tvAddress = findViewById(R.id.tv_address);
        ivBack = findViewById(R.id.iv_back);
    }

    private void initView() {
        User user = CurrentUserUtils.getCurrentUser();
        tvName.setText(user.getName());
        tvAddress.setText(user.getAddress());
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}