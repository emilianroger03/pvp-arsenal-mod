package com.emilian.pvparsenal;

import com.emilian.pvparsenal.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvpArsenal implements ModInitializer {
    public static final String MOD_ID = "pvparsenal";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.register();
        LOGGER.info("PvP Arsenal loaded — Vision and Time Clock ready!");
    }
}
