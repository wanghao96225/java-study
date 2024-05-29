package com.wh.other;

import java.util.List;

public class StreamApiStudy {

    public static void main(String[] args) {

        /*
            在一串数字中找出最大的偶数
            方法1.for循环遍历if比较
            方法2.stream api流筛选
         */

        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        // 方法1
//        Integer temp = 0;
//        for (Integer integer : integers) {
//
//            if (integer % 2 == 0) {
//                temp = integer >= temp ? integer : temp;
//            }
//        }
//        System.out.println(temp);

        // 方法2
        integers.stream().filter(integer -> integer % 2 == 0).max(Integer::compareTo).ifPresent(System.out::println);
    }
}
