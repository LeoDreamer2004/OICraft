package org.dindier.oicraft.assets.exception;

/**
 * The requested entity was not found
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class<?> clazz) {
        super("Cannot find entity: " + clazz.getName());
    }

    public EntityNotFoundException(Class<?> clazz, String e) {
        super("Cannot find entity " + clazz.getName() + " because " + e);
    }
}
