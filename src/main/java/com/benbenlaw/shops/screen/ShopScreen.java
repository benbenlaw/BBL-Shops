package com.benbenlaw.shops.screen;

import com.benbenlaw.shops.attachments.ShopsAttachments;
import com.benbenlaw.shops.item.ShopsItems;
import com.benbenlaw.shops.network.packets.BuyShopItem;
import com.benbenlaw.shops.network.packets.SellShopItem;
import com.benbenlaw.shops.recipe.ShopEntryRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.*;

public class ShopScreen extends Screen {

    private enum Mode { BUY, SELL }

    private static final int ITEM_BOX_SIZE = 34;
    private static final int ITEM_ICON_SCALE = 2;
    private static final int BUY_BUTTON_HEIGHT = 14;
    private static final int SLOT_SPACING = 4;
    private static final int BUTTON_Y_OFFSET = -2;
    private static final int SLOT_WIDTH = ITEM_BOX_SIZE + SLOT_SPACING;
    private static final int SLOT_HEIGHT = ITEM_BOX_SIZE + BUY_BUTTON_HEIGHT + BUTTON_Y_OFFSET + 4;

    private static final int TIER_HEADER_HEIGHT = 18;
    private static final int TIER_SPACING = 10;

    private static final int MIN_COLUMNS = 4;
    private static final int MAX_COLUMNS = 12;
    private static final int MIN_VIEWPORT_ROWS = 2;
    private static final int MAX_VIEWPORT_ROWS = 6;

    private static final int PANEL_MARGIN = 10;
    private static final int TITLE_HEIGHT = 20;
    private static final int FOOTER_HEIGHT = 26;
    private static final int SCREEN_PADDING = 20;
    private static final int SCROLLBAR_WIDTH = 4;
    private static final int MODE_BUTTON_WIDTH = 50;

    private static final int PANEL_BACKGROUND = 0xFF1E1E1E;
    private static final int PANEL_BORDER = 0xFF444444;
    private static final int BOX_BACKGROUND = 0xFF2B2B2B;
    private static final int BOX_BORDER = 0xFF555555;
    private static final int BUY_BUTTON_COLOR = 0xFF3A8F3A;
    private static final int BUY_BUTTON_HOVER_COLOR = 0xFF4CAF50;
    private static final int BUY_BUTTON_BORDER = 0xFF1F5C1F;
    private static final int BUY_BUTTON_DISABLED_COLOR = 0xFF4A2E2E;
    private static final int BUY_BUTTON_DISABLED_BORDER = 0xFF6B3B3B;
    private static final int SELL_BUTTON_COLOR = 0xFF2E5F8F;
    private static final int SELL_BUTTON_HOVER_COLOR = 0xFF3D7AB5;
    private static final int SELL_BUTTON_BORDER = 0xFF1A3A57;
    private static final int PRICE_UNAFFORDABLE_COLOR = 0xFFFF5555;
    private static final int TIER_HEADER_COLOR = 0xFFFFD700;
    private static final int SCROLLBAR_TRACK_COLOR = 0xFF141414;
    private static final int SCROLLBAR_THUMB_COLOR = 0xFF777777;

    private final List<Map.Entry<Identifier, ShopEntryRecipe>> allEntries;
    private List<Map.Entry<Identifier, ShopEntryRecipe>> filteredEntries;

    private String searchText = "";
    private int scrollOffset = 0;
    private Mode mode = Mode.BUY;

    private int gridColumns;
    private int gridViewportHeight;
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;
    private int gridX;
    private int gridY;

    private final List<PlacedHeader> placedHeaders = new ArrayList<>();
    private final List<PlacedItem> placedItems = new ArrayList<>();
    private int totalContentHeight = 0;

    public ShopScreen(Component title, Map<Identifier, ShopEntryRecipe> entries) {
        super(title);
        this.allEntries = new ArrayList<>(entries.entrySet());
        this.filteredEntries = this.allEntries;
    }

