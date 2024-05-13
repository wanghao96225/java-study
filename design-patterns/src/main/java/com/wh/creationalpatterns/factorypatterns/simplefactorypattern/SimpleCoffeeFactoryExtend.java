package com.wh.creationalpatterns.factorypatterns.simplefactorypattern;

import com.wh.creationalpatterns.factorypatterns.coffee.AmericanCoffee;
import com.wh.creationalpatterns.factorypatterns.coffee.Coffee;
import com.wh.creationalpatterns.factorypatterns.coffee.LatteCoffee;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 简单工厂模式扩展
 */
public class SimpleCoffeeFactoryExtend {

    private static Map<String, Coffee> map = new HashMap<>();

    static {
        Properties properties = new Properties();
        InputStream is = Coffee.class.getClassLoader().getResourceAsStream("coffee.properties");

        try {

            properties.load(is);
            Set<Object> objects = properties.keySet();
            objects.forEach(key -> {
                String className = properties.getProperty(key.toString());
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(className);
                    Coffee coffee = (Coffee) clazz.newInstance();
                    map.put(key.toString(), coffee);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Coffee createCoffee(String type) {
        return map.get(type);
    }
}
