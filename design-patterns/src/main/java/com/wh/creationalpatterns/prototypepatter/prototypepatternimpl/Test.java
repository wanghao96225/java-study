package com.wh.creationalpatterns.prototypepatter.prototypepatternimpl;

public class Test {

    /*
        原型模式的克隆分为浅克隆和深克隆。
        浅克隆：创建一个新对象，新对象的属性和原来对象完全相同，对于非基本类型属性，仍指向原有属性所指向的对象的内存地址。
        深克隆：创建一个新对象，属性中引用的其他对象也会被克隆，不再指向原有对象地址。

        Java中的Object类中提供了 clone() 方法来实现浅克隆。 Cloneable 接口是上面的类图中的抽象原型类，
        而实现了Cloneable接口的子实现类就是具体的原型类。

        输出结果是false，表明这个r1和r2不是同一个对象，并且只有在r1创建时Realizetype的构造方法执行了一次，
        调用clone方法时并不会触发构造方法，而是触发实现的clone方法。
     */

    public static void main(String[] args) throws CloneNotSupportedException {

        Realizetype r1 = new Realizetype();
        Realizetype r2 = (Realizetype) r1.clone();

        System.out.println("对象r1和r2是同一个对象？" + (r1 == r2));
    }
}
