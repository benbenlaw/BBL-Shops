package com.benbenlaw.shops.events.client;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.attachments.PlayerBalanceData;
import com.benbenlaw.shops.attachments.ShopsAttachments;
import com.benbenlaw.shops.item.ShopsItems;
import com.benbenlaw.shops.screen.ClientScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.List;

@EventBusSubscriber(modid = Shops.MOD_ID, value = Dist.CLIENT)
public class ClientRenderer {

    @SubscribeEvent
    public static void onButtonClick(ScreenEvent.MouseButtonPressed.Post event) {

        if (event.getScreen() instanceof InventoryScreen screen) {

            int mouseX = (int) event.getMouseX();
            int mouseY = (int) event.getMouseY();

            int x = screen.getLeftPos() + screen.getImageWidth() - 18;
            int y = screen.getTopPos() + 2;
            int width = 16;
            int height = 16;

            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                ClientScreens.openShopScreen();
            }
        }

    }

    @SubscribeEvent
    public static void onContainerForeground(ContainerScreenEvent.Render.Foreground event) {
        if (event.getContainerScreen() instanceof InventoryScreen screen) {
            Player player = screen.getMinecraft().player;
            if (player == null) return;

            PlayerBalanceData data = player.getData(ShopsAttachments.PLAYER_BALANCE);

            int x = screen.getImageWidth() - 18;
            int y = 2;
            int width = 16;
            int height = 16;

            var graphics = event.getGuiGraphics();
            graphics.item(ShopsItems.GOLD_COIN.get().getDefaultInstance(), x, y);

            int screenX = screen.getLeftPos() + x;
            int screenY = screen.getTopPos() + y;
            int mouseX = event.getMouseX();
            int mouseY = event.getMouseY();

            if (mouseX >= screenX && mouseX <= screenX + width && mouseY >= screenY && mouseY <= screenY + height) {
                Component tooltipText = Component.translatable("tooltip.shops.balance", data.getBalance());

                graphics.setTooltipForNextFrame(tooltipText, mouseX, mouseY);
            }
        }
    }
}