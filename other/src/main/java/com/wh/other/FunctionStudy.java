package com.wh.other;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FunctionStudy {
    public static void main(String[] args) {
        /*
        函数式接口分类
            1)有入参,无出参 ---- consumer(消费) ---- void accept(T t)
            2)有入参,有出参 ---- function(多功能函数) ---- R apply(T t)
                2.1)有入参,有出参 ---- predicate(判断/断言) ---- boolean test(T t)
            3)无入参,无出参 ---- runnable(普通函数) ---- void run()
            4)无入参,有出参 ---- supplier(生产) ---- T get()
         */

        Random random = new Random();
//        // Supplier生产数据
//        Supplier<String> supplier = () -> String.valueOf(random.nextInt(100) + 1);
//        // Predicate判断数据
//        Predicate<String> predicate = s -> s.matches("-?\\d+(\\.\\d+)?");
//        // Function将数据进行转换
//        Function<String, Integer> function = Integer::parseInt;
//        // Consumer消费数据
//        Consumer<Integer> consumer = num -> {
//            if (num % 2 == 0) {
//                System.out.println(num + "是偶数");
//            } else {
//                System.out.println(num + "是奇数");
//            }
//        };

        // 方法调用, 将多个函数组合成一个函数,此写法与以上注释相同
        extracted(() -> String.valueOf(random.nextInt(100)+1) , str -> str.matches("-?\\d+(\\.\\d+)?"), Integer::parseInt, num -> {
            if (num % 2 == 0) {
                System.out.println(num + "是偶数");
            } else {
                System.out.println(num + "是奇数");
            }
        });
    }


    /**
     * 一个利用函数式接口处理数据的静态方法。
     * @param supplier 提供字符串的 Supplier 接口实例，用于获取一个字符串。
     * @param predicate 用于判断字符串是否满足条件的 Predicate 接口实例。
     * @param function 将字符串转换为整数的 Function 接口实例。
     * @param consumer 消费整数的 Consumer 接口实例，用于处理函数应用结果。
     */
    private static void extracted(Supplier<String> supplier, Predicate<String> predicate, Function<String, Integer> function, Consumer<Integer> consumer) {
        // 判断字符串是否满足条件，如果满足则处理，否则输出错误信息
        if (predicate.test(supplier.get())) {
            consumer.accept(function.apply(supplier.get())); // 应用函数并消费结果
            System.out.println("输入数据是数字");
        } else {
            System.out.println("输入数据有误");
        }
    }
}
