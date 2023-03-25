package com.zombie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameOverScreen extends ScreenAdapter {
    private final ZombieGame game;
    private final OrthographicCamera camera;
    private float timer;

    public GameOverScreen(ZombieGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        timer = 2.5f;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(
                game.batch,
                "Game Over!",
                Gdx.graphics.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f
        );
        game.font.draw(
                game.batch,
                "Press the Left Mouse Button to Play",
                Gdx.graphics.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2.5f
        );
        game.batch.end();
        timer -= Gdx.graphics.getDeltaTime();

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && timer <= 0f) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }
}
