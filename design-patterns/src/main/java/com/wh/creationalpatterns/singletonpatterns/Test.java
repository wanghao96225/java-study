package com.wh.creationalpatterns.singletonpatterns;

/**
 *
 */
public class Test {

    public static void main(String[] args) {

        /*
            单例模式的结构
            单例模式的主要有以下角色：
            单例类。只能创建一个实例的类
             访问类。使用单例类

            单例模式的实现
            单例设计模式分类两种：
            饿汉式：类加载就会导致该单实例对象被创建
            懒汉式：类加载不会导致该单实例对象被创建，而是首次使用该对象时才会创建
         */

        /*
            饿汉单例 - 1.静态变量创建类的对象
            说明：该方式在成员位置声明Singleton类型的静态变量，并创建Singleton类的对象instance。instance对象是随着类的加载而创建的。如果该对象足够大的话，而一直没有使用就会造成内存的浪费。
         */
        HungrySingletonStaticVariable.getInstance();
        /*
            饿汉单例 - 2.静态代码块方式
            说明：该方式在成员位置声明Singleton类型的静态变量，而对象的创建是在静态代码块中，也是对着类的加载而创建。所以和饿汉式的静态变量创建类的对象基本上一样，当然该方式也存在内存浪费问题。
         */
        HungrySingletonStaticBlock.getInstance();

        /*
            懒汉单例 - 1.线程不安全
            从上面代码我们可以看出该方式在成员位置声明Singleton类型的静态变量，并没有进行对象的赋值操作，那么什么时候赋值的呢？
            当调用getInstance()方法获取Singleton类的对象的时候才创建Singleton类的对象，这样就实现了懒加载的效果。
            但是，如果是多线程环境，会出现线程安全问题。

            解释下为什么会存在线程问题：
            假设现在有两个线程t1和t2同时调用getInstance() 方法，t1刚判断instance == null为true进入内部吗，刚要进行创建，
            此时正好丢失CPU执行权，进入阻塞状态，而t2获得CPU执行权，也进行了instance == null判断，发现也是true，
            就进入了内部代码进行创建，此时我们发现t1和t2都会执行内部的new代码，从而获得了两个不同的实例对象。
         */
        LazySingletonNotThreadSafe.getInstance();

        /*
            懒汉单例 - 2.线程安全
            该方式也实现了懒加载效果，同时又解决了线程安全问题。但是在getInstance()方法上添加了synchronized关键字，导致该方法的执行效果特别低。从上面代码我们可以看出，其实就是在初始化instance的时候才会出现线程安全问题，一旦初始化完成就不存在了。
         */
        LazySingletonThreadSafe.getInstance();

        /*
            懒汉单例 - 3.双重检查
            双重检查锁模式是一种非常好的单例实现模式，解决了单例、性能、线程安全问题，上面的双重检测锁模式看上去完美无缺，其实是存在问题，在多线程的情况下，可能会出现空指针问题，出现问题的原因是JVM在实例化对象的时候会进行优化和指令重排序操作。
            要解决双重检查锁模式带来空指针异常的问题，只需要使用 volatile 关键字, volatile 关键字可以保证可见性和有序性。
         */
        LazySingletonThreadSafeDoubleCheck.getInstance();
    }
}
