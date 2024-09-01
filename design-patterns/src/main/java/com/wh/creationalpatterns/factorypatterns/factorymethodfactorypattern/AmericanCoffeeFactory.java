package com.wh.creationalpatterns.factorypatterns.factorymethodfactorypattern;

import com.wh.creationalpatterns.factorypatterns.coffee.AmericanCoffee;
import com.wh.creationalpatterns.factorypatterns.coffee.Coffee;

public class AmericanCoffeeFactory implements CoffeeFactory {

    @Override
    public Coffee createCoffee() {
        return new AmericanCoffee();
    }
}
