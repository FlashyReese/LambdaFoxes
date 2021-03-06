/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaFoxes.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdafoxes.entity.ai.goal;

import me.lambdaurora.lambdafoxes.entity.LambdaFoxEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.sound.SoundEvents;

import java.util.EnumSet;
import java.util.Optional;

/**
 * Represents a goal which makes a fox attack with its owner if the trust level is high enough.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class FoxAttackWithOwnerGoal extends TrackTargetGoal
{
    private final LambdaFoxEntity fox;
    private       LivingEntity    attacking;
    private       int             lastAttackTime;

    public FoxAttackWithOwnerGoal(LambdaFoxEntity fox)
    {
        super((MobEntity) fox, false);
        this.fox = fox;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart()
    {
        if (this.fox.getTrustLevel() >= this.fox.getMaxTrustLevel() - 1 && !this.fox.isWaiting()) {
            Optional<LivingEntity> owner = this.fox.getOwner();
            if (owner.isPresent()) {
                this.attacking = owner.get().getAttacking();
                int lastAttackTime = owner.get().getLastAttackTime();
                return lastAttackTime != this.lastAttackTime
                        && this.canTrack(this.attacking, TargetPredicate.DEFAULT)
                        && this.fox.canAttackWithOwner(this.attacking, owner.get());
            }
        }
        return false;
    }

    @Override
    public void start()
    {
        ((FoxEntity) this.fox).setTarget(this.attacking);
        this.fox.getOwner().ifPresent(livingEntity -> this.lastAttackTime = livingEntity.getLastAttackTime());

        ((FoxEntity) this.fox).playSound(SoundEvents.ENTITY_FOX_AGGRO, 1.0F, 1.0F);
        this.fox.setFoxAggressive(true);
        this.fox.setFoxSleeping(false);

        super.start();
    }
}
