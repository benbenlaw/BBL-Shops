package com.benbenlaw.shops.screen;

import com.benbenlaw.shops.Shops;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class ShopsBlockScreen extends AbstractContainerScreen<ShopsMenu> {

    private static final Identifier TEXTURE = Shops.identifier("textures/gui/shop_block_gui.png");

    private static final int PANEL_BACKGROUND = 0xFF1E1E1E;
    private static final int PANEL_BORDER = 0xFF444444;
    private static final int HINT_COLOR = 0xFFAAAAAA;

    public ShopsBlockScreen(ShopsMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackground(guiGraphics, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        //graphics.text(this.font, Component.translatable("block.shops.shop.player_balance_card"), 26, 6, HINT_COLOR, false);
        //graphics.text(this.font, Component.translatable("block.shops.shop.selling_input"), 26, 42, HINT_COLOR, false);
        //graphics.text(this.font, Component.translatable("block.shops.shop.output"), 134, 42, HINT_COLOR, false);
    }
}
