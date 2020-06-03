package com.github.fernthedev.gprefix.spigot.locale;

import lombok.Getter;
import org.bukkit.Material;

import java.io.Serializable;

@Getter
public class GuiLocale implements Serializable {

    private Material prefixMaterial = Material.NAME_TAG;
    private Material approveMaterial = Material.EMERALD_BLOCK;
    private Material denyMaterial = Material.REDSTONE_BLOCK;
    private Material exitInventoryMaterial = Material.BARRIER;
    private Material silenceMaterial = Material.NOTE_BLOCK;
    private Material reloadButtonMaterial = Material.STICK;
    private Material currentPageButtonMaterial = Material.PAPER;
    private Material scrollPageButton = Material.ARROW;

    private String prefixListGUITitle = "&9Prefix Approval Queue";
    private String prefixListGUIApproveTitle = "&9Prefix Approval for ${player}";
    private String prefixItemName = "&3${player}";
    private String prefixItemLore = "${prefix}";

    private String reloadListButton = "&aReload";
    private String silentButton = "&cSilent: ${silent}";
    private String approveButton = "&aApprove";
    private String denyButton = "&cDeny";
    private String closeInventoryButton = "&cExit";
    private String nextPageButton = "&3Next page";
    private String previousPageButton = "&ePrevious page";
    private String currentPageButton = "&6Current page: &a${page}";

    private String prefixManagedByOther = "&cPrefix has been approved/denied by a different staff member or removed abruptly from database.";

}
