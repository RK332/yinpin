package com.example.yinpin;

import android.app.Application;
import android.util.Log;

import com.example.yinpin.entity.Stuff;
import com.example.yinpin.sqlite.DBStuff;
import com.example.yinpin.utils.SqliteUtils;

import java.util.List;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate 开始");
        
        // 如有需要，先将旧数据库 flower_shop.db 中的数据迁移到新的 YinPin.db
        SqliteUtils.migrateOldDatabaseIfNeeded();

        // 确保新数据库已经初始化（这会触发onUpgrade如果版本升级）
        SqliteUtils.getInstance().getReadableDatabase();
        Log.d(TAG, "新数据库已初始化");
        

        DBStuff.deleteAll();
        Log.d(TAG, "已清除所有旧数据");
        
        // 初始化饮品数据
        initDrinkData();
        
        // 验证数据是否插入成功
        List<Stuff> allStuff = DBStuff.getAll();
        Log.d(TAG, "数据初始化完成，当前商品数量: " + allStuff.size());
        for (Stuff stuff : allStuff) {
            Log.d(TAG, "商品: " + stuff.getName() + ", 分类: " + stuff.getKind() + ", 价格: " + stuff.getPrice());
            // 检查是否有旧的汉堡数据残留
            if (stuff.getName().contains("堡") || stuff.getKind().contains("堡")) {
                Log.e(TAG, "警告：发现旧的数据残留！");
            }
        }
        
        // 如果数据为空，再次尝试初始化
        if (allStuff.isEmpty()) {
            Log.w(TAG, "数据为空，重新初始化");
            initDrinkData();
        }
    }
    
    private void initDrinkData() {
        Stuff pearlMilkTea = new Stuff("经典珍珠奶茶", "经典饮品无需多言，Q弹的珍珠搭配香浓的奶茶，口感层次丰富。精选优质茶叶，配以新鲜牛奶，甜度适中，每一口都是满满的幸福感，非常经典的产品", "奶茶类", "18");
        boolean result1 = DBStuff.add(pearlMilkTea);
        Log.d(TAG, "插入经典珍珠奶茶: " + (result1 ? "成功" : "失败"));

        Stuff mangoSmoothie = new Stuff("芒果冰沙", "新鲜芒果制作，口感清爽，果香浓郁。冰沙细腻，入口即化，夏日必备的清凉饮品，让你瞬间感受到热带风情", "冷饮类", "12");
        boolean result2 = DBStuff.add(mangoSmoothie);
        Log.d(TAG, "插入芒果冰沙: " + (result2 ? "成功" : "失败"));

        Stuff lemonHoneyTea = new Stuff("柠檬蜂蜜茶", "新鲜柠檬搭配天然蜂蜜，酸甜适中，口感清新。富含维生素C，具有美容养颜的功效，是健康饮品的首选，性价比超高!", "茶类", "17");
        boolean result3 = DBStuff.add(lemonHoneyTea);
        Log.d(TAG, "插入柠檬蜂蜜茶: " + (result3 ? "成功" : "失败"));

        Stuff matchaLatte = new Stuff("抹茶拿铁", "精选日式抹茶粉，搭配香浓牛奶，口感丝滑。抹茶的清香与牛奶的醇厚完美融合，层次分明，每一口都能感受到浓郁的茶香，非常经典的产品", "咖啡类", "16");
        boolean result4 = DBStuff.add(matchaLatte);
        Log.d(TAG, "插入抹茶拿铁: " + (result4 ? "成功" : "失败"));

        Stuff strawberryMilkshake = new Stuff("草莓奶昔", "新鲜草莓制作，口感浓郁，果香四溢。奶昔顺滑细腻，甜度适中，是甜品爱好者的最爱。一口下去，满满的草莓味，真的特别满足", "奶昔类", "20");
        boolean result5 = DBStuff.add(strawberryMilkshake);
        Log.d(TAG, "插入草莓奶昔: " + (result5 ? "成功" : "失败"));

        Stuff icedAmericano = new Stuff("冰美式咖啡", "精选咖啡豆现磨制作，口感醇厚，回味悠长。冰美式清爽解腻，是咖啡爱好者的首选。咖啡的苦味与冰块的清凉完美结合，中规中矩的经典选择", "咖啡类", "13");
        boolean result6 = DBStuff.add(icedAmericano);
        Log.d(TAG, "插入冰美式咖啡: " + (result6 ? "成功" : "失败"));
    }
}
