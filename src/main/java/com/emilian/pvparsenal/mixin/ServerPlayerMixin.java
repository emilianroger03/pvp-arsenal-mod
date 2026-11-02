package com.emilian.pvparsenal.mixin;

import com.emilian.pvparsenal.TimeSlowManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void pvparsenal$onTick(CallbackInfo ci) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
        ServerWorld world = self.getEntityWorld();
        long currentTick = world.getTime();

        if (TimeSlowManager.shouldSkipTick(self.getUuid(), currentTick)) {
            self.setVelocity(0, self.getVelocity().y * 0.5, 0);
            self.networkHandler.syncWithPlayerPosition();
            ci.cancel();
        }
    }
}
