package com.handsometaoa;

import com.handsometaoa.snowflake.SnowflakeImpl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        Executor executor = Executors.newFixedThreadPool(20);
        IdGen idGen = new SnowflakeImpl(123);

        for (int i = 0; i < 20; i++) {
            executor.execute(() -> {
                for (int j = 0; j < 1000; j++) {
                    System.out.println(idGen.getId());
                }
            });
        }
    }

}
