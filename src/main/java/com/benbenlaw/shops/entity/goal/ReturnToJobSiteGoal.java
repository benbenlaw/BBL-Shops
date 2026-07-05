package com.benbenlaw.shops.entity.goal;

import com.benbenlaw.shops.entity.ShopTraderMob;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Optional;

public class ReturnToJobSiteGoal extends Goal {

    private final ShopTraderMob trader;
    private BlockPos target;

    public ReturnToJobSiteGoal(ShopTraderMob trader) {
        this.trader = trader;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Optional<BlockPos> jobSite = trader.getOrFindJobSite();
        if (jobSite.isEmpty()) return false;

        BlockPos pos = jobSite.get();
        double distSqr = trader.distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        if (distSqr <= trader.tetherRadius() * trader.tetherRadius()) return false;

        this.target = pos;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return !trader.getNavigation().isDone();
    }

    @Override
    public void start() {
        trader.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 0.5D);
    }

    @Override
    public void stop() {
        this.target = null;
        trader.getNavigation().stop();
    }
}