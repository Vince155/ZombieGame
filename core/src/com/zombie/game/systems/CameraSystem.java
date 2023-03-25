package com.zombie.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.zombie.game.components.Camera;
import com.zombie.game.components.Player;
import com.zombie.game.entity.Entity;
import com.zombie.game.helpers.Utils;

public class CameraSystem {
    private final Entity cameraEntity;
    private final Entity playerEntity;

    public CameraSystem(Entity cameraEntity, Entity playerEntity) {
        this.cameraEntity = cameraEntity;
        Camera camera = (Camera) Utils.getComponent(this.cameraEntity, Camera.class);
        this.playerEntity = playerEntity;
        Player player = (Player) Utils.getComponent(this.playerEntity, Player.class);

        camera.cam.position.set(
                player.movement.position.x,
                player.movement.position.y,
                0
        );

        camera.cam.update();
    }

    public void update(SpriteBatch batch) {
            render(batch);
    }

    private void render(SpriteBatch batch) {
        Camera camera = (Camera) Utils.getComponent(cameraEntity, Camera.class);
        Player player = (Player) Utils.getComponent(playerEntity, Player.class);

        if (camera == null || player == null) {
            return;
        }

        batch.setProjectionMatrix(camera.cam.combined);
        followPlayer(camera, player);
    }

    private void followPlayer(Camera camera, Player player) {
        float lerp = 15f;
        Vector3 camPosition = camera.cam.position;
        Vector2 playerPosition = player.movement.position;
        camPosition.x += (playerPosition.x - camPosition.x) * lerp * Gdx.graphics.getDeltaTime();
        camPosition.y += (playerPosition.y - camPosition.y) * lerp * Gdx.graphics.getDeltaTime();

        camera.cam.update();
    }
}
