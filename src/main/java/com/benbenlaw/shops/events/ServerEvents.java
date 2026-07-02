package com.benbenlaw.shops.events;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.attachments.PlayerBalanceData;
import com.benbenlaw.shops.attachments.ShopsAttachments;
import com.benbenlaw.shops.events.client.ClientRecipeCache;
import com.benbenlaw.shops.network.packets.SyncPlayerBalanceToClient;
import com.benbenlaw.shops.recipe.ShopEntryRecipe;
import com.benbenlaw.shops.recipe.ShopsRecipeTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Shops.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (!level.isClientSide()) {
            PlayerBalanceData data = player.getData(ShopsAttachments.PLAYER_BALANCE);
            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncPlayerBalanceToClient(data.getBalance()));
        }
    }

    @SubscribeEvent
    public static void syncPlayerBalance(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (!level.isClientSide()) {
            PlayerBalanceData data = player.getData(ShopsAttachments.PLAYER_BALANCE);
            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncPlayerBalanceToClient(data.getBalance()));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player oldPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();

            int count = oldPlayer.getData(ShopsAttachments.PLAYER_BALANCE).getBalance();
            newPlayer.getData(ShopsAttachments.PLAYER_BALANCE).setBalance(count);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (!level.isClientSide()) {
            PlayerBalanceData data = player.getData(ShopsAttachments.PLAYER_BALANCE);
            data.addStage("stone");
            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncPlayerBalanceToClient(data.getBalance()));
        }
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(ShopsRecipeTypes.SHOP_TYPE.get());
    }

    @SubscribeEvent
    public static void onRecipeReceived(RecipesReceivedEvent event) {
        RecipeMap recipeMap = event.getRecipeMap();

        //Shop
        Collection<RecipeHolder<ShopEntryRecipe>> meltingRecipe = recipeMap.byType(ShopsRecipeTypes.SHOP_TYPE.get());
        Map<Identifier, ShopEntryRecipe> meltingRecipeMap = new HashMap<>();

        for (RecipeHolder<ShopEntryRecipe> recipeHolder : meltingRecipe) {
            meltingRecipeMap.put(recipeHolder.id().identifier(), recipeHolder.value());
        }
        ClientRecipeCache.setCachedShopRecipes(meltingRecipeMap);
    }

}
