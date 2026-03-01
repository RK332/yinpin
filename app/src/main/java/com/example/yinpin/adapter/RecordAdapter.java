package com.example.yinpin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.yinpin.R;
import com.example.yinpin.sqlite.DBStuff;
import com.example.yinpin.entity.Record;
import com.example.yinpin.entity.Stuff;


import java.util.List;

public class RecordAdapter extends ArrayAdapter<Record> {
    
    private OnDeleteListener onDeleteListener;
    
    public interface OnDeleteListener {
        void onDelete(int position, Record record);
    }
    
    public RecordAdapter(@NonNull Context context, int resource, @NonNull List<Record> objects) {
        super(context, resource, objects);
    }
    
    public void setOnDeleteListener(OnDeleteListener listener) {
        this.onDeleteListener = listener;
    }
    
    //每个子项被滚动到屏幕内的时候会被调用
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Record r = getItem(position);//得到当前项的 Record 实例

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_record, parent, false);

        String id = r.getId();
        Stuff stuff = DBStuff.getById(Integer.parseInt(id));

        TextView title = view.findViewById(R.id.tv_title);
        TextView address = view.findViewById(R.id.et_address);
        ImageView pic = view.findViewById(R.id.iv_pic);
        TextView price = view.findViewById(R.id.tv_price);
        ImageView ivDelete = view.findViewById(R.id.iv_delete);

        // 显示价格
        if (r.getPrice() != null && !r.getPrice().isEmpty()) {
            price.setText("¥" + r.getPrice());
        } else {
            price.setText("¥0.00");
        }

        // 显示名称 - 优先使用Record中的name，如果为空则使用Stuff的name
        String name = r.getName();
        if (name == null || name.isEmpty()) {
            if (stuff != null && stuff.getName() != null) {
                name = stuff.getName();
            } else {
                name = "商品已失效";
            }
        }
        title.setText(name);

        // 显示收货地址
        address.setText("收货地址：" + r.getAddress());

        // 加载图片
        if (stuff != null && stuff.getPic() != 0) {
            Glide.with(pic).load(stuff.getPic()).into(pic);
        } else {
            // 如果商品不存在，使用默认背景色
            pic.setImageResource(0);
            pic.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.main));
        }

        // 删除按钮点击事件
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteListener != null) {
                    onDeleteListener.onDelete(position, r);
                }
            }
        });

        return view;
    }

}
