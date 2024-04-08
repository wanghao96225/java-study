package com.wh.creationalpatterns.singletonpatterns;

/**
 * 23种设计模式 - 创建者模式 - 单例模式
 */
public class HungrySingletonStaticVariable {

    /**
     * 饿汉单例 - 1.静态变量创建类的对象
     */

    // 定义一个私有的构造函数，该构造函数不能被外部类调用
    private HungrySingletonStaticVariable() {
    }

    // 定义一个私有的静态实例变量 instance，用于保存 SingletonStaticVariable 类的单例实例
    private static HungrySingletonStaticVariable instance = new HungrySingletonStaticVariable();

    // 定义一个公有的静态方法 getInstance，用于获取 SingletonStaticVariable 类的单例实例
    public static HungrySingletonStaticVariable getInstance() {
        System.out.println("HungrySingletonStaticVariable - 饿汉单例 - 静态变量创建类的对象");
        return instance;
    }

}
