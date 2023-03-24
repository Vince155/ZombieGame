package com.zombie.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.zombie.game.components.Camera;
import com.zombie.game.components.Enemy;
import com.zombie.game.components.Player;
import com.zombie.game.components.Projectile;
import com.zombie.game.entity.Entity;
import com.zombie.game.helpers.Utils;

import java.util.HashMap;
import java.util.PriorityQueue;

public class PlayerSystem {
    private final HashMap<Long, Entity> entities;
    private final Entity cameraEntity;
    private final ProjectileSystem projectileSystem;
    private final HashMap<Long, Entity> projectileEntities;
    private final HashMap<Long, Entity> enemyEntities;


    public PlayerSystem(
            HashMap<Long, Entity> entities,
            HashMap<Long, Entity> projectileEntities,
            HashMap<Long, Entity> enemyEntities,

            Entity cameraEntity,
            ProjectileSystem projectileSystem
    ) {
        this.entities = entities;
        this.projectileEntities = projectileEntities;
        this.enemyEntities = enemyEntities;
        this.cameraEntity = cameraEntity;
        this.projectileSystem = projectileSystem;
    }

    public void update(SpriteBatch batch) {
        for (Entity entity: entities.values()) {
            render(batch, entity);
            lowerProjectileTimer(entity);
            handleMovement(entity);
            rotateTowardsMouse(entity);

            Player player = (Player) Utils.getComponent(entity, Player.class);

            if (player == null) {
                continue;
            }

            player.hitTimer -= Gdx.graphics.getDeltaTime();

            if ((isHitByProjectile(player) || isHitByZombie(player)) && player.hitTimer <= 0f) {
                player.health.current--;
                player.hitTimer = 1f;
            }

            if (isDead(player)) {
                System.out.println("Player is dead");
            }
        }
    }

    public void increaseScore(float value, Player player) {
        player.score += value;
    }

    private void handleMovement(Entity entity) {
        Player player = (Player) Utils.getComponent(entity, Player.class);

        if (player == null) {
           return;
        }

        float speedX = player.movement.velocity.x;
        float speedY = player.movement.velocity.y;
        player.movement.position.x = player.getX();
        player.movement.position.y = player.getY();
        Rectangle boundingRectangle = player.getBoundingRectangle();
        float bottomBound = player.movement.position.y;
        float leftBound = player.movement.position.x;
        float topBound = boundingRectangle.height + bottomBound;
        float rightBound = boundingRectangle.width + leftBound;

        if (Gdx.input.isKeyPressed(Input.Keys.W) && topBound < Gdx.graphics.getHeight()) {
            player.translateY(speedY * Gdx.graphics.getDeltaTime());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A) && leftBound > 0f) {
            player.translateX(-speedX * Gdx.graphics.getDeltaTime());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D) && rightBound < Gdx.graphics.getWidth()) {
            player.translateX(speedX * Gdx.graphics.getDeltaTime());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S) && bottomBound > 0f) {
            player.translateY(-speedY * Gdx.graphics.getDeltaTime());
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && player.weaponTimer <= 0f) {
            projectileSystem.fireProjectile(entity.guid);
            resetWeaponTimer(player);
        }
    }

    private void rotateTowardsMouse(Entity playerEntity) {
        Player player = (Player) Utils.getComponent(playerEntity, Player.class);
        Camera camera = (Camera) Utils.getComponent(cameraEntity, Camera.class);
        Vector3 mousePos = camera.cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector3 playerPos = new Vector3(
                player.getX() + player.getWidth() / 2f,
                player.getY() + player.getHeight() / 2f,
                0f
        );
        float angle = (float) MathUtils.atan2(mousePos.y - playerPos.y, mousePos.x - playerPos.x);
        float degrees = (float) (angle * (180 / Math.PI));

        player.setRotation(degrees);
    }

    private void render(SpriteBatch batch, Entity entity) {
        Player player = (Player) Utils.getComponent(entity, Player.class);

        if (player == null) {
            return;
        }

        player.draw(batch);
    }

    private void lowerProjectileTimer(Entity entity) {
        Player player = (Player) Utils.getComponent(entity, Player.class);

        if (player == null) {
            return;
        }

        player.weaponTimer -= Gdx.graphics.getDeltaTime();
    }

    private void resetWeaponTimer(Player player) {
        player.weaponTimer = player.maxWeaponTimer;
    }

    private boolean isHitByProjectile(Player player) {
        PriorityQueue<Long> queue = new PriorityQueue<>();
        boolean isHit = false;

        for (Entity projectileEntity: projectileEntities.values()) {
            Projectile projectile = (Projectile) Utils.getComponent(projectileEntity, Projectile.class);

            if (projectile == null) {
                continue;
            }

            Long boxId = projectile.boxId;
            Entity bossEntity = enemyEntities.get(boxId);

            if (bossEntity == null) {
                continue;
            }

            Rectangle projectileBoundingRectangle = projectile.getBoundingRectangle();
            Rectangle playerBoundingRectangle = player.getBoundingRectangle();

            if (projectileBoundingRectangle.overlaps(playerBoundingRectangle)) {
                queue.add(projectileEntity.guid);
                isHit = true;
            }
        }

        while (queue.size() > 0) {
            Long projectileId = queue.poll();

            projectileSystem.removeProjectileEntity(projectileId);
        }

        return isHit;
    }

    private boolean isHitByZombie(Player player) {
        boolean isHit = false;

        for (Entity enemyEntity: enemyEntities.values()) {
            Enemy enemy = (Enemy) Utils.getComponent(enemyEntity, Enemy.class);

            if (enemy == null) {
                continue;
            }

            Rectangle enemyBoundingRectangle = enemy.getBoundingRectangle();
            Rectangle playerBoundingRectangle = player.getBoundingRectangle();

            if (playerBoundingRectangle.overlaps(enemyBoundingRectangle)) {
                isHit = true;
            }
        }

        return isHit;
    }

    private boolean isDead(Player player) {
        return player.health.current <= 0f;
    }
}
