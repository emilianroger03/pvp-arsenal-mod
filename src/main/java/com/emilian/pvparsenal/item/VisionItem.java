package com.emilian.pvparsenal.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.Optional;

public class VisionItem extends FishingRodItem {
    private static final double SWAP_RADIUS = 50.0;

    public VisionItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        ServerWorld serverWorld = (ServerWorld) world;
        ServerPlayerEntity player = (ServerPlayerEntity) user;

        if (player.fishHook != null) {
            player.fishHook.discard();
        }

        Optional<ServerPlayerEntity> nearest = serverWorld.getPlayers().stream()
                .filter(p -> p != player)
                .filter(p -> p.squaredDistanceTo(player) <= SWAP_RADIUS * SWAP_RADIUS)
                .min(Comparator.comparingDouble(p -> p.squaredDistanceTo(player)));

        if (nearest.isPresent()) {
            ServerPlayerEntity target = nearest.get();
            swapPositions(player, target, serverWorld);
            return ActionResult.SUCCESS;
        }

        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(),
                SoundCategory.PLAYERS, 1.0f, 0.5f);
        return ActionResult.FAIL;
    }

    private void swapPositions(ServerPlayerEntity player, ServerPlayerEntity target, ServerWorld world) {
        Vec3d playerPos = player.getPos();
        float playerYaw = player.getYaw();
        float playerPitch = player.getPitch();

        Vec3d targetPos = target.getPos();
        float targetYaw = target.getYaw();
        float targetPitch = target.getPitch();

        player.teleport(world, targetPos.x, targetPos.y, targetPos.z, targetYaw, targetPitch);
        target.teleport(world, playerPos.x, playerPos.y, playerPos.z, playerYaw, playerPitch);

        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.PLAYERS, 1.0f, 1.2f);
        world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.PLAYERS, 1.0f, 1.2f);

        spawnSwapParticles(world, playerPos);
        spawnSwapParticles(world, targetPos);
    }

    private void spawnSwapParticles(ServerWorld world, Vec3d pos) {
        world.spawnParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y + 1, pos.z,
                30, 0.5, 1.0, 0.5, 0.05);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }
}
