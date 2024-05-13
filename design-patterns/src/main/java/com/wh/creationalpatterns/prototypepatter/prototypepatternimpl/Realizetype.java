package com.wh.creationalpatterns.prototypepatter.prototypepatternimpl;

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
