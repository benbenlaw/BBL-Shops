package com.benbenlaw.shops.screen;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.block.entity.ShopBlockEntity;
import com.benbenlaw.shops.capability.PlayerBalanceData;
import com.benbenlaw.shops.capability.ShopsAttachments;
import com.benbenlaw.shops.item.util.ShopEntry;
import com.benbenlaw.shops.item.util.ShopRegistry;
import com.benbenlaw.shops.network.packets.SyncAutoItemToServer;
import com.benbenlaw.shops.network.packets.SyncPurchaseToServer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.components.EditBox;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShopScreen extends AbstractContainerScreen<ShopMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Shops.MOD_ID, "textures/gui/shop_gui.png");

    //Grid Settings
    int columns = 6;
    int startX = 35;
    int startY = 34;
    int spacingX = 18;
    int spacingY = 18;

    public ItemStack autoProduced;

    private EditBox searchBox;

    public ShopScreen(ShopMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        this.autoProduced = menu.blockEntity.getAutoProduced();

        // Place search bar near the top (adjust x,y,width as needed)
        this.searchBox = new EditBox(this.font, leftPos + 34, topPos + 17, 108, 12, Component.literal("Search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(true);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(0xFFFFFF);

        this.addRenderableWidget(this.searchBox);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        List<ShopEntry> itemsToDisplay = getFilteredItems();

        for (int i = 0; i < itemsToDisplay.size(); i++) {
            int x = leftPos + startX + (i % columns) * spacingX;
            int y = topPos + startY + (i / columns) * spacingY;
            graphics.renderItem(itemsToDisplay.get(i).getItem(), x, y);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
        this.searchBox.render(graphics, mouseX, mouseY, partialTicks);

        List<ShopEntry> itemsToDisplay = getFilteredItems();

        for (int i = 0; i < itemsToDisplay.size(); i++) {
            int x = leftPos + startX + (i % columns) * spacingX;
            int y = topPos + startY + (i / columns) * spacingY;

            if (mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16) {
                graphics.renderTooltip(
                        font,
                        Component.literal(itemsToDisplay.get(i).getItem().getHoverName().getString() + " - " + itemsToDisplay.get(i).getPrice() + " coins"),
                        mouseX, mouseY
                );
            }
        }

        renderAutoProducedItem(graphics, mouseX, mouseY);
        renderSlotTooltips(graphics, mouseX, mouseY, leftPos, topPos);
    }

    private void renderSlotTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        List<TooltipArea> tooltipAreas = new ArrayList<>();

        tooltipAreas.add(new TooltipArea(8, 16, 16, 16,"block.shops.shop.player_balance_card"));
        tooltipAreas.add(new TooltipArea(8, 34, 16, 16,"block.shops.shop.selling_input"));
        tooltipAreas.add(new TooltipArea(8, 52, 16, 16,"block.shops.shop.catalog"));
        tooltipAreas.add(new TooltipArea(152, 52, 16, 16,"block.shops.shop.output"));

        for (TooltipArea area : tooltipAreas) {
            if (isMouseAboveArea(mouseX, mouseY, x, y, area.offsetX, area.offsetY, area.width, area.height)) {
                if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && !this.hoveredSlot.hasItem()) {
                    guiGraphics.renderTooltip(this.font, Component.translatable(area.translationKey), mouseX, mouseY);
                }
            }
        }

        if (autoProduced.isEmpty()) {
            if (isMouseAboveArea(mouseX, mouseY, x, y, 152, 34, 16, 16)) {
                guiGraphics.renderTooltip(this.font, Component.translatable("block.shops.shop.auto_produce"), mouseX, mouseY);
            }
        }
    }


    public static boolean isMouseOver(double mouseX, double mouseY, int x, int y, int sizeX, int sizeY) {
        return mouseX >= (double)x && mouseX <= (double)(x + sizeX) && mouseY >= (double)y && mouseY <= (double)(y + sizeY);
    }

    public static boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return isMouseOver((double)pMouseX, (double)pMouseY, x + offsetX, y + offsetY, width, height);
    }

    private void renderAutoProducedItem(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!autoProduced.isEmpty()) {
            int x = leftPos + 152;
            int y = topPos + 34;
            graphics.renderItem(autoProduced, x, y);

            if (mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16) {
                graphics.renderTooltip(
                        font,
                        Component.literal("Auto Produce: " + autoProduced.getHoverName().getString()),
                        mouseX, mouseY
                );
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        List<ShopEntry> itemsToDisplay = getFilteredItems();

        for (int i = 0; i < itemsToDisplay.size(); i++) {
            int x = leftPos + startX + (i % columns) * spacingX;
            int y = topPos + startY + (i / columns) * spacingY;

            if (mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16) {
                ShopEntry item = itemsToDisplay.get(i);

                if (button == 0) {
                    PlayerBalanceData balance = menu.player.getData(ShopsAttachments.PLAYER_BALANCE);
                    if (balance == null) return super.mouseClicked(mouseX, mouseY, button);

                    if (balance.getBalance() >= item.getPrice()) {
                        int newBalance = balance.getBalance() - item.getPrice();
                        balance.setBalance(newBalance);
                        menu.player.getInventory().add(item.getItem().copy());

                        PacketDistributor.sendToServer(new SyncPurchaseToServer(newBalance, item.getItem().copy()));
                    } else {
                        menu.player.sendSystemMessage(Component.literal("Not enough balance!"));
                    }
                }

                else if (button == 1) {

                    if (autoProduced != null && ItemStack.isSameItem(autoProduced, item.getItem())) {
                        autoProduced = ItemStack.EMPTY;
                    } else {
                        autoProduced = item.getItem().copy();
                    }

                    PacketDistributor.sendToServer(new SyncAutoItemToServer(menu.blockPos, autoProduced.copy()));
                }

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }


    private List<ShopEntry> getFilteredItems() {
        ItemStack catalogueStack = menu.blockEntity.getItemStackHandler().getStackInSlot(ShopBlockEntity.CATALOG);

        List<ShopEntry> baseItems;
        if (!catalogueStack.isEmpty()) {
            baseItems = ShopRegistry.getCatalogueItems(catalogueStack);
        } else {
            baseItems = List.of();
        }

        // If search box is empty, return all
        if (searchBox == null || searchBox.getValue().isEmpty()) {
            return baseItems;
        }

        String query = searchBox.getValue().toLowerCase(Locale.ROOT);
        return baseItems.stream()
                .filter(entry -> entry.getItem().getHoverName().getString().toLowerCase(Locale.ROOT).contains(query))
                .toList();
    }

}
