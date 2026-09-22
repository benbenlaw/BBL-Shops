package com.benbenlaw.shops.data;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.block.ShopsBlocks;
import com.benbenlaw.shops.item.ShopsItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static net.minecraft.client.data.models.BlockModelGenerators.*;
import static net.minecraft.client.data.models.BlockModelGenerators.X_ROT_180;
import static net.minecraft.client.data.models.BlockModelGenerators.Y_ROT_180;
import static net.minecraft.client.data.models.BlockModelGenerators.Y_ROT_270;
import static net.minecraft.client.data.models.BlockModelGenerators.Y_ROT_90;

public class ShopsModelProvider extends ModelProvider {

    public ShopsModelProvider(PackOutput output) {
        super(output, Shops.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

        itemModels.generateFlatItem(ShopsItems.COPPER_COIN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ShopsItems.IRON_COIN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ShopsItems.GOLD_COIN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ShopsItems.PLAYER_BALANCE_CARD.get(), ModelTemplates.FLAT_ITEM);

        createMachineBlock(ShopsBlocks.SHOP_BLOCK.get(), blockModels.blockStateOutput, blockModels.modelOutput);

    }

    public void createMachineBlock(Block block, Consumer<BlockModelDefinitionGenerator> blockStateOutput, BiConsumer<Identifier, ModelInstance> modelOutput) {
        TextureMapping idleTextureMapping = (new TextureMapping()).put(TextureSlot.TOP, new Material(Shops.identifier("block/machine_top"))).put(TextureSlot.SIDE, new Material(Shops.identifier("block/machine_side_idle"))).put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_front"));

        MultiVariant multivariant = plainVariant(ModelTemplates.CUBE_ORIENTABLE.create(block, idleTextureMapping, modelOutput));
        MultiVariant multivariant1 = plainVariant(ModelTemplates.CUBE_ORIENTABLE_VERTICAL.create(block, idleTextureMapping, modelOutput));

        blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(PropertyDispatch.initial(BlockStateProperties.FACING)
                                .select(Direction.DOWN, multivariant1.with(X_ROT_180))
                                .select(Direction.UP,  multivariant1)
                                .select(Direction.NORTH, multivariant)
                                .select(Direction.EAST, multivariant.with(Y_ROT_90))
                                .select(Direction.SOUTH, multivariant.with(Y_ROT_180))
                                .select(Direction.WEST, multivariant.with(Y_ROT_270))
                        ));
    }


    @Override
    protected @NotNull Stream<? extends Holder<Item>> getKnownItems() {
        return ShopsItems.ITEMS.getEntries().stream();
    }
}