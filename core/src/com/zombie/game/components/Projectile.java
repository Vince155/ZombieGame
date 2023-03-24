package com.zombie.game.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.zombie.game.entity.Entity;

public class Projectile extends Sprite implements IComponent {
    public Movement movement;
    public float damage;
    public boolean explosive;
    public long boxId;

    public Projectile(
            String sprite,
            float speed,
            float damage,
            boolean explosive,
            long boxId
    ) {
        super(new Texture(sprite));
        this.boxId = boxId;
        this.explosive = explosive;
        this.damage = damage;
        this.movement = new Movement();
        this.movement.position = new Vector2(this.getX(), getY());
        this.movement.velocity = new Vector2(speed, speed);
    }
}
