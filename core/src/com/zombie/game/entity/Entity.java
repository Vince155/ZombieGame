package com.zombie.game.entity;

import static com.badlogic.gdx.math.MathUtils.random;

import com.zombie.game.components.IComponent;

import java.util.ArrayList;

public class Entity {
    public long guid;
    public ArrayList<IComponent> components;

    public Entity() {
        guid = random.nextLong();
        components = new ArrayList<>();
    }
}
