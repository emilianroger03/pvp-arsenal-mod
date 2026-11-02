package com.emilian.pvparsenal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeSlowManager {
    private static final Map<UUID, Long> slowedPlayers = new HashMap<>();

    public static void applySlow(UUID playerId, long endTick) {
        slowedPlayers.put(playerId, endTick);
    }

    public static boolean isSlowed(UUID playerId, long currentTick) {
        Long endTick = slowedPlayers.get(playerId);
        if (endTick == null) return false;
        if (currentTick >= endTick) {
            slowedPlayers.remove(playerId);
            return false;
        }
        return true;
    }

    public static boolean shouldSkipTick(UUID playerId, long currentTick) {
        if (!isSlowed(playerId, currentTick)) return false;
        // Only let 1 in 4 ticks through = 5 TPS effective tick rate
        return currentTick % 4 != 0;
    }

    public static void remove(UUID playerId) {
        slowedPlayers.remove(playerId);
    }
}
