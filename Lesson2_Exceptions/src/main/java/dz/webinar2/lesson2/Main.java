package dz.webinar2.lesson2;

/*
1. Напишите метод, на вход которого подаётся двумерный строковый массив размером 4х4,
    при подаче массива другого размера необходимо бросить исключение MyArraySizeException.
2. Далее метод должен пройтись по всем элементам массива, преобразовать в int, и просуммировать.
    Если в каком-то элементе массива преобразование не удалось (например, в ячейке лежит символ
    или текст вместо числа), должно быть брошено исключение MyArrayDataException, с детализацией
    в какой именно ячейке лежат неверные данные.
3. В методе main() вызвать полученный метод, обработать возможные исключения MySizeArrayException
    и MyArrayDataException, и вывести результат расчета.

 */

import dz.webinar2.lesson2.exceptions.MyArrayDataException;
import dz.webinar2.lesson2.exceptions.MyArraySizeException;

public class Main {
    public static void main(String[] args) {
        String [][] testArray = {
                {"1", "2", "3", "4"},
                {"5", "6", "7", "8"},
                {"9", "10", "11", "12"},
                {"13", "14", "15", "16"}
        };

        try {
            System.out.println("Сумма равна " + method (testArray));;
        } catch (MyArraySizeException e) {
            System.out.println(e.getMessage());
        } catch (MyArrayDataException e) {
            System.out.println(e.getMessage());
        }
    }

    public static int method (String [][] array) throws MyArrayDataException, MyArraySizeException {
        if (array.length != 4 || array [0].length != 4) {
            throw new MyArraySizeException(array [0].length, array.length);
        }

        int sum = 0;

        for (int y = 0; y < array.length; y++) {
            for (int x = 0; x < array [0].length; x++) {
                try {
                    sum += Integer.parseInt(array[y][x]);
                } catch (NumberFormatException e) {
                    throw new MyArrayDataException(x, y, array [y][x]);
                }
            }
        }

        return sum;
    }
}
