package com.wh.creationalpatterns.factorypatterns.simplefactorypattern;

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
     */
    void addSugar();

    /**
     * 加奶
     */
    void addMilk();

}
