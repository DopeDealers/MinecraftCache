package org.cyci.cache;

/**
 * @author - Phil
 * @project - Complex
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Tue - 19/Jan/2021 - 7:15 PM
 */
public class TableEmptyException extends Exception{
    public TableEmptyException(String Error) {
        super(Error);
    }
}
