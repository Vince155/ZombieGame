package com.zombie.game.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends Sprite implements IComponent {
    public Long allyId;
    public Movement movement;
    public Health health;
    public float value;

    public Enemy(
            String sprite,
            float xStart,
            float yStart,
            Long allyId,
            float maxHealth,
            float value,
            float speed
    ) {
        super(new Texture(sprite));
        this.movement = new Movement();
        this.movement.position = new Vector2(xStart, yStart);
        this.setPosition(xStart, yStart);
        this.movement.position.x = this.getX();
        this.movement.position.y = this.getY();
        this.movement.velocity = new Vector2(speed, speed);
        this.health = new Health(maxHealth, maxHealth);
        this.allyId = allyId;
        this.value = value;
    }
}
