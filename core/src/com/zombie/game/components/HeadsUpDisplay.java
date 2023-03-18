package com.zombie.game.components;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class HeadsUpDisplay implements IComponent {
    public BitmapFont font;
    public Long playerId;

    public HeadsUpDisplay(Long playerId, Long cameraId) {
        this.playerId = playerId;
        font = new BitmapFont();
    }
}
