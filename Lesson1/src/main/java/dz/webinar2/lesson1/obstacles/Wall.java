package dz.webinar2.lesson1.obstacles;

import dz.webinar2.lesson1.interfaces.Jumpable;

public class Wall {
    private int height;
    private static final int UNIT_LOAD = 10; //Сколько сил тратит стена у сущностей за каждый метр высоты

    public Wall () {
        height = (int) (Math.random() * 3 + 1);
    }

    public int getHeight() {
        return height;
    }

    public void applyObstacle (Jumpable jumpable) {
        jumpable.jump(height * UNIT_LOAD);
    }
}
