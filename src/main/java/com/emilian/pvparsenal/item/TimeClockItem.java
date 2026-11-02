package com.emilian.pvparsenal.item;

import com.emilian.pvparsenal.TimeSlowManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TimeClockItem extends Item {
    private static final int EFFECT_DURATION_TICKS = 30 * 20;
    private static final int COOLDOWN_TICKS = 60 * 20;
    private static final double EFFECT_RADIUS = 50.0;

    private static final Map<UUID, Long> lastUsedTick = new HashMap<>();

    public TimeClockItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) user;
        long currentTick = world.getTime();

        Long lastUsed = lastUsedTick.get(player.getUuid());
        if (lastUsed != null) {
            long elapsed = currentTick - lastUsed;
            if (elapsed < COOLDOWN_TICKS) {
                int secondsLeft = (int) ((COOLDOWN_TICKS - elapsed) / 20);
                player.sendMessage(
                        Text.literal("Time Clock on cooldown: " + secondsLeft + "s")
                                .formatted(Formatting.RED),
                        true);
                return ActionResult.FAIL;
            }
        }

        Optional<ServerPlayerEntity> nearest = serverWorld.getPlayers().stream()
                .filter(p -> p != player)
                .filter(p -> p.squaredDistanceTo(player) <= EFFECT_RADIUS * EFFECT_RADIUS)
                .min(Comparator.comparingDouble(p -> p.squaredDistanceTo(player)));

        if (nearest.isPresent()) {
            ServerPlayerEntity target = nearest.get();

            long endTick = currentTick + EFFECT_DURATION_TICKS;
            TimeSlowManager.applySlow(target.getUuid(), endTick);

            lastUsedTick.put(player.getUuid(), currentTick);
            player.getItemCooldownManager().set(this.getDefaultStack(), COOLDOWN_TICKS);

            serverWorld.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(),
                    SoundCategory.PLAYERS, 1.0f, 0.5f);
            serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE, target.getX(), target.getY() + 1, target.getZ(),
                    40, 0.5, 1.0, 0.5, 0.02);

            player.sendMessage(
                    Text.literal("Time slowed for " + target.getName().getString() + "!")
                            .formatted(Formatting.GOLD),
                    true);
            target.sendMessage(
                    Text.literal("Time has been slowed around you...")
                            .formatted(Formatting.DARK_PURPLE),
                    true);

            return ActionResult.SUCCESS;
        }

        player.sendMessage(
                Text.literal("No players in range!")
                        .formatted(Formatting.GRAY),
                true);
        return ActionResult.FAIL;
    }
}
