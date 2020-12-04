package com.github.fernthedev.gprefix.velocity.event;

import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class PrefixUpdateEvent extends PrefixListUpdateEvent {

    private final UUID uuid;
    private final PrefixInfoData prefixInfoData;
}
