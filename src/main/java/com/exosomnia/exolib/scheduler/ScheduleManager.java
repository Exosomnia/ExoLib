package com.exosomnia.exolib.scheduler;

import com.exosomnia.exolib.scheduler.actions.ScheduledAction;

import java.util.*;

public class ScheduleManager {

    private static int tickCount = 0;
    private final PriorityQueue<ScheduledAction> ACTION_SCHEDULE = new PriorityQueue<>(new ScheduledComparator());

    public void tick() {
        while (!ACTION_SCHEDULE.isEmpty() && ACTION_SCHEDULE.peek().scheduledTick <= tickCount) {
            ScheduledAction action = ACTION_SCHEDULE.poll();
            action.execute();
        }
        tickCount++;
    }

    public void scheduleAction(ScheduledAction action, int ticks) {
        action.scheduleFor(this, tickCount + ticks);
        ACTION_SCHEDULE.add(action);
    }

    private static class ScheduledComparator implements Comparator<ScheduledAction> {
        public int compare(ScheduledAction actionLeft, ScheduledAction actionRight) {
            return Integer.compare(actionLeft.scheduledTick, actionRight.scheduledTick);
        }
    }
}
