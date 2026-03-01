package com.example.yinpin.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.yinpin.R;
import com.example.yinpin.adapter.CartAdapter;
import com.example.yinpin.entity.Record;
import com.example.yinpin.entity.Stuff;
import com.example.yinpin.entity.User;
import com.example.yinpin.sqlite.DBCart;
import com.example.yinpin.sqlite.DBRecord;
import com.example.yinpin.sqlite.DBStuff;
import com.example.yinpin.utils.CurrentUserUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class CartFragment extends Fragment {

    public static ArrayList<Record> record = new ArrayList<>();
    public static TextView _tvTotal;
    private ListView lvCart;
    private Button btnBuy;
    private TextView etEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cart, container, false);
        bindView(v);
        initView();
        return v;
    }

    private void bindView(View v) {
        lvCart = v.findViewById(R.id.lv_cart);
        etEmpty = v.findViewById(R.id.tv_empty);
        _tvTotal = v.findViewById(R.id.tv_total);
        btnBuy = v.findViewById(R.id.btn_buy);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次Fragment可见时强制刷新数据
        if (lvCart != null) {
            set();
        }
    }

    private void initView() {
        _tvTotal.setText("0.00");
        //点击购买/结算按钮
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 检查是否选择了商品
                if (CartFragment.record.size() == 0) {
                    // 使用对话框提示
                    new AlertDialog.Builder(getContext())
                        .setTitle("提示")
                        .setMessage("请先选择要购买的商品")
                        .setPositiveButton("确定", null)
                        .show();
                    return;
                }
                
                // 计算总价并处理购买
                double total = 0;
                int successCount = 0;
                int totalCount = CartFragment.record.size();
                
                for (Record r : CartFragment.record) {
                    //添加购买记录到数据库
                    if (DBRecord.add(r)) {
                        total += Double.valueOf(r.getPrice());
                        successCount++;
                        Log.d("CartFragment", "成功添加购买记录: " + r.toString());
                        
                        //从购物车数据库中删除已经购买的商品
                        if (DBCart.del(r.getId(), r.getUsername())) {
                            Log.d("CartFragment", "成功从购物车中删除: " + r.toString());
                        }
                    } else {
                        Log.e("CartFragment", "失败添加购买记录: " + r.toString());
                    }
                }
                
                // 清空选中记录和总价
                record = new ArrayList<>();
                _tvTotal.setText("0.00");
                
                // 刷新购物车显示
                set();
                
                // 显示结算成功提示
                String message = "✓ 结算成功！\n" +
                        "购买商品：" + successCount + " 件\n" +
                        "总金额：¥" + String.format("%.2f", total) + " 元";
                
                // 使用Snackbar显示
                Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
                
                // 使用对话框确保用户看到
                new AlertDialog.Builder(getContext())
                    .setTitle("结算成功")
                    .setMessage(message)
                    .setPositiveButton("确定", null)
                    .show();
            }
        });
        set();
    }

    /**
     * 公共方法：刷新购物车数据
     */
    public void refreshCart() {
        if (lvCart != null) {
            set();
        }
    }

    /**
     * 从数据库中读取加入购物车的商品
     */
    private void set() {
        User user = CurrentUserUtils.getCurrentUser();
        ArrayList<Stuff> array = new ArrayList<>();
        //获取购物车中商品的所有id
        ArrayList<String> allId = DBCart.getLikesTitle(user.getName());
        
        //通过id依次获取商品信息，过滤掉无效的商品
        for (String s : allId) {
            try {
                Stuff stuff = DBStuff.getById(Integer.parseInt(s));
                // 如果商品存在且有效，才添加到列表
                if (stuff != null && stuff.getId() != null && !stuff.getId().equals("-1")) {
                    array.add(stuff);
                } else {
                    // 商品不存在，从购物车中删除这个无效的ID
                    Log.d("CartFragment", "商品不存在，从购物车删除: " + s);
                    DBCart.del(s, user.getName());
                }
            } catch (Exception e) {
                // ID格式错误或其他异常，从购物车中删除
                Log.e("CartFragment", "获取商品信息失败: " + s, e);
                DBCart.del(s, user.getName());
            }
        }
        
        // 更新空购物车提示
        if (array.size() == 0) {
            etEmpty.setVisibility(View.VISIBLE);
        } else {
            etEmpty.setVisibility(View.GONE);
        }
        
        for (Stuff t : array) {
            Log.d("CartFragment", "购物车商品: " + t.toString());
        }
        CartAdapter adapter = new CartAdapter(getContext(), R.layout.item_cart, array);
        adapter.setOnDeleteListener(new CartAdapter.OnDeleteListener() {
            @Override
            public void onDelete(int position, Stuff stuff) {
                if (DBCart.del(stuff.getId(), user.getName())) {
                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    // 如果该商品在选中列表中，也要移除
                    int i = 0;
                    for (Record r : record) {
                        if (r.getId().equals(stuff.getId())) {
                            record.remove(i);
                            // 更新总价
                            double total = Double.parseDouble(_tvTotal.getText().toString());
                            total -= Double.parseDouble(r.getPrice());
                            _tvTotal.setText(String.format("%.2f", total));
                            break;
                        }
                        i++;
                    }
                    refreshCart();
                } else {
                    Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lvCart.setAdapter(adapter);
        lvCart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i < array.size()) {
                    String s = array.get(i).getName();
                    Toast.makeText(getContext(), "name：" + s, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}