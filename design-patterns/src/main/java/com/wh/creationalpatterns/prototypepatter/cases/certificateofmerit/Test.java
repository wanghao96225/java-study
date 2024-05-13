package com.wh.creationalpatterns.prototypepatter.cases.certificateofmerit;

public class Test {

    public static void main(String[] args) throws CloneNotSupportedException {

        Certificate certificate = new Certificate();
        certificate.setName("张三");

        Certificate certificate1 = (Certificate) certificate.clone();
        certificate1.setName("李四");

        certificate.show();
        certificate1.show();
    }
}
