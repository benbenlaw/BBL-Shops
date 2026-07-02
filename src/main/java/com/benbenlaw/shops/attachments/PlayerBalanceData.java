package com.benbenlaw.shops.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public record PlayerBalanceData(int balance, String[] stages) {

    public static final Codec<PlayerBalanceData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("balance").forGetter(PlayerBalanceData::balance),
            Codec.STRING.listOf()
                    .xmap(list -> list.toArray(new String[0]), Arrays::asList)
                    .fieldOf("stages").forGetter(PlayerBalanceData::stages)
    ).apply(instance, PlayerBalanceData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerBalanceData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PlayerBalanceData::balance,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
            data -> Arrays.asList(data.stages()),
            (balance, stagesList) -> new PlayerBalanceData(balance, stagesList.toArray(new String[0]))
    );

    public int getBalance() {
        return balance;
    }

    public String[] getStages() {
        return stages;
    }

    public PlayerBalanceData addBalance(int amount) {
        return new PlayerBalanceData(balance + amount, stages);
    }

    public PlayerBalanceData subtractBalance(int amount) {
        return new PlayerBalanceData(Math.max(0, balance - amount), stages);
    }

    public PlayerBalanceData setBalance(int amount) {
        return new PlayerBalanceData(amount, stages);
    }

    public boolean hasStage(String stage) {
        return Arrays.asList(stages).contains(stage);
    }

    public PlayerBalanceData addStage(String stage) {
        if (hasStage(stage)) return this;

        Set<String> merged = new LinkedHashSet<>(Arrays.asList(stages));
        merged.add(stage);
        return new PlayerBalanceData(balance, merged.toArray(new String[0]));
    }

    public PlayerBalanceData removeStage(String stage) {
        if (!hasStage(stage)) return this;

        Set<String> remaining = new LinkedHashSet<>(Arrays.asList(stages));
        remaining.remove(stage);
        return new PlayerBalanceData(balance, remaining.toArray(new String[0]));
    }

}