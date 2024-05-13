package com.wh.creationalpatterns.factorypatterns.abstractfactory;

import com.wh.creationalpatterns.factorypatterns.coffee.AmericanCoffee;
import com.wh.creationalpatterns.factorypatterns.coffee.Coffee;
import com.wh.creationalpatterns.factorypatterns.dessert.Dessert;
import com.wh.creationalpatterns.factorypatterns.dessert.MatchaMousse;

public class AmericanDessertFactory implements DessertFactory{

    /**
     * 创建美式甜点
     * @return
     */
    @Override
    public Coffee createCoffee() {
        return new AmericanCoffee();
    }

    /**
     * 创建美式甜点
     * @return
     */
    @Override
    public Dessert createDessert() {
        return new MatchaMousse();
    }
}
