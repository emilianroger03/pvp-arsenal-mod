package com.emilian.pvparsenal.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PositionFlag;
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
import java.util.Set;

public class VisionItem extends FishingRodItem {
    private static final double SWAP_RADIUS = 50.0;

    public VisionItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

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
        double playerX = player.getX();
        double playerY = player.getY();
        double playerZ = player.getZ();
        float playerYaw = player.getYaw();
        float playerPitch = player.getPitch();

        double targetX = target.getX();
        double targetY = target.getY();
        double targetZ = target.getZ();
        float targetYaw = target.getYaw();
        float targetPitch = target.getPitch();

        player.teleport(world, targetX, targetY, targetZ,
                Set.of(), targetYaw, targetPitch, false);
        target.teleport(world, playerX, playerY, playerZ,
                Set.of(), playerYaw, playerPitch, false);

        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.PLAYERS, 1.0f, 1.2f);
        world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.PLAYERS, 1.0f, 1.2f);

        spawnSwapParticles(world, playerX, playerY, playerZ);
        spawnSwapParticles(world, targetX, targetY, targetZ);
    }

    private void spawnSwapParticles(ServerWorld world, double x, double y, double z) {
        world.spawnParticles(ParticleTypes.LARGE_SMOKE, x, y + 1, z,
                30, 0.5, 1.0, 0.5, 0.05);
    }
}
