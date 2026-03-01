package com.example.yinpin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.yinpin.R;
import com.example.yinpin.entity.Record;
import com.example.yinpin.entity.Stuff;
import com.example.yinpin.entity.User;
import com.example.yinpin.fragment.CartFragment;
import com.example.yinpin.sqlite.DBCart;
import com.example.yinpin.utils.CurrentUserUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends ArrayAdapter<Stuff> {

    private final User user;
    private OnDeleteListener onDeleteListener;
    private Map<String, Integer> quantityMap; // 存储每个商品的数量

    public interface OnDeleteListener {
        void onDelete(int position, Stuff stuff);
    }

    public CartAdapter(@NonNull Context context, int resource, @NonNull List<Stuff> objects) {
        super(context, resource, objects);
        user = CurrentUserUtils.getCurrentUser();
        quantityMap = new HashMap<>();
        // 初始化数量映射
        loadQuantities();
    }

    private void loadQuantities() {
        quantityMap = DBCart.getCartWithQuantity(user.getName());
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        this.onDeleteListener = listener;
    }

    //每个子项被滚动到屏幕内的时候会被调用
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Stuff s = getItem(position);//得到当前项的 Stuff 实例

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_cart, parent, false);

        TextView title = view.findViewById(R.id.tv_title);
        TextView price = view.findViewById(R.id.tv_price);
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        Button btnDecrease = view.findViewById(R.id.btn_decrease);
        Button btnIncrease = view.findViewById(R.id.btn_increase);
        ImageView pic = view.findViewById(R.id.iv_pic);
        CheckBox box = view.findViewById(R.id.box);
        ImageView ivDelete = view.findViewById(R.id.iv_delete);
        
        // 检查商品数据是否有效
        if (s != null && s.getName() != null) {
            title.setText(s.getName());
        } else {
            title.setText("商品已失效");
        }
        
        if (s != null && s.getPrice() != null && !s.getPrice().isEmpty()) {
            price.setText("¥" + s.getPrice());
        } else {
            price.setText("¥0.00");
        }

        // 获取并显示数量
        int quantity = quantityMap.getOrDefault(s.getId(), 1);
        tvQuantity.setText(String.valueOf(quantity));

        Glide.with(pic).load(s.getPic()).into(pic);

        // 减少数量按钮
        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(tvQuantity.getText().toString());
                if (currentQuantity > 1) {
                    int newQuantity = currentQuantity - 1;
                    if (DBCart.updateQuantity(s.getId(), user.getName(), newQuantity)) {
                        tvQuantity.setText(String.valueOf(newQuantity));
                        quantityMap.put(s.getId(), newQuantity);
                        
                        // 如果该商品被选中，更新总价
                        if (box.isChecked()) {
                            updateTotalPrice(s, -1);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "数量不能少于1，如需删除请点击删除按钮", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 增加数量按钮
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(tvQuantity.getText().toString());
                int newQuantity = currentQuantity + 1;
                if (DBCart.updateQuantity(s.getId(), user.getName(), newQuantity)) {
                    tvQuantity.setText(String.valueOf(newQuantity));
                    quantityMap.put(s.getId(), newQuantity);
                    
                    // 如果该商品被选中，更新总价
                    if (box.isChecked()) {
                        updateTotalPrice(s, 1);
                    }
                }
            }
        });

        // 删除按钮点击事件
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteListener != null) {
                    onDeleteListener.onDelete(position, s);
                }
            }
        });

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), s.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        
        /*
        点击多选框，把此商品的价格（乘以数量）加去或减去，显示在购物车页面的总价total中
        添加或删除，商品到购买记录中。
        */
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int currentQuantity = Integer.parseInt(tvQuantity.getText().toString());
                double itemPrice = Double.parseDouble(s.getPrice());
                double total = Double.parseDouble(CartFragment._tvTotal.getText().toString());
                
                if (box.isChecked()) {
                    // 选中时，根据数量添加多条记录（或者修改Record类支持数量）
                    for (int i = 0; i < currentQuantity; i++) {
                        Record r = new Record(user.getName(), s.getId(), s.getName(), s.getPrice(), user.getAddress());
                        CartFragment.record.add(r);
                    }
                    total += itemPrice * currentQuantity;
                } else {
                    // 取消选中时，移除该商品的所有记录
                    CartFragment.record.removeIf(r -> r.getId().equals(s.getId()));
                    total -= itemPrice * currentQuantity;
                }
                CartFragment._tvTotal.setText(String.format("%.2f", total));
            }
        });
        return view;
    }

    /**
     * 更新总价（当数量变化且商品被选中时）
     */
    private void updateTotalPrice(Stuff stuff, int quantityChange) {
        double total = Double.parseDouble(CartFragment._tvTotal.getText().toString());
        double itemPrice = Double.parseDouble(stuff.getPrice());
        total += itemPrice * quantityChange;
        CartFragment._tvTotal.setText(String.format("%.2f", total));
        
        // 同步更新record列表
        if (quantityChange > 0) {
            // 增加数量，添加记录
            Record r = new Record(user.getName(), stuff.getId(), stuff.getName(), stuff.getPrice(), user.getAddress());
            CartFragment.record.add(r);
        } else {
            // 减少数量，移除一条记录
            for (int i = 0; i < CartFragment.record.size(); i++) {
                if (CartFragment.record.get(i).getId().equals(stuff.getId())) {
                    CartFragment.record.remove(i);
                    break;
                }
            }
        }
    }

}

