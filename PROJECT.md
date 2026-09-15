# PvP Arsenal Mod

## What is it?
A Fabric mod (Minecraft 1.21.11) with two custom PvP weapons: a position-swapping fishing rod and a time-slowing clock.

## Why am I building this?
Because swapping places with someone mid-fight and then freezing them in slow-mo is absolutely nasty. Peak PvP chaos.

## The Weapons

### 1. Vision (Fishing Rod)
- Unbreakable fishing rod
- On cast: teleport to the nearest player within 50 blocks
- **Swap positions** — they go to where I was, I go to where they were
- Visual/sound effects on swap

### 2. Time Clock
- Custom clock item
- Right-click to activate
- Target opponent moves at **0.25x speed** (Slowness effect)
- I stay at **full speed**
- Lasts **30 seconds**
- **60-second cooldown** between uses
- Cooldown indicator (actionbar message or XP bar)

## Steps
- [ ] Step 1: Set up Fabric mod project (build.gradle, mod entrypoint, fabric.mod.json)
- [ ] Step 2: Register the two custom items (Vision + Time Clock)
- [ ] Step 3: Implement Vision — fishing rod cast detection + position swap logic
- [ ] Step 4: Implement Time Clock — right-click activation, slowness effect, cooldown system
- [ ] Step 5: Add item textures and models (even placeholder ones)
- [ ] Step 6: Test in-game with a second player (or fake player)
- [ ] Step 7: Polish — sound effects, particles, cooldown display

## Today's Goal
Get Steps 1-3 done: working Fabric mod with the Vision fishing rod that swaps positions.

## Progress Log
- 2026-09-15: Started project, created plan
