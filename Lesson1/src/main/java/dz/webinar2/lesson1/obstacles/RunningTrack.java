package dz.webinar2.lesson1.obstacles;

import dz.webinar2.lesson1.interfaces.Runnable;

public class RunningTrack {
    private int length;
    private static final int UNIT_LOAD = 1; //Сколько сил тратит дорожка у сущностей за каждый метр пробега

    public RunningTrack () {
        length = (int) (Math.random() * 100 + 1);
    }

    public int getLength() {
        return length;
    }

    public void applyObstacle (Runnable runnable) {
        runnable.run (length * UNIT_LOAD);
    }
}
