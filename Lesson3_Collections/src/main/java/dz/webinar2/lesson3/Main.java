package dz.webinar2.lesson3;

/*
1. Создать массив с набором слов (10-20 слов, должны встречаться повторяющиеся). Найти и вывести
    список уникальных слов, из которых состоит массив (дубликаты не считаем). Посчитать сколько
    раз встречается каждое слово.
2. Написать простой класс ТелефонныйСправочник, который хранит в себе список фамилий и телефонных
    номеров. В этот телефонный справочник с помощью метода add() можно добавлять записи. С помощью
    метода get() искать номер телефона по фамилии. Следует учесть, что под одной фамилией может быть
    несколько телефонов (в случае однофамильцев), тогда при запросе такой фамилии должны выводиться
    все телефоны.
 */

import java.util.*;

public class Main {
    public static void main(String[] args) {
        {//Задание 1
            String[] strings = {
                    "Geekbrains",
                    "Java",
                    "Барсик",
                    "Барсук",
                    "Матрёшка",
                    "Collection",
                    "Шахматы",
                    "ДВС",
                    "R2D2",
                    "Барсик",
                    "Звезда",
                    "Collection",
                    "Java",
                    "Java",
                    "Java",
                    "Java",
                    "Java",
                    "Барсик",
            };

            LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();
            Integer tempInt;
            for (String str : strings) {
                if ((tempInt = linkedHashMap.put(str, 1)) != null) {
                    linkedHashMap.put(str, ++tempInt);
                }
            }

            System.out.println("\"Слова из массива\" -> Сколько раз встречается в массиве:");
            for (Map.Entry<String, Integer> pair : linkedHashMap.entrySet()) {
                System.out.println("\"" + pair.getKey() + "\" -> " + pair.getValue());
            }
        }//Конец задания 1

        System.out.println();

        {//Задание 2
            PhoneBook book = new PhoneBook();
            book.add("Петров", 87895625855L);
            book.add("Иванов", 89256542345L);
            book.add("Иванов", 89335582345L);
            book.add("Иванов", 89256542311L);//----\
            book.add("Иванов", 89256542311L);//----/ Одинаковые номера
            book.add("Сидоров", 89746542345L);
            book.add("Сидоров", 89206542345L);

            System.out.println(book.get("Петров"));
            System.out.println(book.get("Сидоров"));
            System.out.println(book.get("Бобров"));//<- Такого нет в книге
            System.out.println();

            System.out.println(book);//Вывод всей книги
        }//Конец задания 2
    }
}