    @Override
    protected void init() {
        super.init();

        int availableWidth = Math.max(width - SCREEN_PADDING * 2, SLOT_WIDTH * MIN_COLUMNS);
        int availableHeight = Math.max(height - SCREEN_PADDING * 2, SLOT_HEIGHT * MIN_VIEWPORT_ROWS + TITLE_HEIGHT + FOOTER_HEIGHT);

        gridColumns = Mth.clamp((availableWidth - PANEL_MARGIN * 2) / SLOT_WIDTH, MIN_COLUMNS, MAX_COLUMNS);
        int viewportRows = Mth.clamp((availableHeight - PANEL_MARGIN * 2 - TITLE_HEIGHT - FOOTER_HEIGHT) / SLOT_HEIGHT, MIN_VIEWPORT_ROWS, MAX_VIEWPORT_ROWS);

        panelWidth = gridColumns * SLOT_WIDTH - SLOT_SPACING + PANEL_MARGIN * 2;
        panelHeight = viewportRows * SLOT_HEIGHT - SLOT_SPACING + PANEL_MARGIN * 2 + TITLE_HEIGHT + FOOTER_HEIGHT;

        panelX = (width - panelWidth) / 2;
        panelY = (height - panelHeight) / 2;

        gridX = panelX + PANEL_MARGIN;
        gridY = panelY + PANEL_MARGIN + TITLE_HEIGHT;
        gridViewportHeight = viewportRows * SLOT_HEIGHT - SLOT_SPACING;

        int footerY = panelY + panelHeight - FOOTER_HEIGHT - 3;
        int searchWidth = panelWidth - PANEL_MARGIN * 2 - MODE_BUTTON_WIDTH - 4;

        EditBox searchBox = new EditBox(Minecraft.getInstance().font,
                panelX + PANEL_MARGIN, footerY, searchWidth, 20, Component.literal("Search"));
        searchBox.setTooltip(Tooltip.create(Component.translatable("tooltip.shops.search_bar")));
        searchBox.setResponder(this::onSearchChanged);
        addRenderableWidget(searchBox);

        addRenderableWidget(Button.builder(modeButtonLabel(), b -> toggleMode(b))
                .bounds(panelX + panelWidth - PANEL_MARGIN - MODE_BUTTON_WIDTH, footerY, MODE_BUTTON_WIDTH, 20)
                .build());

        applyFilters();
    }

    private Component modeButtonLabel() {
        return mode == Mode.BUY
                ? Component.translatable("tooltip.shops.mode_buy")
                : Component.translatable("tooltip.shops.mode_sell");
    }

    private void toggleMode(Button button) {
        mode = (mode == Mode.BUY) ? Mode.SELL : Mode.BUY;
        button.setMessage(modeButtonLabel());
        scrollOffset = 0;
        applyFilters();
    }

    private void onSearchChanged(String value) {
        searchText = value.toLowerCase();
        scrollOffset = 0;
        applyFilters();
    }

    private void applyFilters() {
        filteredEntries = allEntries.stream()
                .filter(e -> mode == Mode.BUY ? e.getValue().buyPrice() > 0 : e.getValue().sellPrice() > 0)
                .filter(e -> searchText.isEmpty()
                        || e.getValue().stack().create().getHoverName().getString().toLowerCase().contains(searchText))
                .toList();
        rebuildLayout();
    }

    private void rebuildLayout() {
        placedHeaders.clear();
        placedItems.clear();

        Player player = Minecraft.getInstance().player;
        Set<String> unlocked = player != null
                ? Set.of(player.getData(ShopsAttachments.PLAYER_BALANCE).stages())
                : Set.of();

        Map<String, List<Map.Entry<Identifier, ShopEntryRecipe>>> byTier = new LinkedHashMap<>();
        for (Map.Entry<Identifier, ShopEntryRecipe> entry : filteredEntries) {
            String tier = entry.getValue().tier();
            if (!tier.isEmpty() && !unlocked.contains(tier)) continue;
            byTier.computeIfAbsent(tier, t -> new ArrayList<>()).add(entry);
        }

        int currentY = 0;
        for (Map.Entry<String, List<Map.Entry<Identifier, ShopEntryRecipe>>> tierGroup : byTier.entrySet()) {
            String tier = tierGroup.getKey();
            List<Map.Entry<Identifier, ShopEntryRecipe>> entries = tierGroup.getValue();
            entries.sort(Comparator.comparingInt(e -> e.getValue().order()));

            String namespace = entries.getFirst().getKey().getNamespace();
            Component headerText = Component.translatable("shop_tier." + namespace + "." + (tier.isEmpty() ? "general" : tier));
            placedHeaders.add(new PlacedHeader(headerText, currentY));
            currentY += TIER_HEADER_HEIGHT;

            for (int i = 0; i < entries.size(); i++) {
                int col = i % gridColumns;
                int row = i / gridColumns;
                int itemX = col * SLOT_WIDTH;
                int itemY = currentY + row * SLOT_HEIGHT;
                placedItems.add(new PlacedItem(entries.get(i), itemX, itemY));
            }
            int rowCount = (entries.size() + gridColumns - 1) / gridColumns;
            currentY += rowCount * SLOT_HEIGHT + TIER_SPACING;
        }

        totalContentHeight = currentY;
        scrollOffset = Mth.clamp(scrollOffset, 0, maxScroll());
    }

