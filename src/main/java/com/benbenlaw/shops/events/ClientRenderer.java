package com.benbenlaw.shops.events;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.capability.PlayerBalanceData;
import com.benbenlaw.shops.capability.ShopsAttachments;
import com.benbenlaw.shops.item.ShopsItems;
import com.benbenlaw.shops.screen.ShopScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = Shops.MOD_ID, value = Dist.CLIENT)
public class ClientRenderer {


    @SubscribeEvent
    public static void onScreenRenderer(ScreenEvent.Render.Post event) {

        if (event.getScreen() instanceof InventoryScreen screen) {
            Player player = event.getScreen().getMinecraft().player;

            if (player != null) {
                PlayerBalanceData data = player.getData(ShopsAttachments.PLAYER_BALANCE);

                int mouseX = event.getMouseX();
                int mouseY = event.getMouseY();

                int x = screen.getGuiLeft() + screen.getXSize() - 18;
                int y = screen.getGuiTop() + 2;
                int width = 16;
                int height = 16;

                if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                    event.getGuiGraphics().renderTooltip(Minecraft.getInstance().font, Component.translatable("tooltip.shops.balance", data.getBalance()), mouseX, mouseY);
                }
                event.getGuiGraphics().renderFakeItem(ShopsItems.GOLD_COIN.get().getDefaultInstance(), x, y);
            }
        }

        if (event.getScreen() instanceof ShopScreen screen) {
            Player player = event.getScreen().getMinecraft().player;

            if (player != null) {
                PlayerBalanceData data = player.getData(ShopsAttachments.PLAYER_BALANCE);

                int mouseX = event.getMouseX();
                int mouseY = event.getMouseY();

                int x = screen.getGuiLeft() + screen.getXSize() - 18;
                int y = screen.getGuiTop() + 2;
                int width = 16;
                int height = 16;

                if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                    event.getGuiGraphics().renderTooltip(Minecraft.getInstance().font, Component.translatable("tooltip.shops.balance", data.getBalance()), mouseX, mouseY);
                }
                event.getGuiGraphics().renderFakeItem(ShopsItems.GOLD_COIN.get().getDefaultInstance(), x, y);
            }
        }
    }
}


