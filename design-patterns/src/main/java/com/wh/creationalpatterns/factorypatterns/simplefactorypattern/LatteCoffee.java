package com.wh.creationalpatterns.factorypatterns.simplefactorypattern;

/**
 * 简单工厂模式 - 拿铁咖啡
 */
public class LatteCoffee implements Coffee{
    @Override
    public String getName() {
        return "LatteCoffee - 拿铁咖啡";
    }

    @Override
    public void addSugar() {
        System.out.println("LatteCoffee - 加糖");
    }

    @Override
    public void addMilk() {
        System.out.println("LatteCoffee - 加奶");
    }
}
