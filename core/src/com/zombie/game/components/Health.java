package com.zombie.game.components;

public class Health implements IComponent {
    public float max;
    public float current;

    public Health(float max, float current) {
        this.max = max;
        this.current = current;
    }
}
