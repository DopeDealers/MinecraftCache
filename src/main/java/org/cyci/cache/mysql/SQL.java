package org.cyci.cache.mysql;

import org.bukkit.Bukkit;
import org.cyci.cache.Cache;

import java.sql.*;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * @author - Phil
 * @project - MinecraftCache
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Wed - 20/Jan/2021 - 12:36 AM
 */
public abstract class SQL implements AsyncDatabase {

    private final Cache plugin;
    private final ReentrantLock lock;
    Connection conn;
    BlockingDeque<Consumer<Connection>> queue;

    SQL(Cache plugin) {
        this.plugin = plugin;
        this.queue = new LinkedBlockingDeque<>();
        this.lock = new ReentrantLock();
    }

    @Override
    public void createTable(Callback<Boolean> callback) {
        PluginTask.runAsync(() -> queue.add(connection -> {
            if (isNotConnected(callback)) return;
            String sql;
            sql = Objects.requireNonNull(plugin.getConfig().getString("createTable")).replace("{mCt}", table);
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(sql);
                boolean execute = statement.execute();
                PluginTask.run(() -> callback.handle(null, execute));
            } catch (SQLException e) {
                PluginTask.run(() -> callback.handle(e, false));
            } finally {
                try {
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    @Override
    public void select(String key, String whereKey, Object whereValue, Callback<Map<String, Object>> callback) {
        PluginTask.runAsync(() -> queue.add(connection -> {
            if (isNotConnected(callback)) {
                PluginTask.run(() -> callback.handle(new Exception("Database not connected!"), null));
                return;
            }
            String sql = "SELECT " + key + " FROM " + table;
            if (whereKey != null || whereValue != null) sql += " WHERE " + whereKey + " = ?;";
            else sql += ";";
            PreparedStatement statement = null;
            ResultSet execute = null;
            try {
                statement = connection.prepareStatement(sql);
                if (sql.contains("?")) this.set(whereValue, 1, statement);
                execute = statement.executeQuery();
                ResultSetMetaData metaData = execute.getMetaData();

                if (execute.isClosed()) {
                    PluginTask.run(() -> callback.handle(new Exception("Player not on database. Adding them..."), null));
                    return;
                }
                Map<String, Object> result = new Hashtable<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String colName = metaData.getColumnName(i);
                    result.put(colName, execute.getObject(colName));
                }
                PluginTask.run(() -> callback.handle(null, result));
            } catch (SQLException e) {
                PluginTask.run(() -> callback.handle(e, null));
            } finally {
                try {
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    if (execute != null) execute.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    @Override
    public void insert(String[] keys, Object[] values, Callback<Object> callback) {
        PluginTask.runAsync(() -> queue.add(connection -> {
            if (isNotConnected(callback)) {
                PluginTask.run(() -> callback.handle(new Exception("Database not connected!"), null));
                return;
            }

            if (keys.length != values.length) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    callback.handle(new Exception("Mismatch on keys and values length."), null);
                });
                return;
            }

            String sql = "INSERT INTO " + table + " (" + String.join(",", keys) + ") VALUES (";
            for (int i = 0; i < keys.length; i++) {
                sql += "?";
                if (i < keys.length - 1) sql += ",";
            }
            sql += ");";

            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(sql);

                for (int i = 0; i < keys.length; i++) {
                    this.set(values[i], i + 1, statement);
                }

                boolean execute = statement.execute();
                PluginTask.run(() -> callback.handle(null, execute));
            } catch (SQLException e) {
                PluginTask.run(() -> callback.handle(e, null));
            } finally {
                try {
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    @Override
    public void update(String[] keys, Object[] values, String whereKey, Object whereValue, Callback<Object> callback) {
        PluginTask.runAsync(() -> queue.add(connection -> {
            if (isNotConnected(callback)) {
                PluginTask.run(() -> callback.handle(new Exception("Database not connected!"), null));
                return;
            }

            if (keys.length != values.length) {
                PluginTask.run(() -> callback.handle(new Exception("Mismatch on keys and values length."), null));
                return;
            }

            String sql = "UPDATE " + table + " SET ";
            for (int i = 0; i < keys.length; i++) {
                sql += keys[i] + " = ?";
                if (i < keys.length - 1) sql += ",";
            }

            sql += " WHERE " + whereKey + " = ?;";
            PreparedStatement statement = null;

            try {
                statement = connection.prepareStatement(sql);
                for (int i = 0; i < keys.length; i++) this.set(values[i], i + 1, statement);
                this.set(whereValue, values.length + 1, statement);
                long update = statement.executeUpdate();
                PluginTask.run(() -> callback.handle(null, update));
            } catch (SQLException e) {
                PluginTask.run(() -> callback.handle(e, null));
            } finally {
                try {
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    private <T> boolean isNotConnected(Callback<T> callback) {
        try {
            if (conn == null || conn.isClosed()) {
                PluginTask.run(() -> callback.handle(new Exception("Database not connected!"), null));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    private void set(Object o, int index, PreparedStatement statement) throws SQLException {
        if (o instanceof String) statement.setString(index, (String) o);
        else if (o instanceof Boolean) statement.setBoolean(index, (Boolean) o);
        else if (o instanceof Long) statement.setLong(index, (Long) o);
        else if (o instanceof Integer) statement.setInt(index, (Integer) o);
    }

    public void startProcess() {
        PluginTask.runAsync(() -> {
            while (plugin.isEnabled()) {
                try {
                    Consumer<Connection> runnable = queue.take();
                    lock.lock();
                    runnable.accept(conn);
                } catch (Exception e) {
                    // Ignore this
                } finally {
                    lock.unlock();
                }
            }
        });
    }
}
