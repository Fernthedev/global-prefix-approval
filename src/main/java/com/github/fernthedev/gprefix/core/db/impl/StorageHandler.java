package com.github.fernthedev.gprefix.core.db.impl;

import java.util.concurrent.CompletableFuture;

public interface StorageHandler {

    void init();

    CompletableFuture<?> save();

    CompletableFuture<?> load();
}
