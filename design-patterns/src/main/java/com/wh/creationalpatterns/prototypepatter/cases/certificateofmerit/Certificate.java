package com.wh.creationalpatterns.prototypepatter.cases.certificateofmerit;

public class Certificate implements Cloneable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void show(){
        System.out.println(name + "同学:获得奖状");
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
