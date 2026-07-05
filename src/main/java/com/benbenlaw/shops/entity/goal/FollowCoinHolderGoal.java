package com.benbenlaw.shops.entity.goal;

import com.benbenlaw.shops.entity.ShopTraderMob;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.Optional;

public class FollowCoinHolderGoal extends Goal {

    private final ShopTraderMob trader;
    private Player target;

    public FollowCoinHolderGoal(ShopTraderMob trader) {
        this.trader = trader;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Optional<BlockPos> jobSite = trader.getOrFindJobSite();
        if (jobSite.isEmpty()) return false;

        Player nearest = trader.level().getNearestPlayer(
                trader.getX(), trader.getY(), trader.getZ(), trader.followRadius(), false);

        if (nearest == null || !trader.isHoldingCoin(nearest)) return false;
        if (jobSite.get().distSqr(nearest.blockPosition()) > trader.maxFollowDistanceFromJobSite() * trader.maxFollowDistanceFromJobSite()) return false;

        this.target = nearest;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null || !target.isAlive()) return false;
        if (!trader.isHoldingCoin(target)) return false;

        Optional<BlockPos> jobSite = trader.getOrFindJobSite();
        if (jobSite.isEmpty()) return false;

        return jobSite.get().distSqr(target.blockPosition()) <= trader.maxFollowDistanceFromJobSite() * trader.maxFollowDistanceFromJobSite();
    }

    @Override
    public void tick() {
        if (trader.distanceToSqr(target) <= trader.followStopDistance() * trader.followStopDistance()) {
            trader.getNavigation().stop();
            return;
        }
        trader.getNavigation().moveTo(target, trader.followSpeed());
    }

    @Override
    public void stop() {
        this.target = null;
        trader.getNavigation().stop();
    }
}