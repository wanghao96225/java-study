package com.wh.creationalpatterns.factorypatterns.coffee;

/**
 * 23种设计模式 - 简单工厂模式 - 美式咖啡
 */
public class AmericanCoffee implements Coffee {
    @Override
    public String getName() {
        return "AmericanCoffee - 美式咖啡";
    }

    @Override
    public void addSugar(int sweetness) {
        System.out.println("AmericanCoffee - 加 " + sweetness + "% 糖");
    }

    @Override
    public void addMilk(int milk) {
        System.out.println("AmericanCoffee - 加 " + milk + "% 奶");
    }
}
