package com.zombie.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.zombie.game.components.Enemy;
import com.zombie.game.components.Level;
import com.zombie.game.components.Player;
import com.zombie.game.components.Projectile;
import com.zombie.game.entity.Entity;
import com.zombie.game.helpers.Logger;
import com.zombie.game.helpers.Utils;

import java.util.HashMap;
import java.util.Objects;
import java.util.PriorityQueue;

public class EnemySystem {
    private float enemyTimer;
    private float bigEnemyTimer;
    private float fastEnemyTimer;
    private float bossTimer;
    private final boolean basicEnemyInPlay;
    private boolean bigEnemyInPlay;
    private boolean fastEnemyInPlay;
    private boolean bossInPlay;
    private boolean bossIsDead;
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
        bigEnemyTimer = 5f;
        fastEnemyTimer = 5f;
        bossTimer = 5f;
        basicEnemyInPlay = true;
    }

    public void update(SpriteBatch batch) {
        handleBasicEnemyCreation();
        handleFastEnemyCreation();
        handleBigEnemyCreation();
        handleBossEnemyCreation();
        handleEnemySpawnLogic();

        if (basicEnemyInPlay) {
            enemyTimer -= Gdx.graphics.getDeltaTime();
        }

        if (fastEnemyInPlay) {
            fastEnemyTimer -= Gdx.graphics.getDeltaTime();
        }

        if (bigEnemyInPlay) {
            bigEnemyTimer -= Gdx.graphics.getDeltaTime();
        }

        if (bossInPlay) {
            bossTimer -= Gdx.graphics.getDeltaTime();
        }

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
            }

            if (Objects.equals(enemy.name, "boss")) {
                enemy.weaponTimer -= Gdx.graphics.getDeltaTime();

                if (enemy.weaponTimer <= 0f) {
                    fireProjectiles(entity, enemy);
                    enemy.weaponTimer = enemy.maxWeaponTimer;
                }
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

    public void dispose() {
        for (Entity enemyEntity: enemyEntities.values()) {
            Enemy enemy = (Enemy) Utils.getComponent(enemyEntity, Enemy.class);

            if (enemy == null) {
                continue;
            }

            Texture enemyTexture = enemy.getTexture();
            enemyTexture.dispose();
        }
    }

    private void render(SpriteBatch batch, Enemy enemy) {
        enemy.draw(batch);
    }

    private Entity getPlayerEntity() {
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

            return null;
        }

        return relevantEntity;
    }

    private Entity createEnemy(
            Long allyId,
            float health,
            float scoreValue,
            String sprite,
            float speed,
            String name
    ) {
        Entity entity = new Entity();
        Entity playerEntity = allyEntities.get(allyId);
        Player player = (Player) Utils.getComponent(playerEntity, Player.class);
        Level level = (Level) Utils.getComponent(levelEntity, Level.class);

        if (player == null) {
            return null;
        }

        if (level == null) {
            return null;
        }

        float xPos = MathUtils.random.nextInt((int) level.width);
        float yPos = MathUtils.random.nextInt((int) level.height);
        float playerXPos = player.movement.position.x;
        float playerYPos = player.movement.position.y;
        float distance = Vector2.dst(xPos, yPos, playerXPos, playerYPos);

        while (distance <= 400f) {
            xPos = MathUtils.random.nextInt((int) level.width);
            yPos = MathUtils.random.nextInt((int) level.height);
            distance = Vector2.dst(xPos, yPos, playerXPos, playerYPos);
        }

        Enemy enemy = new Enemy(sprite, xPos, yPos, allyId, health, scoreValue, speed, name);
        entity.components.add(enemy);

        return entity;
    }

    private Entity createBasicEnemy() {
        Entity playerEntity = getPlayerEntity();

        if (playerEntity == null) {
            Logger.error("Player entity is null");

            return null;
        }

        return createEnemy(
                playerEntity.guid,
                5f,
                100f,
                "BlackBox.png",
                50f,
                "basic"
        );
    }

    private Entity createFastEnemy() {
        Entity playerEntity = getPlayerEntity();

        if (playerEntity == null) {
            Logger.error("Player entity is null");

            return null;
        }

        return createEnemy(
                playerEntity.guid,
                5f,
                400f,
                "YellowBox.png",
                135f,
                "fast"
        );
    }

    private Entity createBigEnemy() {
        Entity playerEntity = getPlayerEntity();

        if (playerEntity == null) {
            Logger.error("Player entity is null");

            return null;
        }

        return createEnemy(
                playerEntity.guid,
                10f,
                300f,
                "BigOrangeBox.png",
                75f,
                "big"
        );
    }

    private Entity createBoss() {
        Entity playerEntity = getPlayerEntity();

        if (playerEntity == null) {
            Logger.error("Player entity is null");

            return null;
        }

        return createEnemy(
                playerEntity.guid,
                50f,
                3000f,
                "PurpleBox.png",
                110f,
                "boss"
        );
    }

    private void handleBasicEnemyCreation() {
        if (enemyTimer <= 0f && basicEnemyInPlay && !bossInPlay) {
            Entity newBasicEnemyEntity = createBasicEnemy();

            if (newBasicEnemyEntity == null) {
                Logger.error("Failed to create basic enemy");

                return;
            }

            this.enemyEntities.put(newBasicEnemyEntity.guid, newBasicEnemyEntity);
            enemyTimer = 3f;
        }
    }

    private void handleFastEnemyCreation() {
        if (fastEnemyTimer <= 0f && fastEnemyInPlay && !bossInPlay) {
            Entity newFastEnemyEntity = createFastEnemy();

            if (newFastEnemyEntity == null) {
                Logger.error("Failed to create fast enemy");

                return;
            }

            this.enemyEntities.put(newFastEnemyEntity.guid, newFastEnemyEntity);
            fastEnemyTimer = 5f;
        }
    }

    private void handleBigEnemyCreation() {
        if (bigEnemyTimer <= 0f && bigEnemyInPlay && !bossInPlay) {
            Entity newBigEnemyEntity = createBigEnemy();

            if (newBigEnemyEntity == null) {
                Logger.error("Failed to create big enemy");

                return;
            }

            this.enemyEntities.put(newBigEnemyEntity.guid, newBigEnemyEntity);
            bigEnemyTimer = 5f;
        }
    }

    private void handleBossEnemyCreation() {
        if (bossTimer <= 0f && bossInPlay) {
            Entity bossEntity = createBoss();

            if (bossEntity == null) {
                Logger.error("Failed to create boss");

                return;
            }

            this.enemyEntities.put(bossEntity.guid, bossEntity);
            bossTimer = 5000f;
        }
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

            Long boxId = projectile.boxId;
            Entity playerEntity = allyEntities.get(boxId);

            if (playerEntity == null) {
                continue;
            }

            Player player = (Player) Utils.getComponent(playerEntity, Player.class);

            if (player == null) {
                continue;
            }

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

    private void handleEnemySpawnLogic() {
        Level level = (Level) Utils.getComponent(levelEntity, Level.class);

        if (level == null) {
            Logger.error("Level time is null");

            return;
        }

        float levelTime = level.time;

        if (bossIsDead) {
            bossInPlay = false;
        }

        if (levelTime >= 30f) {
            bigEnemyInPlay = true;
        }

        if (levelTime >= 45f) {
            fastEnemyInPlay = true;
        }

        if (levelTime >= 180f && !bossIsDead) {
            bossInPlay = true;
        }
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

    private void fireProjectiles(Entity bossEntity, Enemy enemy) {
        float[] rotations = {
                0f,
                20f,
                40f,
                60f,
                80f,
                100f,
                120f,
                140f,
                160f,
                180f,
                200f,
                220f,
                240f,
                260f,
                280f,
                300f,
                320f,
                340f,
                360f
        };

        for (float rotation: rotations) {
            Entity projectileEntity = createProjectile(bossEntity, enemy, rotation);
            projectileEntities.put(projectileEntity.guid, projectileEntity);
        }
    }

    private Entity createProjectile(Entity bossEntity, Enemy enemy, float rotation) {
        Entity entity = new Entity();
        Projectile projectile = new Projectile(
                "Bullet.png",
                250f,
                1f,
                false,
                bossEntity.guid
        );

        float bossPosX = enemy.movement.position.x;
        float bossPosY = enemy.movement.position.y;
        projectile.setPosition(bossPosX + (enemy.getWidth() / 2f), bossPosY);
        projectile.setRotation(rotation);
        entity.components.add(projectile);

        return entity;
    }
}
