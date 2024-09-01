package com.wh.creationalpatterns.factorypatterns.abstractfactory;

import com.wh.creationalpatterns.factorypatterns.coffee.Coffee;
import com.wh.creationalpatterns.factorypatterns.dessert.Dessert;

/**
 * 23种设计模式 - 工厂模式 - 抽象工厂
 */
public interface DessertFactory {

    /**
     * 创建咖啡
     * @return
     */
    Coffee createCoffee();

    /**
     * 创建甜点
     * @return
     */
    Dessert createDessert();
}
