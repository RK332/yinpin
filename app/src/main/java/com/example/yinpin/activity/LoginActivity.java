package com.example.yinpin.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yinpin.R;
import com.example.yinpin.entity.User;
import com.example.yinpin.sqlite.DBUser;
import com.example.yinpin.utils.CurrentUserUtils;

public class LoginActivity extends AppCompatActivity {

    private static final String PREF_NAME = "login_pref";
    private static final String KEY_REMEMBER_PASSWORD = "remember_password";
    private static final String KEY_SAVED_USERNAME = "saved_username";
    private static final String KEY_SAVED_PASSWORD = "saved_password";

    private Button btnLogin;
    private Button btnRegister;
    private EditText etName, etPassword;
    private CheckBox cbRememberPassword;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        bindView();
        initView();
    }


    private void bindView() {
        btnLogin = findViewById(R.id.btn_login);
        etPassword = findViewById(R.id.et_password);
        etName = findViewById(R.id.et_name);
        btnRegister = findViewById(R.id.btn_register);
        cbRememberPassword = findViewById(R.id.cb_remember_password);
    }

    private void initView() {
        // 检查是否保存了用户名和密码
        boolean rememberPassword = sharedPreferences.getBoolean(KEY_REMEMBER_PASSWORD, false);
        cbRememberPassword.setChecked(rememberPassword);
        
        if (rememberPassword) {
            String savedUsername = sharedPreferences.getString(KEY_SAVED_USERNAME, "");
            String savedPassword = sharedPreferences.getString(KEY_SAVED_PASSWORD, "");
            etName.setText(savedUsername);
            etPassword.setText(savedPassword);
        } else {
            // 如果没有记住密码，只填充用户名
            User user = CurrentUserUtils.getCurrentUser();
            if (user != null) {
                etName.setText(user.getName());
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = etName.getText().toString();
                String p = etPassword.getText().toString();
                if (DBUser.check(n, p)) {
                    // 保存记住密码状态
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (cbRememberPassword.isChecked()) {
                        editor.putBoolean(KEY_REMEMBER_PASSWORD, true);
                        editor.putString(KEY_SAVED_USERNAME, n);
                        editor.putString(KEY_SAVED_PASSWORD, p);
                    } else {
                        editor.putBoolean(KEY_REMEMBER_PASSWORD, false);
                        editor.remove(KEY_SAVED_USERNAME);
                        editor.remove(KEY_SAVED_PASSWORD);
                    }
                    editor.apply();
                    
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    CurrentUserUtils.setCurrentUser(DBUser.get(n));
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // 关闭登录页面
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败，用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}