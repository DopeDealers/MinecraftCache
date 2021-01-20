package org.cyci.cache;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author - Phil
 * @project - Complex
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Tue - 19/Jan/2021 - 6:32 PM
 */
public class Cache extends JavaPlugin {
    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    /**
     * @Cache
     */
    private static final Map<UUID, Player> cache;
    /**
     * @Cache
     * @return the cache
     */
    public static Map<UUID, Player> getCache() {return cache;}
    /**
     * @param userID
     * @param userInfo
     * @return String
     * @throws KeyExistsException
     * {@code Cache.insertUser(UUID, Player)}
     */
    public static String insertUser(UUID userID, Player userInfo) throws KeyExistsException {
        if (Cache.cache.containsKey(userID)) throw new KeyExistsException("The Key already exists in the cache.");
        Cache.cache.put(userID, userInfo);
        return "User inserted";
    }
    /**
     * @param userID
     * @return String
     * @throws DoesNotExistException
     * {@code Cache.removeUser(UUID)}
     */
    public static String removeUser(UUID userID) throws DoesNotExistException {
        if (!Cache.cache.containsKey(userID)) throw new DoesNotExistException("The key does not exist in the current cache!");
        Cache.cache.remove(userID);
        return "User removed";
    }
    /**
     * @param userID
     * @return boolean
     * {@code Cache.userExists(UUID)}
     */
    public static boolean userExists(UUID userID) {
        return Cache.cache.containsKey(userID);
    }

    /**
     * @param userID
     * @return User
     * @throws DoesNotExistException
     * {@code Cache.getUser(UUID)}
     */
    public static Cache getUser(UUID userID) throws DoesNotExistException {
        if (!Cache.cache.containsKey(userID)) throw new DoesNotExistException("The key does not exist in the current cache!");
        return (Cache) Cache.cache.get(userID);
    }
    /**
     * @Version
     * @return
     */
    public static String getVersion() {
        return "1.0.0";
    }
    static {
        cache = new HashMap<UUID, Player>();
    }
}
