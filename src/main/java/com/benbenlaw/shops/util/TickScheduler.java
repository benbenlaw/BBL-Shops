package com.benbenlaw.shops.util;

import net.minecraft.world.level.Level;
import net.minecraft.world.ticks.ScheduledTick;
import org.jetbrains.annotations.Async;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class TickScheduler {

    private static final List<ScheduledTask> TASKS = new ArrayList<>();

    public static void schedule(Level level, int delayTicks, Runnable task) {
        long runAt = level.getGameTime() + delayTicks;
        synchronized (TASKS) {
            TASKS.add(new ScheduledTask(level, runAt, task));
        }
    }

    public static void tick(Level level) {

        long currentTick = level.getGameTime();
        List<ScheduledTask> exec = new ArrayList<>();

        synchronized (TASKS) {
            Iterator<ScheduledTask> it = TASKS.iterator();

            while (it.hasNext()) {
                ScheduledTask t = it.next();

                if (t.level == level && t.runAtTick <= currentTick) {
                    exec.add(t);
                    it.remove();
                }
            }
        }

        for (ScheduledTask t : exec) {
            t.task.run();
        }
    }

    private static class ScheduledTask {
        final Level level;
        final long runAtTick; Runnable task;

        ScheduledTask(Level level, long runAtTick, Runnable task) {
            this.level = level;
            this.runAtTick = runAtTick;
            this.task = task;
        }
    }
}