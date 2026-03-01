package com.example.yinpin.entity;

import com.example.yinpin.R;

public class Stuff {
    private String id;//id
    private String name;//名称
    private String title;//标题
    private String kind;//分类
    private String price;//价格
    private int pic;//图片

    public Stuff() {
        this.id="-1";
    }

    public Stuff( String name, String title, String kind, String price ) {
        this.id = "id";
        this.name = name;
        this.title = title;
        this.kind = kind;
        this.price = price;
        this.pic = 0;
    }


    public Stuff(String id, String name, String title, String kind, String price) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.kind = kind;
        this.price = price;
        switch (name){
            case "经典珍珠奶茶":
                this.pic = R.drawable.jing_dian_zhen_zhu_nai_cha;
                break;
            case "芒果冰沙":
                this.pic = R.drawable.mang_guo_bing_sha;
                break;
            case "柠檬蜂蜜茶":
                this.pic = R.drawable.ning_meng_feng_mi_cha;
                break;
            case "抹茶拿铁":
                this.pic = R.drawable.mo_cha_na_tie;
                break;
            case "草莓奶昔":
                this.pic = R.drawable.cao_mei_nai_xi;
                break;
            case "冰美式咖啡":
                this.pic = R.drawable.bing_mei_shi_ka_fei;
                break;
            default:
                this.pic=R.drawable.ic_about;
                break;
        }
    }

    @Override
    public String toString() {
        return "Stuff{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", kind='" + kind + '\'' +
                ", price='" + price + '\'' +
                ", pic=" + pic +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }
}
