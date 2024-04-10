package com.wh.creationalpatterns.factorypatterns.coffee;

/**
 * 简单工厂 - 咖啡接口
 */
public interface Coffee {

    /**
     * 获取咖啡名称
     * @return
     */
    String getName();

    /**
     * 加糖
     * @param sweetness
     */
    void addSugar(int sweetness);

    /**
     * 加奶
     * @param milk
     */
    void addMilk(int milk);

}