    private int maxScroll() {
        return Math.max(0, totalContentHeight - gridViewportHeight);
    }

    private boolean playerHasItem(Player player, ItemStack wanted) {
        if (player == null) return false;
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (!stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, wanted)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, PANEL_BACKGROUND);
        graphics.outline(panelX, panelY, panelWidth, panelHeight, PANEL_BORDER);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.text(Minecraft.getInstance().font, Component.translatable("menu.shops.shop"),
                panelX + PANEL_MARGIN, panelY + 8, 0xFFFFFFFF, true);

        Player player = Minecraft.getInstance().player;
        int playerBalance = player != null ? player.getData(ShopsAttachments.PLAYER_BALANCE).getBalance() : 0;

        Component balanceText = Component.translatable("tooltip.shops.balance", playerBalance);
        int balanceTextWidth = Minecraft.getInstance().font.width(balanceText);
        int balanceIconWidth = 16;
        int balanceGroupWidth = balanceIconWidth + 4 + balanceTextWidth;

        int balanceX = panelX + panelWidth - PANEL_MARGIN - balanceGroupWidth;
        int balanceIconY = panelY + 4;

        graphics.item(ShopsItems.GOLD_COIN.get().getDefaultInstance(), balanceX, balanceIconY);
        graphics.text(Minecraft.getInstance().font, balanceText,
                balanceX + balanceIconWidth + 4, panelY + 8, 0xFFFFFFFF, true);

        int viewportTop = gridY;
        int viewportBottom = gridY + gridViewportHeight;
        graphics.enableScissor(gridX, viewportTop, gridX + gridColumns * SLOT_WIDTH, viewportBottom);

        for (PlacedHeader header : placedHeaders) {
            int screenY = gridY + header.y() - scrollOffset;
            if (screenY + TIER_HEADER_HEIGHT < viewportTop || screenY > viewportBottom) continue;
            graphics.text(Minecraft.getInstance().font, header.text(), gridX, screenY + 4, TIER_HEADER_COLOR, true);
        }

        Map.Entry<Identifier, ShopEntryRecipe> hoveredSlot = null;

        for (PlacedItem placed : placedItems) {
            int boxX = gridX + placed.x();
            int boxY = gridY + placed.y() - scrollOffset;
            int buttonX = boxX;
            int buttonY = boxY + ITEM_BOX_SIZE + BUTTON_Y_OFFSET;
            int buttonWidth = ITEM_BOX_SIZE;

            if (boxY + SLOT_HEIGHT < viewportTop || boxY > viewportBottom) continue;

            boolean slotHovered = mouseX >= boxX && mouseX < boxX + ITEM_BOX_SIZE
                    && mouseY >= boxY && mouseY < buttonY + BUY_BUTTON_HEIGHT
                    && mouseY >= viewportTop && mouseY < viewportBottom;

            ShopEntryRecipe recipe = placed.entry().getValue();
            ItemStack stack = recipe.stack().create();
            int price = mode == Mode.BUY ? recipe.buyPrice() : recipe.sellPrice();
            boolean canInteract = mode == Mode.BUY
                    ? playerBalance >= price
                    : playerHasItem(player, stack);

            graphics.fill(boxX, boxY, boxX + ITEM_BOX_SIZE, boxY + ITEM_BOX_SIZE, BOX_BACKGROUND);
            graphics.outline(boxX, boxY, ITEM_BOX_SIZE, ITEM_BOX_SIZE, BOX_BORDER);

            graphics.pose().pushMatrix();
            graphics.pose().translate(boxX + ITEM_BOX_SIZE / 2f, boxY + ITEM_BOX_SIZE / 2f);
            graphics.pose().scale(ITEM_ICON_SCALE, ITEM_ICON_SCALE);
            graphics.item(stack, -8, -8);
            graphics.pose().popMatrix();

            int normalColor = mode == Mode.BUY ? BUY_BUTTON_COLOR : SELL_BUTTON_COLOR;
            int hoverColor = mode == Mode.BUY ? BUY_BUTTON_HOVER_COLOR : SELL_BUTTON_HOVER_COLOR;
            int borderColor = mode == Mode.BUY ? BUY_BUTTON_BORDER : SELL_BUTTON_BORDER;

            int buttonColor = !canInteract ? BUY_BUTTON_DISABLED_COLOR : (slotHovered ? hoverColor : normalColor);
            int buttonBorder = !canInteract ? BUY_BUTTON_DISABLED_BORDER : borderColor;
            int priceColor = canInteract ? 0xFFFFFFFF : PRICE_UNAFFORDABLE_COLOR;

            graphics.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + BUY_BUTTON_HEIGHT, buttonColor);
            graphics.outline(buttonX, buttonY, buttonWidth, BUY_BUTTON_HEIGHT, buttonBorder);
            graphics.centeredText(Minecraft.getInstance().font, Component.literal(String.valueOf(price)),
                    buttonX + buttonWidth / 2, buttonY + 3, priceColor);

