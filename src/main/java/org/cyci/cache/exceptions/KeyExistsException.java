package org.cyci.cache.exceptions;

import org.cyci.cache.Cache;

/**
 * @author - Phil
 * @project - MinecraftCache
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Wed - 20/Jan/2021 - 12:59 AM
 */
public class KeyExistsException extends Exception {
    public KeyExistsException(String Error) {
        super(Error);
    }
}
