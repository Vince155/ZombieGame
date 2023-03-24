package com.zombie.game.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Player extends Sprite implements IComponent {
    public Movement movement;
    public Health health;
    public float score;
    public float weaponTimer;
    public float maxWeaponTimer;
    public float hitTimer;

    public Player(String sprite, float xStart, float yStart) {
        super(new Texture(sprite));
        this.movement = new Movement();
        this.setPosition(xStart, yStart);
        this.movement.position = new Vector2(this.getX(), this.getY());
        this.movement.velocity = new Vector2(150f, 150f);
        this.health = new Health(5f, 5f);
        this.score = 0f;
        this.weaponTimer = 0f;
        this.maxWeaponTimer = 0.4f;
        this.hitTimer = 1f;
    }
}
