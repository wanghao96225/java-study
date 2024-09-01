package com.wh.creationalpatterns.prototypepatterns.deepcopy;

/**
 * 23种设计模式 - 原型模式 - 深拷贝 - 学生类
 */
public class Student {

    /**
     * 姓名
     */
    private String name;

    /**
     * 地址
     */
    private String address;

    // 构造方法
    public Student(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
