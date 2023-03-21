package com.zombie.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.zombie.game.components.Camera;
import com.zombie.game.components.HeadsUpDisplay;
import com.zombie.game.components.Level;
import com.zombie.game.components.Player;
import com.zombie.game.entity.Entity;
import com.zombie.game.systems.CameraSystem;
import com.zombie.game.systems.EnemySystem;
import com.zombie.game.systems.HeadsUpDisplaySystem;
import com.zombie.game.systems.LevelSystem;
import com.zombie.game.systems.PlayerSystem;
import com.zombie.game.systems.ProjectileSystem;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ZombieGame extends Game {
	SpriteBatch batch;
	PlayerSystem playerSystem;
	LevelSystem levelSystem;
	CameraSystem cameraSystem;
	EnemySystem enemySystem;
	ProjectileSystem projectileSystem;
	HeadsUpDisplaySystem headsUpDisplaySystem;
	HashMap<Long, Entity> enemies;
	HashMap<Long, Entity> allies;
	HashMap<Long, Entity> projectiles;
	Entity playerEntity;
	Entity cameraEntity;
	Entity levelEntity;
	Entity hudEntity;
	
	@Override
	public void create () {
		levelEntity = new Entity();
		playerEntity = new Entity();
		cameraEntity = new Entity();
		hudEntity = new Entity();
		allies = new HashMap<>();
		enemies = new HashMap<>();
		projectiles = new HashMap<>();
		batch = new SpriteBatch();

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
		playerSystem = new PlayerSystem(allies, cameraEntity, projectileSystem);
		cameraSystem = new CameraSystem(cameraEntity, playerEntity);
		enemySystem = new EnemySystem(enemies, levelEntity, allies, projectiles, projectileSystem, playerSystem);
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		batch.begin();
		levelSystem.update(batch);
		playerSystem.update(batch);
		cameraSystem.update(batch);
		headsUpDisplaySystem.update(batch);
		projectileSystem.update(batch);
		enemySystem.update(batch);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		enemies.clear();
		projectiles.clear();
	}
}
