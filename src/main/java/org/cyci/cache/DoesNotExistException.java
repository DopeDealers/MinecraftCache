package org.cyci.cache;

/**
 * @author - Phil
 * @project - Complex
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Tue - 19/Jan/2021 - 6:55 PM
 */
public class DoesNotExistException extends Exception{
    public DoesNotExistException(String Error) {
        super(Error);
    }
}
