package com.wh.creationalpatterns.factorypatterns.factorymethodfactorypatter;

import com.wh.creationalpatterns.factorypatterns.coffee.Coffee;
import com.wh.creationalpatterns.factorypatterns.coffee.LatteCoffee;

public class LatteCoffeeFactory implements CoffeeFactory{
    @Override
    public Coffee createCoffee() {
        return new LatteCoffee();
    }
}
