package com.zombie.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zombie.game.components.Camera;
import com.zombie.game.components.HeadsUpDisplay;
import com.zombie.game.components.Player;
import com.zombie.game.entity.Entity;
import com.zombie.game.helpers.Utils;

import java.util.HashMap;

public class HeadsUpDisplaySystem {
    private final HashMap<Long, Entity> playerEntities;
    private final Entity cameraEntity;
    private final Entity hudEntity;

    public HeadsUpDisplaySystem(
            HashMap<Long, Entity> playerEntities,
            Entity cameraEntity,
            Entity hudEntity
    ) {
        this.playerEntities = playerEntities;
        this.cameraEntity = cameraEntity;
        this.hudEntity = hudEntity;
    }

    public void update(SpriteBatch batch) {
        Camera camera = (Camera) Utils.getComponent(cameraEntity, Camera.class);
        HeadsUpDisplay headsUpDisplay = (HeadsUpDisplay) Utils.getComponent(hudEntity, HeadsUpDisplay.class);

        if (headsUpDisplay == null || camera == null) {
            return;
        }

        Long playerId = headsUpDisplay.playerId;
        Entity playerEntity = playerEntities.get(playerId);

        if (playerEntity == null) {
            return;
        }

        Player player = (Player) Utils.getComponent(playerEntity, Player.class);

        if (player == null) {
            return;
        }

        render(
                batch,
                player,
                camera,
                headsUpDisplay
        );
    }

    private void render(
            SpriteBatch batch,
            Player player,
            Camera camera,
            HeadsUpDisplay headsUpDisplay
    ) {
        BitmapFont font = headsUpDisplay.font;
        font.getData().setScale(2f, 2f);
        font.draw(
                batch,
                "Score: " + player.score,
                camera.cam.position.x - (camera.cam.viewportWidth / 2f),
                font.getLineHeight() + camera.cam.position.y - (camera.cam.viewportHeight / 2f)
        );
        font.draw(
                batch,
                "Health: " + player.health.current + " / " + player.health.max,
                camera.cam.position.x - (camera.cam.viewportWidth / 2f),
                font.getLineHeight() + camera.cam.position.y - (camera.cam.viewportHeight / 2.5f)
        );
    }
}
