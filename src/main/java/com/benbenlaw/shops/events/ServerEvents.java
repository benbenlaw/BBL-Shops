package com.benbenlaw.shops.events;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.attachments.PlayerBalanceData;
import com.benbenlaw.shops.attachments.ShopTraderData;
import com.benbenlaw.shops.attachments.ShopsAttachments;
import com.benbenlaw.shops.datamaps.ShopsDataMaps;
import com.benbenlaw.shops.entity.ShopsEntityTypes;
import com.benbenlaw.shops.events.client.ClientRecipeCache;
import com.benbenlaw.shops.network.packets.OpenShopTraderScreen;
import com.benbenlaw.shops.network.packets.SyncPlayerBalanceToClient;
import com.benbenlaw.shops.recipe.ShopEntryRecipe;
import com.benbenlaw.shops.recipe.ShopsRecipeTypes;
import com.benbenlaw.shops.util.ShopsTags;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ShopsEntityTypes.SHOP_TRADER.get(), Villager.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerDataMaps(RegisterDataMapTypesEvent event) {
        event.register(ShopsDataMaps.TRADER_NAMES);
    }
}
