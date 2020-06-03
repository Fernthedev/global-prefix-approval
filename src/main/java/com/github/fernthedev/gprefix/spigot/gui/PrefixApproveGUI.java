package com.github.fernthedev.gprefix.spigot.gui;

import com.github.fernthedev.fernapi.server.spigot.util.GuiUtil;
import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.api.IFPlayer;
import com.github.fernthedev.fernapi.universal.data.chat.ChatColor;
import com.github.fernthedev.gprefix.core.CommonNetwork;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.spigot.SpigotPlugin;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class PrefixApproveGUI implements InventoryProvider, Listener {

    private final UUID uuid;
    private final String name;
    private String prefix;
    private boolean silent = false;

    private volatile boolean managed = false;

    private static final int rows = 5;
    private static final int columns = 9;
    @Setter
    private SmartInventory inventory;

    public PrefixApproveGUI(UUID uuid, String name, String prefix) {
        this.uuid = uuid;
        this.name = name;
        this.prefix = prefix;
    }

    public static SmartInventory INVENTORY(UUID uuid, String name, String prefix, SmartInventory inventory) {
        PrefixApproveGUI prefixApproveGUI = new PrefixApproveGUI(uuid, name, prefix);
        SmartInventory smartInventory = SmartInventory.builder()
                .id("prefixApprove")
                .parent(inventory)
                .provider(prefixApproveGUI)
                .manager(SpigotPlugin.getInstance().getInventoryManager())
                .size(rows, columns)
                .title(SpigotPlugin.getConfigData().getGuiLocale().getPrefixListGUIApproveTitle().replace("${player}", name).replace("${prefix}", prefix))
                .build();
        prefixApproveGUI.setInventory(inventory);

        return smartInventory;
    }

    private void setButtons(InventoryContents contents) {
        String silentText = SpigotPlugin.getConfigData().getGuiLocale().getSilentButton();

        silentText = silentText.replace("${silent}", Universal.getLocale().boolColored(silent));

        ItemStack silentStack = GuiUtil.build(SpigotPlugin.guiLocale().getSilenceMaterial(), 1, silentText);
        contents.set(rows - 2, columns / 2, ClickableItem.of(silentStack, inventoryClickEvent -> silent = !silent));




        String prefixTag = SpigotPlugin.getConfigData().getGuiLocale().getPrefixItemName();
        prefixTag = prefixTag.replace("${prefix}", prefix).replace("${player}", name);

        String prefixLoreTag = SpigotPlugin.getConfigData().getGuiLocale().getPrefixItemLore();
        prefixLoreTag = ChatColor.RESET + prefixLoreTag.replace("${prefix}", prefix).replace("${player}", name);


        List<String> loreList = new ArrayList<>();
        loreList.add(prefixLoreTag);
        ItemStack prefixStack = GuiUtil.build(SpigotPlugin.guiLocale().getPrefixMaterial(), 1, prefixTag, loreList);

        contents.set(1, columns / 2, ClickableItem.empty(prefixStack));

    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));


        ItemStack approveStack = GuiUtil.build(SpigotPlugin.getConfigData().getGuiLocale().getApproveMaterial(), ChatColor.translateAlternateColorCodes('&', SpigotPlugin.getConfigData().getGuiLocale().getApproveButton()));
        ItemStack denyStack =  GuiUtil.build(SpigotPlugin.getConfigData().getGuiLocale().getDenyMaterial(), ChatColor.translateAlternateColorCodes('&', SpigotPlugin.getConfigData().getGuiLocale().getDenyButton()));

        IFPlayer<?> prefixPlayer = Universal.getMethods().getPlayerFromUUID(uuid);

        contents.set(3, 1, ClickableItem.of(approveStack,
                e -> doPrefix(player, prefixPlayer, CommonNetwork.PrefixUpdateMode.APPROVED, prefix, silent)));

        contents.set(3, columns - 2, ClickableItem.of(denyStack,
                e -> doPrefix(player, prefixPlayer, CommonNetwork.PrefixUpdateMode.DENIED, prefix, silent)));

        setButtons(contents);


        ItemStack closeItem = GuiUtil.build(SpigotPlugin.getConfigData().getGuiLocale().getExitInventoryMaterial(), ChatColor.translateAlternateColorCodes('&', SpigotPlugin.getConfigData().getGuiLocale().getCloseInventoryButton()));

        contents.set(rows - 1, columns - 1, ClickableItem.of(closeItem,
                e -> inventory.open(player)));
    }

    private void doPrefix(Player player, IFPlayer<?> prefixPlayer, CommonNetwork.PrefixUpdateMode updateMode, String prefix, boolean silent) {
        managed = true;
        Core.getPrefixPlugin().getPrefixManager().updatePrefixStatus(Universal.getMethods().convertPlayerObjectToFPlayer(player), prefixPlayer, new PrefixInfoData(prefix, updateMode), silent);
        inventory.open(player);
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        if (!Core.getPrefixPlugin().getPrefixManager().getPrefixes().containsKey(uuid)) {
            if (!managed) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', SpigotPlugin.getConfigData().getGuiLocale().getPrefixManagedByOther()));
                player.closeInventory();
            }
            return;
        }

        prefix = Core.getPrefixPlugin().getPrefixManager().getPrefixes().get(uuid).getPrefix();

        setButtons(contents);


    }
}
