package org.cyci.cache;

/**
 * @author - Phil
 * @project - Complex
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Tue - 19/Jan/2021 - 6:50 PM
 */
public class KeyExistsException extends Exception{
    public KeyExistsException(String Error) {
        super(Error);
    }
}
