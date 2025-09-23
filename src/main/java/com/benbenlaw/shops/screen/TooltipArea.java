package com.benbenlaw.shops.screen;

public class TooltipArea {
    public int offsetX;
    public int offsetY;
    public int width;
    public int height;
    public String translationKey;

    public TooltipArea(int offsetX, int offsetY, int width, int height, String translationKey) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
        this.translationKey = translationKey;
    }
}
