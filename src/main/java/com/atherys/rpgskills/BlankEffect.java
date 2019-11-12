package com.atherys.rpgskills;

import com.atherys.skills.api.effect.AbstractEffect;
import com.atherys.skills.api.effect.ApplyableCarrier;

/**
 * An effect that does nothing.
 */
public class BlankEffect extends AbstractEffect {
    protected BlankEffect(String id, String name) {
        super(id, name);
    }

    @Override
    public boolean canApply(long timestamp, ApplyableCarrier<?> character) {
        return false;
    }

    @Override
    public boolean apply(long timestamp, ApplyableCarrier<?> character) {
        return true;
    }

    @Override
    public boolean canRemove(long timestamp, ApplyableCarrier<?> character) {
        return false;
    }

    @Override
    public boolean remove(long timestamp, ApplyableCarrier<?> character) {
        return true;
    }
}
