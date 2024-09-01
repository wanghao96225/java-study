package com.wh.creationalpatterns.prototypepatterns.shallowcopy;

/**
 * 23种设计模式 - 原型模式 - 浅拷贝
 */
public class Test {

    /*
        原型模式应用场景
        对象的创建非常复杂，可以使用原型模式快捷的创建对象。
        性能和安全要求比较高。
     */

    public static void main(String[] args) throws CloneNotSupportedException {

        Certificate certificate = new Certificate();
        certificate.setName("张三");

        // 浅拷贝
        Certificate certificate1 = (Certificate) certificate.clone();
        certificate1.setName("李四");

        certificate.show();
        certificate1.show();
    }
}
