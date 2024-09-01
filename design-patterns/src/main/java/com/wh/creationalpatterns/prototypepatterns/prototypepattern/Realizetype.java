package com.wh.creationalpatterns.prototypepatterns.prototypepattern;

/**
 * 23种设计模式 - 原型模式 - 原型类
 */
public class Realizetype implements Cloneable{

    /**
     * 无参构造方法
     */
    public Realizetype(){
        System.out.println("具体原型创建成功！");
    }

    /**
     * 克隆方法
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    protected Object clone() throws CloneNotSupportedException{
        System.out.println("具体原型复制成功！");
        return super.clone();
    }
}
