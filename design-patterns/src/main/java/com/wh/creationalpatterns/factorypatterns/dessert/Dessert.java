package com.wh.creationalpatterns.factorypatterns.dessert;

public interface Dessert {

    /**
     * 获取甜点名称
     * @return
     */
    String getName();

    /**
     * 显示甜点的简介
     */
    void showBrief();
}
