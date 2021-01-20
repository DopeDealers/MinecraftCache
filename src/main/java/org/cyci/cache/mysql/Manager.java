package org.cyci.cache.mysql;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Manager {
    private static AsyncDatabase database;
    private static final Plugin plugin = Bukkit.getPluginManager().getPlugin("MinecraftCache");

    public static boolean initialize() {
        System.out.println("Connecting to db.");
        AtomicBoolean valid = new AtomicBoolean(true);
        String host = Objects.requireNonNull(plugin).getConfig().getString("db.login_info.host"),
                username = Objects.requireNonNull(plugin).getConfig().getString("db.login_info.username"),
                password = Objects.requireNonNull(plugin).getConfig().getString("db.login_info.password"),
                db = database.getTable();

        database = new MySQL(plugin).connect(host, 3306, db, username, password, (error, result) -> {
        if (error != null) {
            error.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
            valid.set(false);
            return;
        }
        System.out.println("Connected to MYSQL DB");
        });

        if (database instanceof SQL)((SQL) database).startProcess();
        database.createTable((e, r) -> {
        if (e != null) e.printStackTrace();
        });
        return valid.get();
    }

    public static AsyncDatabase getDatabase() {
        return database;
    }

    public static void close() {
        if (database instanceof SQL) {
            SQL sql = (SQL) database;
            sql.queue.add(connection -> {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            });
        }
    }
}