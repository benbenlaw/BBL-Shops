package com.benbenlaw.shops.util;

import com.benbenlaw.shops.Shops;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {

    public static final KeyMapping.Category KEY_CATEGORY =  KeyMapping.Category.register(Shops.identifier("shops"));

    public static final String SHOP_OPEN_KEY = "key.shops.shop_open_key";

    public static final KeyMapping SHOP_OPEN_HOTKEY = new KeyMapping(SHOP_OPEN_KEY, KeyConflictContext.UNIVERSAL, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY);

}
