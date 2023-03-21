package com.zombie.game.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Level extends Sprite implements IComponent {
    public float width;
    public float height;
    public float time;

    public Level(String sprite) {
        super(new Texture(sprite));
    }
}
