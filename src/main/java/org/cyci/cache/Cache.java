package org.cyci.cache;

import org.bukkit.entity.Player;

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
public class Cache {

    private static final Map<UUID, Player> cache;
    public Cache() {
    }

    public static String insertUser(UUID userID, Map<UUID, Player> userInfo) throws KeyExistsException {
        if (Cache.cache.containsKey(userID)) throw new KeyExistsException("The Key already exists in the cache.");
        Cache.cache.put(userID, (Player) userInfo);
        return "User inserted";
    }
    public static String removeUser(UUID userID) throws DoesNotExistException {
        if (!Cache.cache.containsKey(userID)) throw new DoesNotExistException("The key does not exist in the current cache!");
        Cache.cache.remove(userID);
        return "User removed";
    }
    public static boolean userExists(UUID userID) {
        return Cache.cache.containsKey(userID);
    }
    public static Cache getUser(UUID userID) throws DoesNotExistException {
        if (!Cache.cache.containsKey(userID)) throw new DoesNotExistException("The key does not exist in the current cache!");
        return (Cache) Cache.cache.get(userID);
    }
    public static String getVersion() {
        return "1.0.0";
    }
    static {
        cache = new HashMap<UUID, Player>();
    }
}
