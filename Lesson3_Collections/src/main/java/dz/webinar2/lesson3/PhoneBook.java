package dz.webinar2.lesson3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class PhoneBook {
    private final LinkedHashMap <String, ArrayList<Long>> book = new LinkedHashMap<>();

    public void add (String surname, Long phone) {
        //Здесь можно выполнить проверку на правильность фамилии и номера
        if (surname == null || phone == null) {
            return;
        }
        addOne(surname, phone);
    }

    private void addOne (String surname, Long phone) {
        ArrayList <Long> tempList = book.get (surname);

        if (tempList == null) {
            tempList = new ArrayList<>(2);
            tempList.add (phone);
            book.put (surname, tempList);
        } else {
            if (!tempList.contains(phone)) {
                tempList.add (phone);
                book.put (surname, tempList);
            }
        }
    }

    public String get (String surname) {
        StringBuilder result = new StringBuilder("");
        ArrayList <Long> tempList;

        if (surname == null || (tempList = book.get (surname)) == null) {
            return "Такой фамилии нет в справочнике";
        } else {
            result.append(surname + ":\n");
            for (Long phone : tempList) {
                result.append("\t" + phone + "\n");
            }
        }
        return result.toString();
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer("");
        result.append("Телефонная книга:\n");
        for (Map.Entry<String, ArrayList<Long>> pair : book.entrySet()) {
            result.append("\t" + pair.getKey() + "\n");
            for (Long phone : pair.getValue()) {
                result.append("\t\t" + phone + "\n");
            }
        }
        return result.toString();
    }
}