            if (slotHovered) {
                hoveredSlot = placed.entry();
            }
        }

        graphics.disableScissor();

        if (maxScroll() > 0) {
            int trackX = gridX + gridColumns * SLOT_WIDTH - SLOT_SPACING + 4;
            graphics.fill(trackX, viewportTop, trackX + SCROLLBAR_WIDTH, viewportBottom, SCROLLBAR_TRACK_COLOR);

            int thumbHeight = Math.max(10, gridViewportHeight * gridViewportHeight / totalContentHeight);
            int thumbY = viewportTop + (gridViewportHeight - thumbHeight) * scrollOffset / maxScroll();
            graphics.fill(trackX, thumbY, trackX + SCROLLBAR_WIDTH, thumbY + thumbHeight, SCROLLBAR_THUMB_COLOR);
        }

        if (hoveredSlot != null) {
            ShopEntryRecipe recipe = hoveredSlot.getValue();
            ItemStack stack = recipe.stack().create();

            List<ClientTooltipComponent> lines = new ArrayList<>();
            for (Component line : Screen.getTooltipFromItem(Minecraft.getInstance(), stack)) {
                lines.add(ClientTooltipComponent.create(line.getVisualOrderText()));
            }
            lines.add(ClientTooltipComponent.create(Component.translatable("tooltip.shops.buy_price", recipe.buyPrice()).getVisualOrderText()));
            lines.add(ClientTooltipComponent.create(Component.translatable("tooltip.shops.sell_price", recipe.sellPrice()).getVisualOrderText()));

            graphics.tooltip(Minecraft.getInstance().font, lines, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null, stack);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        int viewportTop = gridY;
        int viewportBottom = gridY + gridViewportHeight;

        Player player = Minecraft.getInstance().player;
        int playerBalance = player != null ? player.getData(ShopsAttachments.PLAYER_BALANCE).getBalance() : 0;

        for (PlacedItem placed : placedItems) {
            int boxY = gridY + placed.y() - scrollOffset;
            if (boxY + SLOT_HEIGHT < viewportTop || boxY > viewportBottom) continue;

            int boxX = gridX + placed.x();
            int buttonY = boxY + ITEM_BOX_SIZE + BUTTON_Y_OFFSET;

            if (event.x() >= boxX && event.x() < boxX + ITEM_BOX_SIZE
                    && event.y() >= boxY && event.y() < buttonY + BUY_BUTTON_HEIGHT
                    && event.y() >= viewportTop && event.y() < viewportBottom) {

                ShopEntryRecipe recipe = placed.entry().getValue();
                if (mode == Mode.BUY) {
                    if (playerBalance >= recipe.buyPrice()) {
                        ClientPacketDistributor.sendToServer(new BuyShopItem(placed.entry().getKey()));
                    }
                } else {
                    if (playerHasItem(player, recipe.stack().create())) {
                        ClientPacketDistributor.sendToServer(new SellShopItem(placed.entry().getKey()));
                    }
                }
                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX >= gridX && mouseX < gridX + gridColumns * SLOT_WIDTH
                && mouseY >= gridY && mouseY < gridY + gridViewportHeight) {
            scrollOffset = Mth.clamp(scrollOffset - (int) (scrollY * SLOT_HEIGHT / 2), 0, maxScroll());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record PlacedHeader(Component text, int y) {}

    private record PlacedItem(Map.Entry<Identifier, ShopEntryRecipe> entry, int x, int y) {}
}