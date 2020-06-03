package com.github.fernthedev.gprefix.spigot.event;

import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class PrefixUpdateEvent extends PrefixListUpdateEvent {

    private final UUID uuid;
    private final PrefixInfoData prefixInfoData;

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
