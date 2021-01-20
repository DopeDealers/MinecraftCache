package org.cyci.cache.mysql;

import org.bukkit.plugin.Plugin;
import org.cyci.cache.Cache;

import java.sql.DriverManager;
import java.sql.SQLException;

class MySQL extends SQL {

    public MySQL(Plugin plugin) {
        super((Cache) plugin);
    }

    @Override
    public AsyncDatabase connect(String host, int port, String database, String user, String password, Callback<String> callback) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            callback.handle(e, null);
            return this;
        }

        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database
                    + "?autoReconnect=true", user, password);
            callback.handle(null, null);
        } catch (SQLException e) {
            callback.handle(e, null);
        }
        return this;
    }

}
