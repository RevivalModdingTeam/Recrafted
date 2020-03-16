package dev.revivalmoddingteam.recrafted.handler.event;

public class Action {

    private int ticks;
    private Runnable runnable;

    public Action(int ticks, Runnable runnable) {
        this.ticks = ticks;
        this.runnable = runnable;
    }

    public boolean tick() {
        if(ticks-- <= 0) {
            runnable.run();
            return true;
        }
        return false;
    }
}
