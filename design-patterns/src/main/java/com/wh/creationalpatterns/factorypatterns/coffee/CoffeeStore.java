package com.wh.creationalpatterns.factorypatterns.coffee;

import com.wh.creationalpatterns.factorypatterns.factorymethodfactorypatter.AmericanCoffeeFactory;
import com.wh.creationalpatterns.factorypatterns.factorymethodfactorypatter.LatteCoffeeFactory;
import com.wh.creationalpatterns.factorypatterns.simplefactorypattern.SimpleCoffeeFactory;
import com.wh.creationalpatterns.factorypatterns.simplefactorypattern.SimpleCoffeeFactoryExtend;
import com.wh.creationalpatterns.factorypatterns.staticfactorypattern.StaticCoffeeFactory;

public class CoffeeStore {

    /*
        简单工厂模式

        简单工厂模式结构
        抽象产品 ：定义了产品的规范，描述了产品的主要特性和功能。
        具体产品 ：实现或者继承抽象产品的子类
        具体工厂 ：提供了创建产品的方法，调用者通过该方法来获取产品。

        优点 ：封装了创建对象的过程，可以通过参数直接获取对象。把对象的创建和业务逻辑层分开，这样以后就避免了修改客户代码，如果要实现新产品直接修改工厂类，而不需要在原代码中修改，这样就降低了客户代码修改的可能性，更加容易扩展。
        缺点 ：增加新产品时还是需要修改工厂类的代码，违背了“开闭原则”。
     */
    public Coffee simpleOrderCoffee(String type) {
        SimpleCoffeeFactory simpleCoffeeFactory = new SimpleCoffeeFactory();
        return simpleCoffeeFactory.createCoffee(type);
    }

    /*
        简单工厂模式扩展
        可以通过工厂模式+配置文件的方式解除工厂对象和产品对象的耦合。在工厂类中加载配置文件中的全类名，并创建对象进行存储，客户端如果需要对象，直接进行获取即可。
     */
    public Coffee simpleOrderCoffeeExtend(String type) {
        return SimpleCoffeeFactoryExtend.createCoffee(type);
    }

    /*
        静态工厂模式

        抽象产品 ：定义了产品的规范，描述了产品的主要特性和功能。
        具体产品 ：实现或者继承抽象产品的子类
        具体工厂 ：提供了创建产品的方法，调用者通过该方法来获取产品。

        优点 ：封装了创建对象的过程，可以通过参数直接获取对象。把对象的创建和业务逻辑层分开，这样以后就避免了修改客户代码，如果要实现新产品直接修改工厂类，而不需要在原代码中修改，这样就降低了客户代码修改的可能性，更加容易扩展。
        缺点 ：增加新产品时还是需要修改工厂类的代码，违背了“开闭原则”。
     */
    public Coffee staticOrderCoffee(String type) {
        return StaticCoffeeFactory.createCoffee(type);
    }

    /*
        工厂方法模式

        抽象工厂（Abstract Factory）：提供了创建产品的接口，调用者通过它访问具体工厂的工厂方法来创建产品。
        具体工厂（ConcreteFactory）：主要是实现抽象工厂中的抽象方法，完成具体产品的创建。
        抽象产品（Product）：定义了产品的规范，描述了产品的主要特性和功能。
        具体产品（ConcreteProduct）：实现了抽象产品角色所定义的接口，由具体工厂来创建，它同具体工厂之间一一对应。

        优点：
        用户只需要知道具体工厂的名称就可得到所要的产品，无须知道产品的具体创建过程；
        在系统增加新的产品时只需要添加具体产品类和对应的具体工厂类，无须对原工厂进行任何修改，满足开闭原则；
        缺点：
        每增加一个产品就要增加一个具体产品类和一个对应的具体工厂类，这增加了系统的复杂度。
     */
    public Coffee factoryOrderCoffee(String type) {
        Coffee coffee = null;
        if ("american".equals(type)) {
            coffee = new AmericanCoffeeFactory().createCoffee();
        } else if ("latte".equals(type)) {
            coffee = new LatteCoffeeFactory().createCoffee();
        }
        return coffee;
    }

    public static void main(String[] args) {
        CoffeeStore coffeeStore = new CoffeeStore();
//        Coffee coffee = coffeeStore.simpleOrderCoffee("american");
//        System.out.println(coffee.getName());

        Coffee coffee = coffeeStore.simpleOrderCoffeeExtend("american");
        System.out.println(coffee.getName());
    }
}
