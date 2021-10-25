package dz.webinar2.lesson2.exceptions;

public class MyArrayDataException extends Exception {
    private static final String MSG = "Неправильные данные в массиве. Ячейка (%d, %d) = '%s'";

    public MyArrayDataException(int x, int y, String s) {
        super (String.format(MSG, x, y, s));
    }
}
