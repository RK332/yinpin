package com.example.yinpin.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.yinpin.R;
import com.example.yinpin.entity.Stuff;

import java.util.List;

public class StuffAdapter extends ArrayAdapter<Stuff> {
    public StuffAdapter(@NonNull Context context, int resource, @NonNull List<Stuff> objects) {
        super(context, resource, objects);
    }

    //每个子项被滚动到屏幕内的时候会被调用
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Stuff s = getItem(position);//得到当前项的 Fruit 实例

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_stuff, parent, false);

        TextView kind = view.findViewById(R.id.tv_kind);
        TextView name = view.findViewById(R.id.et_name);
        ImageView pic = view.findViewById(R.id.iv_pic);
        TextView price = view.findViewById(R.id.tv_price);

        price.setText("¥" + s.getPrice());
        kind.setText("分类：" + s.getKind());
        name.setText("名称：" + s.getName());

        Glide.with(pic).load(s.getPic()).into(pic);

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), s.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public void setData(List<Stuff> stuffList) {
        clear();
        addAll(stuffList);
        notifyDataSetChanged();
    }
}
