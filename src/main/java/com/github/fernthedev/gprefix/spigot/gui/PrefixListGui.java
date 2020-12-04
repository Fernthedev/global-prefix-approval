package com.github.fernthedev.gprefix.spigot.gui;

import com.github.fernthedev.fernapi.server.spigot.util.GuiUtil;
import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.data.chat.ChatColor;
import com.github.fernthedev.fernapi.universal.data.chat.TextMessage;
import com.github.fernthedev.fernapi.universal.util.UUIDFetcher;
import com.github.fernthedev.gprefix.core.CommonNetwork;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.spigot.SpigotPlugin;
import com.github.fernthedev.gprefix.spigot.event.PrefixListUpdateEvent;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryListener;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class PrefixListGui implements InventoryProvider, Listener {

    private static final Map<UUID, Player> playerMap = new HashMap<>();
    private static final PrefixListGui instance = new PrefixListGui();
    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("prefixQueueList")
            .provider(instance)
            .manager(SpigotPlugin.getInstance().getInventoryManager())
            .type(InventoryType.CHEST)
            .size(
                    SpigotPlugin.getConfigData().getPrefixQueueGuiSize().getRows(),
                    SpigotPlugin.getConfigData().getPrefixQueueGuiSize().getColumns()
            )
            .listener(new InventoryListener<>(InventoryCloseEvent.class, inventoryCloseEvent -> playerMap.remove(inventoryCloseEvent.getPlayer().getUniqueId())))
            .title(TextMessage.fromColor(SpigotPlugin.getConfigData().getGuiLocale().getPrefixListGUITitle()).toPlainText())
            .build();
    private final int rows = SpigotPlugin.getConfigData().getPrefixQueueGuiSize().getRows();
    private final int columns = SpigotPlugin.getConfigData().getPrefixQueueGuiSize().getColumns();
    private final int itemsPerPage = ((rows - 3) * (columns - 2));
    private List<ClickableItem> clickableItems = new ArrayList<>();

    @Override
    public void init(Player player, InventoryContents contents) {
        playerMap.put(player.getUniqueId(), player);
        Bukkit.getPluginManager().registerEvents(this, (Plugin) Universal.getPlugin());
        setAllButtons(player, contents);
    }

    private void setAllButtons(Player player, InventoryContents contents) {
        bottomAndBorderButtons(player, contents);

//        contents.set(rows - 1, 1, ClickableItem.of(new ItemStack(Material.CARROT),
//                e -> player.sendMessage(ChatColor.GOLD + "You clicked on a potato.")));




        createNametagButtons(player);
        Pagination pagination = contents.pagination();

        Universal.debug("Items amount " + itemsPerPage);
        pagination.setItems(clickableItems.toArray(new ClickableItem[0]));
        pagination.setItemsPerPage(itemsPerPage);

        SlotIterator iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1);

        for (int i = 2; i < rows - 3; i++) {
            iterator.blacklist(i , 0);
            iterator.blacklist(i , 1);
            iterator.blacklist(i , columns - 1);
            iterator.blacklist(i , columns);
        }

        pagination.addToIterator(iterator);



        setPageButton(contents);
    }

    @EventHandler
    private void onListRefresh(PrefixListUpdateEvent updateEvent) {
        playerMap.forEach((uuid, player) -> createNametagButtons(player));
    }

    private void createNametagButtons(Player player) {
        Map<UUID, PrefixInfoData> prefixes = Core.getPrefixPlugin().getPrefixManager().getPrefixes();

//        Universal.debug("Prefixes list " + prefixes);

        clickableItems.clear();
        prefixes.forEach((uuid, prefixStatus) -> {
            if (prefixStatus.getPrefixUpdateMode() == CommonNetwork.PrefixUpdateMode.AWAIT_APPROVAL) {
                clickableItems.add(getPrefixNametag(player, uuid, prefixStatus.getPrefix()));
            }
        });
    }

    private ClickableItem getPrefixNametag(Player player, UUID uuid, String prefix) {
        Universal.debug("Creating nametag for " + player + " and prefix " + uuid + " " + prefix);
        String name = UUIDFetcher.getName(uuid);


        List<String> loreList = new ArrayList<>();
        loreList.add(ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&',
                SpigotPlugin.getConfigData().getGuiLocale().getPrefixItemLore()
                        .replace("${prefix}", prefix)
                        .replace("${player}", name)
        ));

        ItemStack itemStack = GuiUtil.build(Material.NAME_TAG, (ChatColor.translateAlternateColorCodes('&',
                SpigotPlugin.getConfigData().getGuiLocale().getPrefixItemName()
                        .replace("${prefix}", prefix)
                        .replace("${player}", name)
        )), loreList);



        itemStack.getItemMeta().setLore(loreList);

        return ClickableItem.of(itemStack, inventoryClickEvent -> PrefixApproveGUI.INVENTORY(uuid, name, prefix, INVENTORY).open(player));
    }


    


    private void setPageButton(InventoryContents contents) {
        Pagination pagination = contents.pagination();

        ItemStack currentPage = GuiUtil.build(SpigotPlugin.getConfigData().getGuiLocale().getCurrentPageButtonMaterial(),
                ChatColor.translateAlternateColorCodes('&',
                SpigotPlugin.getConfigData().getGuiLocale().getCurrentPageButton()
                        .replace("${page}", pagination.getPage() + 1 + "")
        ));

        contents.set(rows - 1, (columns / 2), ClickableItem.empty(currentPage));
    }

    private void bottomAndBorderButtons(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        contents.fillBorders(ClickableItem.empty(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));

        ItemStack reloadItem = GuiUtil.build(SpigotPlugin.getConfigData().getGuiLocale().getReloadButtonMaterial(), ChatColor.translateAlternateColorCodes('&', SpigotPlugin.getConfigData().getGuiLocale().getReloadListButton()));

        contents.set(rows - 2, columns / 2, ClickableItem.of(reloadItem,
                e -> createNametagButtons(player)));

        ItemStack closeItem = GuiUtil.build(SpigotPlugin.getConfigData().getGuiLocale().getExitInventoryMaterial(), ChatColor.translateAlternateColorCodes('&', SpigotPlugin.getConfigData().getGuiLocale().getCloseInventoryButton()));

        contents.set(rows - 1, columns - 1, ClickableItem.of(closeItem,
                e -> player.closeInventory()));

        ItemStack nextPage = GuiUtil.build(SpigotPlugin.getConfigData().getGuiLocale().getScrollPageButton(), ChatColor.translateAlternateColorCodes('&', SpigotPlugin.getConfigData().getGuiLocale().getNextPageButton()));

        ItemStack prevPage = nextPage.clone();
        prevPage.getItemMeta().setDisplayName(ChatColor.translateAlternateColorCodes('&', SpigotPlugin.getConfigData().getGuiLocale().getPreviousPageButton()));

        setPageButton(contents);

        if (pagination.isFirst()) {
            contents.set(rows - 1, (columns / 2) - 1, ClickableItem.empty(new ItemStack(Material.AIR)));
        } else {
            contents.set(rows - 1, (columns / 2) - 1, ClickableItem.of(prevPage,
                    e -> INVENTORY.open(player, pagination.previous().getPage())));
        }

        if (pagination.isLast()) {
            contents.set(rows - 1, (columns / 2) + 1, ClickableItem.empty(new ItemStack(Material.AIR)));
        } else {
            contents.set(rows - 1, (columns / 2) + 1, ClickableItem.of(nextPage,
                    e -> INVENTORY.open(player, pagination.next().getPage())));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        pagination.setItems(clickableItems.toArray(new ClickableItem[0]));

//        setAllButtons(player, contents);

        bottomAndBorderButtons(player, contents);
    }




}
