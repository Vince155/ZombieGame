package com.zombie.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zombie.game.components.Player;
import com.zombie.game.components.Projectile;
import com.zombie.game.entity.Entity;
import com.zombie.game.helpers.Logger;
import com.zombie.game.helpers.Utils;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectileSystem {
    private final HashMap<Long, Entity> projectileEntities;
    private final HashMap<Long, Entity> alliedEntities;

    public ProjectileSystem(
            HashMap<Long, Entity> projectileEntities,
            HashMap<Long, Entity> alliedEntities
    ) {
        this.projectileEntities = projectileEntities;
        this.alliedEntities = alliedEntities;
    }

    public void update(SpriteBatch batch) {
        PriorityQueue<Long> queue = new PriorityQueue<>();

        for (Entity entity: projectileEntities.values()) {
            Projectile projectile = getProjectileComponent(entity);

            if (projectile == null) {
                continue;
            }

            moveProjectile(entity);

            if (collidedWithLevel(projectile)) {
                queue.add(entity.guid);
            }

            render(batch, projectile);
        }

        while (queue.size() > 0) {
            Long projectileId = queue.poll();

            removeProjectileEntity(projectileId);
        }
    }

    public void dispose() {
        for (Entity projectileEntity: projectileEntities.values()) {
            Projectile projectile = getProjectileComponent(projectileEntity);

            if (projectile == null) {
                continue;
            }

            Texture projectileTexture = projectile.getTexture();
            projectileTexture.dispose();
        }
    }

    public void removeProjectileEntity(Long projectileId) {
        projectileEntities.remove(projectileId);
    }

    public void fireProjectile(Long playerId) {
        Entity projectileEntity = createProjectile(playerId);

        if (projectileEntity == null) {
            Logger.error("[ProjectileSystem] (fireProjectile method) Projectile unable to be fired");

            return;
        }

        projectileEntities.put(projectileEntity.guid, projectileEntity);
    }

    private boolean collidedWithLevel(Projectile projectile) {
        return projectile.movement.position.x > Gdx.graphics.getWidth() ||
                projectile.movement.position.x < 0f ||
                projectile.movement.position.y > Gdx.graphics.getHeight() ||
                projectile.movement.position.y < 0f;
    }

    private void render(SpriteBatch batch, Projectile projectile) {
        projectile.draw(batch);
    }

    private Entity createProjectile(Long playerId) {
        Entity entity = new Entity();
        Projectile projectile = new Projectile(
                "Bullet.png",
                600f,
                1f,
                false,
                playerId
        );
        Entity playerEntity = alliedEntities.get(playerId);
        Player player = (Player) Utils.getComponent(playerEntity, Player.class);

        if (player == null) {
            Logger.error("[ProjectileSystem] (createProjectile method) Player missing in player entity");

            return null;
        }

        float playerPosX = player.movement.position.x;
        float playerPosY = player.movement.position.y;
        projectile.setPosition(playerPosX + (player.getWidth() / 2f), playerPosY);
        projectile.setRotation(player.getRotation() + 90);
        entity.components.add(projectile);

        return entity;
    }

    private void moveProjectile(Entity projectileEntity) {
        Projectile projectile = getProjectileComponent(projectileEntity);

        if (projectile == null) {
            Logger.error("[ProjectileSystem] (moveProjectile method) projectile component missing");

            return;
        }

        float angle = projectile.getRotation() - 90f;
        double rad = angle * (Math.PI / 180);
        double velocityX = Math.cos(rad) * projectile.movement.velocity.x;
        double velocityY = Math.sin(rad) * projectile.movement.velocity.y;

        projectile.translateX((float) velocityX * Gdx.graphics.getDeltaTime());
        projectile.translateY((float) velocityY * Gdx.graphics.getDeltaTime());

        projectile.movement.position.x = projectile.getX();
        projectile.movement.position.y = projectile.getY();
    }

    private Projectile getProjectileComponent(Entity entity) {
        return (Projectile) Utils.getComponent(entity, Projectile.class);
    }
}
