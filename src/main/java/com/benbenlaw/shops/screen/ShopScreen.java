package com.benbenlaw.shops.screen;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.block.entity.ShopBlockEntity;
import com.benbenlaw.shops.capability.PlayerBalanceData;
import com.benbenlaw.shops.capability.ShopsAttachments;
import com.benbenlaw.shops.loaders.ShopEntry;
import com.benbenlaw.shops.loaders.ShopRegistry;
import com.benbenlaw.shops.network.packets.SyncAutoItemToServer;
import com.benbenlaw.shops.network.packets.SyncPurchaseToServer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ShopScreen extends AbstractContainerScreen<ShopMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Shops.MOD_ID, "textures/gui/shop_gui.png");

    // Grid settings
    private final int columns = 6;
    private final int startX = 28;
    private final int startY = 34;
    private final int spacingX = 18;
    private final int spacingY = 18;
    private final int maxVisibleRows = 2;
    private int scrollOffset = 0;

    //Buttons
    private Button scrollUpButton;
    private Button scrollDownButton;

    //Scroll and Search
    public ItemStack autoProduced;
    private EditBox searchBox;
    private String lastSearch = "";

    public ShopScreen(ShopMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        this.autoProduced = menu.blockEntity.getAutoProduced();

        // Search bar
        this.searchBox = new EditBox(this.font, leftPos + 34, topPos + 17, 108, 12, Component.literal("Search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(true);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(0xFFFFFF);
        this.addRenderableWidget(this.searchBox);

        int buttonX = leftPos + 137;
        int scrollUpY = topPos + 36;
        int scrollDownY = topPos + 55;

        scrollUpButton = Button.builder(Component.literal("▲"), button -> {
            if (scrollOffset > 0) {
                scrollOffset--;
            }
        }).bounds(buttonX, scrollUpY, 12, 12).build();

        scrollDownButton = Button.builder(Component.literal("▼"), button -> {
            List<ShopEntry> items = getFilteredItems();
            int totalRows = (int) Math.ceil(items.size() / (double) columns);
            if (scrollOffset < totalRows - maxVisibleRows) {
                scrollOffset++;
            }
        }).bounds(buttonX, scrollDownY, 12, 12).build();

        this.addRenderableWidget(scrollUpButton);
        this.addRenderableWidget(scrollDownButton);

    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        for (DisplayedItem di : getVisibleItems()) {
            graphics.renderItem(di.entry().getItem(), di.x(), di.y());
            graphics.renderItemDecorations(font, di.entry().getItem(), di.x(), di.y());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
        searchBox.render(graphics, mouseX, mouseY, partialTicks);

        for (DisplayedItem di : getVisibleItems()) {
            if (isMouseOver(mouseX, mouseY, di.x(), di.y(), 16, 16)) {

                List<Component> fullTooltip = di.entry.getItem().getTooltipLines(Item.TooltipContext.EMPTY, menu.player, TooltipFlag.NORMAL );

                fullTooltip.add(Component.literal("Price: " + di.entry().getPrice() + " coins").withStyle(ChatFormatting.GOLD));

                graphics.renderTooltip(font, fullTooltip, Optional.empty(), mouseX, mouseY);
            }
        }

        int totalRows = (int) Math.ceil(getFilteredItems().size() / (double) columns);
        boolean canScroll = totalRows > maxVisibleRows;

        scrollUpButton.visible = canScroll;
        scrollDownButton.visible = canScroll;


        renderAutoProducedItem(graphics, mouseX, mouseY);
        renderSlotTooltips(graphics, mouseX, mouseY, leftPos, topPos);
    }

    private void renderSlotTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        List<TooltipArea> tooltipAreas = List.of(
                new TooltipArea(8, 16, 16, 16, "block.shops.shop.player_balance_card"),
                new TooltipArea(8, 34, 16, 16, "block.shops.shop.selling_input"),
                new TooltipArea(8, 52, 16, 16, "block.shops.shop.catalog"),
                new TooltipArea(152, 52, 16, 16, "block.shops.shop.output")
        );

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

    private void renderAutoProducedItem(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!autoProduced.isEmpty()) {
            int x = leftPos + 152;
            int y = topPos + 34;
            graphics.renderItem(autoProduced, x, y);

            if (isMouseOver(mouseX, mouseY, x, y, 16, 16)) {
                graphics.renderTooltip(font, Component.literal("Auto Produce: " + autoProduced.getHoverName().getString()), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (this.searchBox != null && this.searchBox.isMouseOver(mouseX, mouseY)) {
            if (button == 1) {
                this.searchBox.setValue("");
                this.searchBox.setFocused(true);
                this.lastSearch = "";
                this.scrollOffset = 0;
                return true;
            }
        }

        for (DisplayedItem di : getVisibleItems()) {
            if (isMouseOver(mouseX, mouseY, di.x(), di.y(), 16, 16)) {
                ShopEntry item = di.entry();

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
                } else if (button == 1) {
                    if (autoProduced != null && ItemStack.isSameItem(autoProduced, item.getItem())) {
                        autoProduced = ItemStack.EMPTY;
                    } else {
                        autoProduced = item.getItem().copy();
                    }
                    PacketDistributor.sendToServer(new SyncAutoItemToServer(menu.blockPos, autoProduced.copy()));
                }

                return true;
            }

            if (isMouseOver(mouseX, mouseY, leftPos + 152, topPos + 34, 16, 16) && !autoProduced.isEmpty()) {
                if (button == 0 || button == 1) {
                    autoProduced = ItemStack.EMPTY;
                    PacketDistributor.sendToServer(new SyncAutoItemToServer(menu.blockPos, autoProduced.copy()));
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        List<ShopEntry> items = getFilteredItems();
        int totalRows = (int) Math.ceil(items.size() / (double) columns);

        if (totalRows <= maxVisibleRows) return false;

        scrollOffset -= deltaY; // scroll delta
        scrollOffset = Math.max(0, Math.min(scrollOffset, totalRows - maxVisibleRows));
        return true;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {

        if (this.searchBox != null && this.searchBox.isFocused()) {
            assert minecraft != null;
            if (minecraft.options.keyInventory.matches(key, scanCode)) {
                return true;
            }

            if (this.searchBox.keyPressed(key, scanCode, modifiers)) {
                return true;
            }
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    public List<ShopEntry> getFilteredItems() {
        ItemStack catalogueStack = menu.blockEntity.getItemStackHandler().getStackInSlot(ShopBlockEntity.CATALOG);

        List<ShopEntry> baseItems = !catalogueStack.isEmpty()
                ? ShopRegistry.getByCatalog(catalogueStack)
                : List.of();

        baseItems = baseItems.stream()
                .filter(entry -> entry.getMode() == ShopEntry.ShopMode.PLAYER_BUYS)
                .toList();

        if (searchBox != null && !searchBox.getValue().isEmpty()) {
            String query = searchBox.getValue().toLowerCase(Locale.ROOT);

            if (!query.equals(lastSearch)) {
                scrollOffset = 0;
                lastSearch = query;
            }

            baseItems = baseItems.stream()
                    .filter(entry -> entry.getItem().getHoverName().getString().toLowerCase(Locale.ROOT).contains(query))
                    .toList();
        } else {
            lastSearch = "";
        }

        return baseItems;
    }

    private List<DisplayedItem> getVisibleItems() {
        List<DisplayedItem> visible = new ArrayList<>();
        List<ShopEntry> items = getFilteredItems();

        for (int i = 0; i < items.size(); i++) {
            int col = i % columns;
            int row = i / columns;

            if (row < scrollOffset) continue;
            if (row >= scrollOffset + maxVisibleRows) continue;

            int x = leftPos + startX + col * spacingX;
            int y = topPos + startY + (row - scrollOffset) * spacingY;
            visible.add(new DisplayedItem(items.get(i), x, y));
        }

        return visible;
    }

    public record DisplayedItem(ShopEntry entry, int x, int y) {}

    // Utility
    public static boolean isMouseOver(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public static boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}
