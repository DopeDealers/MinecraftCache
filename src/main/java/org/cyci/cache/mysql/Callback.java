package org.cyci.cache.mysql;


import com.sun.istack.internal.Nullable;

public interface Callback<T> {

    void handle(@Nullable Exception error, @Nullable T result);

}
