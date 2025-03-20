package com.exosomnia.exolib.scheduler.actions;

import com.exosomnia.exolib.scheduler.ScheduleManager;

public abstract class ScheduledAction {

    protected ScheduleManager manager;
    public boolean active = true;
    public int scheduledTick = 0;

    public abstract boolean isValid();
    public abstract void action();

    public void scheduleFor(ScheduleManager manager, int scheduledTick) {
        this.manager = manager;
        this.scheduledTick = scheduledTick;
    }

    public void execute() {
        if (!active || !isValid()) { return; }
        action();
    }
}
