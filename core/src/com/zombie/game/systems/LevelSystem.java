package com.zombie.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zombie.game.components.Level;
import com.zombie.game.entity.Entity;
import com.zombie.game.helpers.Utils;

public class LevelSystem extends ScreenAdapter {
    private final Entity levelEntity;

    public LevelSystem(Entity levelEntity) {
        this.levelEntity = levelEntity;
    }

    public void update(SpriteBatch batch) {
        render(batch);
    }

    private void render(SpriteBatch batch) {
        Level level = (Level) Utils.getComponent(levelEntity, Level.class);

        if (level == null) {
            return;
        }

        level.time += Gdx.graphics.getDeltaTime();
        level.draw(batch);
    }

    @Override
    public void dispose() {
        Level level = (Level) Utils.getComponent(levelEntity, Level.class);

        if (level == null) {
            return;
        }

        Texture levelTexture = level.getTexture();
        levelTexture.dispose();
    }
}
