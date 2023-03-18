package com.zombie.game.helpers;

import com.zombie.game.components.IComponent;
import com.zombie.game.entity.Entity;

public class Utils {
    /**
     * Helper function to get components.
     *
     * @param entity
     * @param type
     * @return
     */
    public static IComponent getComponent(Entity entity, Class<?> type) {
        for (IComponent component : entity.components) {
            if (type.isInstance(component)) {
                return component;
            }
        }

        return null;
    }
}
