package com.wh.creationalpatterns.factorypatterns.dessert;

public class MatchaMousse implements Dessert{

    /**
     * 获取甜点的名称
     * @return
     */
    @Override
    public String getName() {
        return "MatchaMousse - 抹茶慕斯";
    }

    /**
     * 显示甜点的简介
     */
    @Override
    public void showBrief() {
        System.out.println("抹茶慕斯简介：抹茶慕斯是一种常见的甜点，由抹茶、奶油、牛奶、糖、吉利丁等食材制成。它口感细腻柔滑，清香浓郁，入口即化，深受人们喜爱。");
    }
}
