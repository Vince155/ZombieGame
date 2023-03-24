package com.zombie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen extends ScreenAdapter {
    private final ZombieGame game;
    private final OrthographicCamera camera;

    public MainMenuScreen(ZombieGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Zombie Game", 100, 150);
        game.font.draw(game.batch, "Press the Left Mouse Button to Play", 100, 100);
        game.batch.end();

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }
}
