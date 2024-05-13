package com.wh.creationalpatterns.factorypatterns.dessert;

public class Tiramisu implements Dessert{

    /**
     * 获取甜点的名称
     * @return
     */
    @Override
    public String getName() {
        return "Tiramisu - 提拉米苏";
    }

    /**
     * 获取甜点的简介
     */
    @Override
    public void showBrief() {
        System.out.println("提拉米苏简介：提拉米苏（Tiramisu）是一种著名的意大利式甜点，由泡过咖啡或兰姆酒的手指饼干，加上一层马斯卡邦尼奶酪、蛋黄及糖的混合物，然后再在蛋糕表面洒上一层咖啡粉而成。它口感细腻饱满，咖啡的苦味、可可的香气、马斯卡邦尼奶酪的丝滑和手指饼干的柔软完美融合在一起，层次丰富，令人回味无穷。");
    }
}
