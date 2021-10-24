package dz.webinar2.lesson1.teams;


import dz.webinar2.lesson1.entities.Cat;
import dz.webinar2.lesson1.entities.Human;
import dz.webinar2.lesson1.entities.Robot;

/*
5. Добавить класс Team, который будет содержать: название команды, массив из 4-х участников (т.е.
        в конструкторе можно сразу всех участников указывать), метод для вывода информации о членах команды
        прошедших дистанцию, метод вывода информации обо всех членах команды.
*/
public class Team {
    private String name;
    private final static int COUNT_PARTICIPANTS = 4;
    private final Object [] participants = new Object[COUNT_PARTICIPANTS];
    private final boolean [] statusParticipants = new boolean[COUNT_PARTICIPANTS];
    private boolean isPreparedToCourse = true;
    private int curIndex = -1;

    public Team (String name) {
        this.name = name;
        for (int i = 0; i < participants.length; i++) {
            statusParticipants [i] = true;
            switch ((int)(Math.random() * 4 + 1)) {
                case 1: {
                    participants [i] = new Robot();
                } break;
                case 2: {
                    participants [i] = new Human();
                } break;
                default: {
                    participants [i] = new Cat();
                }
            }
        }
    }

    public Object obtain () {
        if (curIndex < 0 || curIndex > COUNT_PARTICIPANTS - 1) {
            curIndex = 0;
        } else {
            if (curIndex == COUNT_PARTICIPANTS - 2) {
                isPreparedToCourse = false;
            }
            curIndex++;
        }

        return participants [curIndex];
    }

    public void setResultForCurrentParticipants (boolean isGotWin) {
        statusParticipants [curIndex] = isGotWin;
    }

    public void printWinnersInfo () {
        StringBuilder result = new StringBuilder();
        if (!isPreparedToCourse) {
            result.append("Участники, прошедшие испытание: ");
            for (int i = 0; i < participants.length; i++) {
                if (participants[i] instanceof Cat) {
                    if (statusParticipants[i]) {
                        result.append(((Cat) participants[i]).getName());
                    }
                } else if (participants[i] instanceof Human) {
                    if (statusParticipants[i]) {
                        result.append(((Human) participants[i]).getName());
                    }
                } else if (participants[i] instanceof Robot) {
                    if (statusParticipants[i]) {
                        result.append(((Robot) participants[i]).getName());
                    }
                }

                if (statusParticipants[i] && i != participants.length - 1) {
                    result.append(", ");
                }

                if (i == participants.length - 1) {
                    result.append(".\n");
                }
            }
            System.out.println(result);
        }
    }

    public void printTeamInfo () {
        StringBuilder result = new StringBuilder();
        if (isPreparedToCourse) {
            result.append("Состав команды \"" + name + "\", состоящей из " + COUNT_PARTICIPANTS + " участников: \n");
            for (int i = 0; i < participants.length; i++) {
                if (i == participants.length - 1) {
                    result.append(" и ");
                }

                if (participants[i] instanceof Cat) {
                    result.append(((Cat) participants[i]).getName());
                } else if (participants[i] instanceof Human) {
                    result.append(((Human) participants[i]).getName());
                } else if (participants[i] instanceof Robot) {
                    result.append(((Robot) participants[i]).getName());
                }

                if (i == participants.length - 1) {
                    result.append(".\n");
                } else {
                    if (i != participants.length - 2) {
                        result.append(", ");
                    }
                }
            }
        } else {
            result.append("Результаты команды \"" + name + "\", проходившая дистанцию: \n");
            for (int i = 0; i < participants.length; i++) {
                if (participants[i] instanceof Cat) {
                    result.append("\t" + ((Cat) participants[i]).getName());
                    if (statusParticipants [i]) {
                        result.append(" (прошёл)");
                    }
                } else if (participants[i] instanceof Human) {
                    result.append("\t" + ((Human) participants[i]).getName());
                    if (statusParticipants [i]) {
                        result.append(" (прошёл)");
                    }
                } else if (participants[i] instanceof Robot) {
                    result.append("\t" + ((Robot) participants[i]).getName());
                    if (statusParticipants [i]) {
                        result.append(" (прошёл)");
                    }
                }

                result.append("\n");
            }
        }
        System.out.println(result);
    }

    public boolean isPreparedToCourse() {
        return isPreparedToCourse;
    }
}
