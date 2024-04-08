package com.wh.creationalpatterns.singletonpatterns;

/**
 * 23种设计模式 - 创建者模式 - 单例模式
 */
public class HungrySingletonStaticBlock {

    /**
     * 饿汉单例 - 2.静态代码块方式
     */

    // 私有化构造函数，防止外部类实例化
    private HungrySingletonStaticBlock() {
    }

    // 创建私有静态实例，并在静态代码块中初始化
    private static HungrySingletonStaticBlock instance = new HungrySingletonStaticBlock();

    // 静态代码块，用于在类加载时执行初始化操作
    static {
        System.out.println("HungrySingletonStaticBlock - 饿汉单例 - 静态代码块");
    }

    // 获取该类的单例实例
    public static HungrySingletonStaticBlock getInstance() {
        return instance;
    }
}
