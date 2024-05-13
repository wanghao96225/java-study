package com.wh.creationalpatterns.factorypatterns.abstractfactory;

import com.wh.creationalpatterns.factorypatterns.coffee.Coffee;
import com.wh.creationalpatterns.factorypatterns.coffee.LatteCoffee;
import com.wh.creationalpatterns.factorypatterns.dessert.Dessert;
import com.wh.creationalpatterns.factorypatterns.dessert.Tiramisu;

public class ItalyDessertFactory implements DessertFactory{

    /**
     * 创建意大利咖啡
     * @return
     */
    @Override
    public Coffee createCoffee() {
        return new LatteCoffee();
    }

    /**
     * 创建意大利甜点
     * @return
     */
    @Override
    public Dessert createDessert() {
        return new Tiramisu();
    }
}
