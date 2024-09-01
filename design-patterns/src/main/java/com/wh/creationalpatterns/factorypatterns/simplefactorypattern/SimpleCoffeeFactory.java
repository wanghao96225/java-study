package com.wh.creationalpatterns.factorypatterns.simplefactorypattern;

import com.wh.creationalpatterns.factorypatterns.coffee.AmericanCoffee;
import com.wh.creationalpatterns.factorypatterns.coffee.Coffee;
import com.wh.creationalpatterns.factorypatterns.coffee.LatteCoffee;

/**
 * 23种设计模式 - 工厂模式 - 简单工厂模式
 */
public class SimpleCoffeeFactory {

    public Coffee createCoffee(String type) {
        Coffee coffee = null;
        if ("american".equals(type)) {
            coffee = new AmericanCoffee();
        } else if ("latte".equals(type)) {
            coffee = new LatteCoffee();
        }
        return coffee;
    }
}
