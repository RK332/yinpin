package com.example.yinpin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.yinpin.R;
import com.example.yinpin.entity.Record;
import com.example.yinpin.entity.Stuff;
import com.example.yinpin.entity.User;
import com.example.yinpin.sqlite.DBCart;
import com.example.yinpin.sqlite.DBRecord;
import com.example.yinpin.sqlite.DBStuff;
import com.example.yinpin.utils.CurrentUserUtils;
import com.google.android.material.snackbar.Snackbar;

public class DetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvName, tvPrice, tvKind;
    private ImageView ivImage, ivBack;
    private Button btnBuy, btnAdd;

    private Stuff stuff;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        stuff = DBStuff.getById(Integer.parseInt(id));
        user = CurrentUserUtils.getCurrentUser();
        bindView();
        initView();
    }

    private void bindView() {
        tvTitle = findViewById(R.id.tv_title);
        tvName = findViewById(R.id.tv_name);
        tvPrice = findViewById(R.id.tv_price);
        tvKind = findViewById(R.id.tv_kind);
        ivImage = findViewById(R.id.iv_image);
        btnBuy = findViewById(R.id.btn_buy);
        btnAdd = findViewById(R.id.btn_add);
        ivBack = findViewById(R.id.iv_back);
    }

    private void initView() {
        tvTitle.setText(stuff.getTitle());
        tvName.setText("名称：" + stuff.getName());
        tvPrice.setText("价格：¥" + stuff.getPrice());

        Glide.with(ivImage).load(stuff.getPic()).into(ivImage);

        tvKind.setText("分类：" + stuff.getKind());
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 执行数据库操作
                DBCart.add(stuff.getId(), user.getName());
                int quantity = DBCart.getQuantity(stuff.getId(), user.getName());
                
                // 使用Snackbar显示提示（从底部弹出，更明显）
                String message = "✓ 加购成功！购物车中已有 " + quantity + " 件";
                Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
                
                // 同时使用对话框确保用户一定能看到
                new AlertDialog.Builder(DetailActivity.this)
                    .setTitle("提示")
                    .setMessage(message)
                    .setPositiveButton("确定", null)
                    .show();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Record r = new Record(user.getName(), stuff.getId(), stuff.getName(), stuff.getPrice(), user.getAddress());
                boolean success = DBRecord.add(r);
                
                String message;
                if (success) {
                    message = "✓ 购买成功！\n商品：" + stuff.getName() + "\n价格：¥" + stuff.getPrice() + " 元";
                } else {
                    message = "❌ 购买失败，请重试";
                }
                
                // 使用Snackbar显示提示（从底部弹出）
                Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
                
                // 同时使用对话框确保用户一定能看到
                new AlertDialog.Builder(DetailActivity.this)
                    .setTitle(success ? "购买成功" : "购买失败")
                    .setMessage(message)
                    .setPositiveButton("确定", null)
                    .show();
            }
        });
    }
}