package com.wh.creationalpatterns.singletonpatterns;

/**
 * 23种设计模式 - 创建者模式 - 单例模式
 */
public class LazySingletonThreadSafeDoubleCheck {

    /**
     * 懒汉单例 - 2.线程安全 - 双重检查
     */

    // 私有化构造函数，防止外部类实例化
    private LazySingletonThreadSafeDoubleCheck() {
    }

    // 创建单例对象实例
    private static volatile LazySingletonThreadSafeDoubleCheck instance = null;

    // 获取单例对象实例
    public static LazySingletonThreadSafeDoubleCheck getInstance() {
        //  // 如果实例还未创建，则进入同步块
        if (instance == null) {
            synchronized (LazySingletonThreadSafeDoubleCheck.class) {
                // // 在同步块内再次检查实例是否存在，避免不必要的锁定
                if (instance == null) {
                    instance = new LazySingletonThreadSafeDoubleCheck();
                }
            }
        }
        return instance;
    }
}
