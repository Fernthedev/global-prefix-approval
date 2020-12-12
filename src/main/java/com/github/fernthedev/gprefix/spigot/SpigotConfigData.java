package com.github.fernthedev.gprefix.spigot;

import com.github.fernthedev.gprefix.core.CommonConfigData;
import com.github.fernthedev.gprefix.spigot.locale.GuiLocale;
import com.github.fernthedev.gprefix.spigot.gui.GuiSize;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SpigotConfigData extends CommonConfigData implements Serializable {

    private GuiLocale guiLocale = new GuiLocale();

    private GuiSize prefixQueueGuiSize = new GuiSize(5, 9);

    private String nameTagEditPrefixCommand = "nte player {player} prefix {prefix}";

}
