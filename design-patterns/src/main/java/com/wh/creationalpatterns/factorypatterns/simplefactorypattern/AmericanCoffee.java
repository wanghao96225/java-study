package com.wh.creationalpatterns.factorypatterns.simplefactorypattern;

/**
 * 简单工厂模式 - 美式咖啡
 */
public class AmericanCoffee implements Coffee{
    @Override
    public String getName() {
        return "AmericanCoffee - 美式咖啡";
    }

    @Override
    public void addSugar() {
        System.out.println("AmericanCoffee - 加糖");
    }

    @Override
    public void addMilk() {
        System.out.println("AmericanCoffee - 加奶");
    }
}
