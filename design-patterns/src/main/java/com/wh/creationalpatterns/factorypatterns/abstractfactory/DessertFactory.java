package com.wh.creationalpatterns.factorypatterns.abstractfactory;

import com.wh.creationalpatterns.factorypatterns.coffee.Coffee;
import com.wh.creationalpatterns.factorypatterns.dessert.Dessert;

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
