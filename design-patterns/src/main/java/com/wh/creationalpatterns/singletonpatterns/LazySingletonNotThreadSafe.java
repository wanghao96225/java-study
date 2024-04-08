package com.wh.creationalpatterns.singletonpatterns;

/**
 * 23种设计模式 - 创建者模式 - 单例模式
 */
public class LazySingletonNotThreadSafe {

    /**
     * 懒汉单例 - 1.线程不安全
     */

    // 私有化构造函数，防止外部类实例化
    private LazySingletonNotThreadSafe() {
    }

    // 创建单例对象实例
    private static LazySingletonNotThreadSafe instance = null;

    // 获取单例对象实例
    public static LazySingletonNotThreadSafe getInstance() {
        // 如果实例为空，则创建新的单例对象
        if (instance == null) {
            instance = new LazySingletonNotThreadSafe();
            System.out.println("LazySingletonNotThreadSafe - 懒汉单例 - 线程不安全");
        }
        return instance;
    }
}
