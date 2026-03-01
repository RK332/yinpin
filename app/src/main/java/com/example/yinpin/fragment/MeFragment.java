package com.example.yinpin.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.yinpin.R;
import com.example.yinpin.activity.AboutActivity;
import com.example.yinpin.activity.InfoActivity;
import com.example.yinpin.activity.RecordActivity;
import com.example.yinpin.entity.User;
import com.example.yinpin.sqlite.DBUser;
import com.example.yinpin.utils.CurrentUserUtils;

public class MeFragment extends Fragment {

   private TextView tvName;
   private LinearLayout llRecord, llChangeAddress, llInfo, llAbout, llContact;

    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         user = CurrentUserUtils.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_me, container, false);
        bindView(v);
        initView();
        return v;
    }

    private void bindView(View v) {
        llRecord = v.findViewById(R.id.myrecord);
        tvName = v.findViewById(R.id.tv_name);
        llChangeAddress = v.findViewById(R.id.ll_change_address);
        llInfo = v.findViewById(R.id.ll_info);
        llAbout = v.findViewById(R.id.ll_about);
        llContact = v.findViewById(R.id.ll_contact);
    }

    private void initView() {
        tvName.setText("欢迎您,亲爱的" + user.getName());
        llAbout.setOnClickListener(this::onClick);
        llChangeAddress.setOnClickListener(this::onClick);
        llRecord.setOnClickListener(this::onClick);
        llContact.setOnClickListener(this::onClick);
        llInfo.setOnClickListener(this::onClick);
    }

    void onClick(View v) {
        int id = v.getId();
        if (id == R.id.myrecord) {//Toast.makeText(getContext(), "record", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), RecordActivity.class);
            startActivity(intent);
        } else if (id == R.id.ll_contact) {
            joinPhone();
        } else if (id == R.id.ll_info) {
            Intent intent1 = new Intent(getActivity(), InfoActivity.class);
            startActivity(intent1);
        } else if (id == R.id.ll_change_address) {
            changeAddress();
        } else if (id == R.id.ll_about) {
            Intent intent3 = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent3);
        }
    }

    //修改收货地址
    void changeAddress() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_address, null);
        AlertDialog.Builder customBuilder = new AlertDialog.Builder(getContext()).setTitle("修改收货地址").setView(view);
        AlertDialog dialog = customBuilder.create();
        dialog.show();
        //自定义按钮并注册监听器
        EditText ed_number = view.findViewById(R.id.et_address);

        ed_number.setHint(user.getAddress());
        Button btn_ok = view.findViewById(R.id.btn_confirm);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), ed_number.getText().toString(), Toast.LENGTH_LONG).show();
                user.setAddress(ed_number.getText().toString());
                if (DBUser.change(user)) {
                    CurrentUserUtils.setCurrentUser(user);
                    Toast.makeText(getContext(), "修改成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "修改失败！", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    void joinPhone() {
        // 显示客服联系方式对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("联系客服")
                .setMessage("客服电话：12345678910\n\n工作时间：周一至周日 9:00-21:00\n\n感谢您的支持！")
                .setPositiveButton("确定", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("拨打电话", (dialog, which) -> {
                    Toast.makeText(getContext(), "客服电话：12345678910", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                })
                .show();
    }

}