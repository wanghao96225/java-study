package com.wh.creationalpatterns.prototypepatterns.deepcopy;

import java.io.*;

public class Test {

    /*
        浅拷贝不会针对非基本类型进行拷贝，只会仍指向原有属性所指向的对象的内存地址。

        stu对象和stu1对象是同一个对象，就会产生将stu1对象中name属性值改为“李四”，
        两个Citation（奖状）对象中显示的都是李四。这就是浅克隆的效果，
        对具体原型类（Citation）中的引用类型的属性进行引用的复制。这种情况需要使用深克隆，
        而进行深克隆需要使用对象流。
     */

    public static void main(String[] args) throws CloneNotSupportedException, IOException, ClassNotFoundException {
//        Citation citation = new Citation();
//        Student stu = new Student("张三", "西安");
//        citation.setStu(stu);
//
//        //复制奖状
//        Citation citation1 = citation.clone();
//
//        //获取c2奖状所属学生对象
//        Student stu1 = citation1.getStu();
//        stu1.setName("李四");
//
//        //判断stu对象和stu1对象是否是同一个对象
//        System.out.println("stu和stu1是同一个对象？" + (stu == stu1));
//
//        citation.show();
//        citation1.show();

        // 深拷贝
        deepCopy();
    }

    // 深拷贝
    public static void deepCopy() throws IOException, ClassNotFoundException {
        Citation citation = new Citation();
        Student stu = new Student("张三", "西安");
        citation.setStu(stu);

        //创建对象输出流对象
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("C:\\Users\\Think\\Desktop\\b.txt"));
        //将citation对象写出到文件中
        oos.writeObject(citation);
        oos.close();

        //创建对象出入流对象
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("C:\\Users\\Think\\Desktop\\b.txt"));
        //读取对象
        Citation citation1 = (Citation) ois.readObject();
        //获取citation1奖状所属学生对象
        Student stu1 = citation1.getStu();
        stu1.setName("李四");

        //判断stu对象和stu1对象是否是同一个对象
        System.out.println("stu和stu1是同一个对象？" + (stu == stu1));

        citation.show();
        citation1.show();
    }

}
