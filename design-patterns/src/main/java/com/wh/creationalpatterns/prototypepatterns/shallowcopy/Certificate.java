package com.wh.creationalpatterns.prototypepatterns.shallowcopy;

/**
 * 23种设计模式 - 原型模式 - 浅拷贝
 */
public class Certificate implements Cloneable {

    // 姓名
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // 展示
    public void show(){
        System.out.println(name + "同学:获得奖状");
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
