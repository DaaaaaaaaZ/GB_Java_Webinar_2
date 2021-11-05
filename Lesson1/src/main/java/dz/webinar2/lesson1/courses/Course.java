package dz.webinar2.lesson1.courses;

import dz.webinar2.lesson1.entities.Cat;
import dz.webinar2.lesson1.interfaces.Jumpable;
import dz.webinar2.lesson1.interfaces.Runnable;
import dz.webinar2.lesson1.obstacles.RunningTrack;
import dz.webinar2.lesson1.obstacles.Wall;
import dz.webinar2.lesson1.teams.Team;

public class Course {
    private Object [] obstacles;

    public Course () {
        int countObstacles = (int) (Math.random() * 3 + 3);

        obstacles = new Object[countObstacles];
        for (int i = 0; i < obstacles.length; i++) {
            if ((int) (Math.random() * 10) > 7) {
                obstacles [i] = new Wall();
            } else {
                obstacles [i] = new RunningTrack();
            }
        }
    }

    public void printCourse () {
        StringBuilder result = new StringBuilder("Полоса препятствий: \n");
        for (Object o : obstacles) {
            if (o instanceof Wall) {
                result.append("\tСтена ").append(((Wall)o).getHeight()).append(" м\n");
            } else if (o instanceof RunningTrack) {
                result.append("\tБеговая дорожка ").append(((RunningTrack)o).getLength()).append(" м\n");
            }
        }
        System.out.println(result.toString());
    }

    private boolean doItOne (Object objectIn) {
        for (Object o : obstacles) {
            if (o instanceof Wall) {
                if (objectIn instanceof Jumpable) {
                    if (((Jumpable)objectIn).getStatus()) {
                        ((Wall)o).applyObstacle((Jumpable)objectIn);
                        if (!((Jumpable)objectIn).getStatus()) {
                            System.out.println();
                            return false;
                        }
                    } else {
                        System.out.println("Участник не прошёл испытания.");
                        System.out.println();
                        return false;
                    }
                }
            } else if (o instanceof RunningTrack) {
                if (objectIn instanceof Runnable) {
                    if (((Runnable)objectIn).getStatus()) {
                        ((RunningTrack)o).applyObstacle((Runnable)objectIn);
                        if (!((Jumpable)objectIn).getStatus()) {
                            System.out.println();
                            return false;
                        }
                    } else {
                        System.out.println("Участник не прошёл испытания.");
                        System.out.println();
                        return false;
                    }
                }
            }
        }
        System.out.println();
        return true;
    }

    public void doIt (Team team) {
        while (team.isPreparedToCourse()) {
            team.setResultForCurrentParticipants(doItOne(team.obtain()));
        }
    }
}
