package dz.webinar2.lesson2.exceptions;

public class MyArraySizeException extends Exception {
    private static final String MSG = "Неправильный размер массива. Ожидается массив 4х4. Получен массив %dx%d";

    public MyArraySizeException(int x, int y) {
        super(String.format(MSG, x, y));
    }
}
