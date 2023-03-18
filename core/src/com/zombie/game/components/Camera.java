package com.zombie.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Camera implements IComponent {
    public OrthographicCamera cam;
    public Long playerId;

    public Camera(Long playerId) {
        this.playerId = playerId;
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        cam = new OrthographicCamera(width, height);
    }
}
