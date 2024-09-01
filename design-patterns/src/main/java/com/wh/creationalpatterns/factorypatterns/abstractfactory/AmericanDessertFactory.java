package com.wh.creationalpatterns.factorypatterns.abstractfactory;

import com.wh.creationalpatterns.factorypatterns.coffee.AmericanCoffee;
import com.wh.creationalpatterns.factorypatterns.coffee.Coffee;
import com.wh.creationalpatterns.factorypatterns.dessert.Dessert;
import com.wh.creationalpatterns.factorypatterns.dessert.MatchaMousse;

/**
 * 23种设计模式 - 工厂模式 - 抽象工厂 - 美式甜点工厂
 */
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
