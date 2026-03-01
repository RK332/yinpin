package com.example.yinpin.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yinpin.R;
import com.example.yinpin.entity.User;
import com.example.yinpin.sqlite.DBUser;


public class RegisterActivity extends AppCompatActivity {
    private ImageView ivBack;
    private EditText etName, etAddress;
    private EditText etPassword;
    private EditText etPasswordConfirm;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bindView();
        initView();
    }

    private void bindView() {
        etAddress = findViewById(R.id.et_address);
        ivBack = findViewById(R.id.iv_back);
        etName = findViewById(R.id.et_name);
        etPassword = findViewById(R.id.et_password);
        etPasswordConfirm = findViewById(R.id.et_password_confirm);
        btnRegister = findViewById(R.id.btn_register);
    }

    private void initView() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etPassword.getText().toString().isEmpty() || etPasswordConfirm.getText().toString().isEmpty() || etName.getText().toString().isEmpty() || etAddress.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "数据不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "两次密码不一样", Toast.LENGTH_SHORT).show();
                    return;
                }
                User u = new User(etName.getText().toString(), etPassword.getText().toString(), etAddress.getText().toString());
                if (!TextUtils.isEmpty(DBUser.get(u.getName()).getName())) {
                    Toast.makeText(RegisterActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (DBUser.add(u)) {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                }


            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}