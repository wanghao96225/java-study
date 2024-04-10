package com.wh.creationalpatterns.factorypatterns.coffee;

/**
 * 简单工厂模式 - 拿铁咖啡
 */
public class LatteCoffee implements Coffee {
    @Override
    public String getName() {
        return "LatteCoffee - 拿铁咖啡";
    }

    @Override
    public void addSugar(int sweetness) {
        System.out.println("LatteCoffee - 加 " + sweetness + "% 糖");
    }

    @Override
    public void addMilk(int milk) {
        System.out.println("LatteCoffee - 加 " + milk + "% 奶");
    }
}
