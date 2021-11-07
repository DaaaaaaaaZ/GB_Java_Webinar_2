package dz.webinar2.lesson5;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        final int size = 10000000; //
        final int h = size / 2;
        float[] arr = new float[size];

        initArr(arr);
        method1 (arr);

        initArr(arr);
        method2 (arr, h);

    }

    private static void method2(float[] arr, int h) {
        float arr1 [] = new float [h];
        float arr2 [] = new float [arr.length - h];

        long start = System.currentTimeMillis();

        System.arraycopy(arr, 0, arr1, 0, h);
        System.arraycopy(arr, h, arr2, 0, h);

        MyThread t1 = new MyThread(arr1);
        MyThread t2 = new MyThread(arr2);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.arraycopy(arr1, 0, arr, 0, h);
        System.arraycopy(arr2, 0, arr, h, h);

        System.out.println("Просчёт в два потока заняло "
                + (System.currentTimeMillis() - start) / 1000f + " секунд");
    }

    private static void initArr (float [] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr [i] = 1f;
        }
    }

    private static void method1(float[] arr) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) *
                    Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
        System.out.println("Просчёт в один поток заняло "
                + (System.currentTimeMillis() - start) / 1000f + " секунд");
    }

    private static class MyThread extends Thread {
        private final float [] partArr;

        public MyThread(float [] partArr) {
            this.partArr = partArr;
        }

        @Override
        public void run() {
            for (int i = 0; i < partArr.length; i++) {
                partArr[i] = (float)(partArr[i] * Math.sin(0.2f + i / 5) *
                        Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
            }
        }
    }
}
