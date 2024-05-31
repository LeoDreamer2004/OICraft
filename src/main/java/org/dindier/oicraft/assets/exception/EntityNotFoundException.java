package org.dindier.oicraft.assets.exception;

/**
 * The requested entity was not found
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class<?> clazz) {
        super("符合要求的实体类型 " + clazz.getSimpleName() + " 没有找到");
    }

    public EntityNotFoundException(Class<?> clazz, String e) {
        super("符合要求的实体类型 " + clazz.getSimpleName() + " 没有找到，因为\n" + e);
    }
}
