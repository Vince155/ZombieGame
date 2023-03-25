package com.zombie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.ScreenUtils;
import com.zombie.game.components.Camera;
import com.zombie.game.components.HeadsUpDisplay;
import com.zombie.game.components.Level;
import com.zombie.game.components.Player;
import com.zombie.game.entity.Entity;
import com.zombie.game.helpers.Utils;
import com.zombie.game.systems.CameraSystem;
import com.zombie.game.systems.EnemySystem;
import com.zombie.game.systems.HeadsUpDisplaySystem;
import com.zombie.game.systems.LevelSystem;
import com.zombie.game.systems.PlayerSystem;
import com.zombie.game.systems.ProjectileSystem;

import java.util.HashMap;

public class GameScreen extends ScreenAdapter {
    private final ZombieGame game;

    private final HashMap<Long, Entity> allies;
    private final HashMap<Long, Entity> enemies;
    private final HashMap<Long, Entity> projectiles;
    private final PlayerSystem playerSystem;
    private final LevelSystem levelSystem;
    private final CameraSystem cameraSystem;
    private final EnemySystem enemySystem;
    private final ProjectileSystem projectileSystem;
    private final HeadsUpDisplaySystem headsUpDisplaySystem;

    public GameScreen(ZombieGame game) {
        this.game = game;

        Entity levelEntity = new Entity();
        Entity playerEntity = new Entity();
        Entity cameraEntity = new Entity();
        Entity hudEntity = new Entity();

        allies = new HashMap<>();
        enemies = new HashMap<>();
        projectiles = new HashMap<>();

        Level level = new Level("vecteezy_cute-abstract-modern-background_.jpg");
        level.height = Gdx.graphics.getHeight();
        level.width = Gdx.graphics.getWidth();
        level.time = 0f;
        level.setSize(level.width, level.height);
        levelEntity.components.add(level);

        Player player = new Player("BlueBox.png", 0f, 0f);
        playerEntity.components.add(player);
        allies.put(playerEntity.guid, playerEntity);

        Camera camera = new Camera(playerEntity.guid);
        cameraEntity.components.add(camera);

        HeadsUpDisplay headsUpDisplay = new HeadsUpDisplay(playerEntity.guid, cameraEntity.guid);
        hudEntity.components.add(headsUpDisplay);

        projectileSystem = new ProjectileSystem(projectiles, allies);
        headsUpDisplaySystem = new HeadsUpDisplaySystem(allies, cameraEntity, hudEntity);
        levelSystem = new LevelSystem(levelEntity);
        playerSystem = new PlayerSystem(allies, projectiles, enemies, cameraEntity, projectileSystem);
        cameraSystem = new CameraSystem(cameraEntity, playerEntity);
        enemySystem = new EnemySystem(enemies, levelEntity, allies, projectiles, projectileSystem, playerSystem);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        game.batch.begin();
        levelSystem.update(game.batch);
        playerSystem.update(game.batch);
        cameraSystem.update(game.batch);
        headsUpDisplaySystem.update(game.batch);
        projectileSystem.update(game.batch);
        enemySystem.update(game.batch);
        game.batch.end();

        for (Entity playerEntity: allies.values()) {
            Player player = (Player) Utils.getComponent(playerEntity, Player.class);

            if (player == null) {
                continue;
            }

            if (player.health.current <= 0f) {
                game.setScreen(new GameOverScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void dispose() {
        levelSystem.dispose();
        playerSystem.dispose();
        projectileSystem.dispose();
        enemySystem.dispose();
        enemies.clear();
        projectiles.clear();
        allies.clear();
    }
}
