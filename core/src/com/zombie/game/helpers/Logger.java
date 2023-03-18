package com.zombie.game.helpers;

import com.badlogic.gdx.Gdx;

public class Logger {
    public static void debug(Object object, String message) {
        Gdx.app.debug("Zombie Game", message);
    }

    public static void error(String message) {
        Gdx.app.error("Zombie Game", message);
    }
}
