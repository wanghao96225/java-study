package com.wh.creationalpatterns.factorypatterns.staticfactorypattern;

import com.wh.creationalpatterns.factorypatterns.coffee.AmericanCoffee;
import com.wh.creationalpatterns.factorypatterns.coffee.Coffee;
import com.wh.creationalpatterns.factorypatterns.coffee.LatteCoffee;

/**
 * 23种设计模式 - 工厂模式 - 静态工厂模式
 */
public class StaticCoffeeFactory {

    public static Coffee createCoffee(String type) {
        Coffee coffee = null;
        if ("american".equals(type)) {
            coffee = new AmericanCoffee();
        } else if ("latte".equals(type)) {
            coffee = new LatteCoffee();
        }
        return coffee;
    }
}
