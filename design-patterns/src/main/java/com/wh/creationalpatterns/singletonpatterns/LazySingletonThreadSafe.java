package com.wh.creationalpatterns.singletonpatterns;

/**
 * 23种设计模式 - 创建者模式 - 单例模式
 */
public class LazySingletonThreadSafe {

    /**
     * 懒汉单例 - 2.线程安全
     */

    // 私有化构造函数，防止外部类实例化
    private LazySingletonThreadSafe() {
    }

    // 创建单例对象实例
    private static LazySingletonThreadSafe instance = null;

    // 获取单例对象实例
    public static synchronized LazySingletonThreadSafe getInstance() {
        // 如果实例为空，则创建新的单例对象
        if (instance == null) {
            instance = new LazySingletonThreadSafe();
            System.out.println("LazySingletonThreadSafe - 懒汉单例 - 线程安全");
        }
        return instance;
    }
}
