package com.zombie.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.zombie.game.components.Enemy;
import com.zombie.game.components.Level;
import com.zombie.game.components.Player;
import com.zombie.game.components.Projectile;
import com.zombie.game.entity.Entity;
import com.zombie.game.helpers.Logger;
import com.zombie.game.helpers.Utils;

import java.util.HashMap;
import java.util.PriorityQueue;

public class EnemySystem {
    private float enemyTimer;
    private float bigEnemyTimer;
    private float fastEnemyTimer;
    private float bossTimer;
    private boolean bigEnemyInPlay;
    private boolean fastEnemyInPlay;
    private boolean bossInPlay;
    private final HashMap<Long, Entity> enemyEntities;
    private final HashMap<Long, Entity> allyEntities;
    private final HashMap<Long, Entity> projectileEntities;
    private final Entity levelEntity;
    private final ProjectileSystem projectileSystem;
    private final PlayerSystem playerSystem;

    public EnemySystem(
            HashMap<Long, Entity> enemyEntities,
            Entity levelEntity,
            HashMap<Long, Entity> allyEntities,
            HashMap<Long, Entity> projectileEntities,
            ProjectileSystem projectileSystem,
            PlayerSystem playerSystem
    ) {
        this.enemyEntities = enemyEntities;
        this.levelEntity = levelEntity;
        this.allyEntities = allyEntities;
        this.projectileEntities = projectileEntities;
        this.projectileSystem = projectileSystem;
        this.playerSystem = playerSystem;
        enemyTimer = 5f;
    }

    public void update(SpriteBatch batch) {
        if (enemyTimer <= 0f) {
            Entity relevantEntity = null;
            int random = MathUtils.random(allyEntities.size());

            for (Entity entity: allyEntities.values()) {
                if (random == 0 || allyEntities.size() == 1) {
                    relevantEntity = entity;

                    break;
                }

                random--;
            }

            if (relevantEntity == null) {
                Logger.error("No allies exist");

                return;
            }

            Entity newEnemyEntity = createEnemy(
                    relevantEntity.guid,
                    5f,
                    100f,
                    "BlackBox.png"
            );
            this.enemyEntities.put(newEnemyEntity.guid, newEnemyEntity);
            enemyTimer = 3f;
        }

        enemyTimer -= Gdx.graphics.getDeltaTime();
        PriorityQueue<Long> queue = new PriorityQueue<>();

        for (Entity entity: this.enemyEntities.values()) {
            Enemy enemy = (Enemy) Utils.getComponent(entity, Enemy.class);


            if (enemy == null) {
                continue;
            }

            if (isDead(enemy)) {
                queue.add(entity.guid);
            }

            if (isHit(enemy)) {
                enemy.health.current--;
                System.out.println(enemy.health.current);
            }

            rotateTowards(enemy);
            moveTowards(enemy);
            render(batch, enemy);
        }

        while (queue.size() > 0) {
            Long enemyId = queue.poll();
            enemyEntities.remove(enemyId);
        }
    }

    private void render(SpriteBatch batch, Enemy enemy) {
        enemy.draw(batch);
    }

    private Entity createEnemy(Long allyId, float health, float scoreValue, String sprite) {
        Entity entity = new Entity();
        Level level = (Level) Utils.getComponent(levelEntity, Level.class);
        float xPos = MathUtils.random.nextInt((int) level.width);
        float yPos = MathUtils.random.nextInt((int) level.height);
        Enemy enemy = new Enemy(sprite, xPos, yPos, allyId, health, scoreValue);
        entity.components.add(enemy);

        return entity;
    }

    private boolean isDead(Enemy enemy) {
        return enemy.health.current <= 0f;
    }

    private boolean isHit(Enemy enemy) {
        PriorityQueue<Long> queue = new PriorityQueue<>();
        boolean isHit = false;

        for (Entity projectileEntity: projectileEntities.values()) {
            Projectile projectile = (Projectile) Utils.getComponent(projectileEntity, Projectile.class);

            if (projectile == null) {
                continue;
            }

            Long playerId = projectile.playerId;
            Entity playerEntity = allyEntities.get(playerId);
            Player player = (Player) Utils.getComponent(playerEntity, Player.class);

            Rectangle projectileBoundingRectangle = projectile.getBoundingRectangle();
            Rectangle enemyBoundingRectangle = enemy.getBoundingRectangle();

            if (enemyBoundingRectangle.overlaps(projectileBoundingRectangle)) {
                queue.add(projectileEntity.guid);

                if (enemy.health.current == 1f) {
                    playerSystem.increaseScore(enemy.value, player);
                }
                isHit = true;
            }
        }

        while (queue.size() > 0) {
            Long projectileId = queue.poll();

            projectileSystem.removeProjectileEntity(projectileId);
        }

        return isHit;
    }

    private void rotateTowards(Enemy enemy) {
        Entity playerEntity = allyEntities.get(enemy.allyId);
        Player player = (Player) Utils.getComponent(playerEntity, Player.class);
        enemy.movement.position.x = enemy.getX();
        enemy.movement.position.y = enemy.getY();
        float angle = (float) Math.atan2(enemy.movement.position.y - player.movement.position.y,
                enemy.movement.position.x - player.movement.position.x);
        float degrees = (float) (angle * (180 / Math.PI));

        enemy.setRotation(degrees - 90);
    }

    private void moveTowards(Enemy enemy) {
        Entity playerEntity = allyEntities.get(enemy.allyId);
        Player player = (Player) Utils.getComponent(playerEntity, Player.class);
        enemy.movement.position.x = enemy.getX();
        enemy.movement.position.y = enemy.getY();
        float directionX = player.movement.position.x - enemy.movement.position.x;
        float directionY = player.movement.position.y - enemy.movement.position.y;
        float speedX = enemy.movement.velocity.x;
        float speedY = enemy.movement.velocity.y;
        double sqrt = Math.sqrt(directionX * directionX + directionY * directionY);
        float velocityX = (float) (directionX * (speedX * Gdx.graphics.getDeltaTime()) / sqrt);
        float velocityY = (float) (directionY * (speedY * Gdx.graphics.getDeltaTime()) / sqrt);

        enemy.translateX(velocityX);
        enemy.translateY(velocityY);
    }
}
