package dz.webinar2.lesson1.entities;

import dz.webinar2.lesson1.interfaces.Jumpable;
import dz.webinar2.lesson1.interfaces.Runnable;

public class Robot implements Runnable, Jumpable {
    private int strength;
    private String name = "Робот";
    private boolean isStrengthEnough = true;

    public Robot () {
        strength = (int) (Math.random() * 300 + 501); //501 - 800
    }

    @Override
    public void jump(int load) {
        if (strength < 0) {
            return;
        }
        System.out.print(name + " пытается перепрыгнуть препятствие.");
        strength -= load;

        if (strength > 0) {
            System.out.println(" И успешно перепрыгнул.");
        } else if (strength == 0) {
            System.out.println(" И перепрыгивает на пределе своих возможностей.");
            isStrengthEnough = false;
        } else {
            System.out.println(" И не смог перепрыгнуть.");
            isStrengthEnough = false;
        }
    }

    @Override
    public void run(int load) {
        if (strength < 0) {
            return;
        }

        System.out.print(name + " побежал дистанцию.");
        strength -= load;

        if (strength > 0) {
            System.out.println(" И успешно справился.");
        } else if (strength == 0) {
            System.out.println(" И пробегает на пределе своих возможностей.");
            isStrengthEnough = false;
        } else {
            System.out.println(" И не смог пробежать.");
            isStrengthEnough = false;
        }
    }

    @Override
    public boolean getStatus() {
        return isStrengthEnough;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
