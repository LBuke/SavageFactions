package com.massivecraft.factions.scoreboards.sidebar;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.SavageFactions;
import com.massivecraft.factions.scoreboards.FSidebarProvider;
import com.massivecraft.factions.struct.Role;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class FDefaultSidebar extends FSidebarProvider {

    @Override
    public String getTitle(FPlayer fplayer) {
        return replaceTags(fplayer, SavageFactions.plugin.getConfig().getString("scoreboard.default-title", "{name}"));
    }

    @Override
    public List<String> getLines(FPlayer fplayer) {
        if (fplayer.hasFaction()) {
            return getOutput(fplayer, "scoreboard.default");
        } else if (SavageFactions.plugin.getConfig().getBoolean("scoreboard.factionless-enabled", false)) {
            return getOutput(fplayer, "scoreboard.factionless");
        }
        return getOutput(fplayer, "scoreboard.default"); // no faction, factionless-board disabled
    }

    public List<String> getOutput(FPlayer fplayer, String list) {
        List<String> lines = SavageFactions.plugin.getConfig().getStringList(list);

        if (lines == null || lines.isEmpty()) {
            return new ArrayList<>();
        }

        for (String str : lines) {
//            System.out.println(str);
            if (!str.equalsIgnoreCase("<facmemberlist>"))
                continue;

            int index = 1;
            for (Role role : Role.values()) {
                for (FPlayer fp : fplayer.getFaction().getFPlayersWhereRole(role)) {
                    if (!fp.isOnline())
                        continue;

                    if (index >= 10)
                        break;

                    lines.add(ChatColor.GREEN + fp.getNameAndTitle() + " " + getHealth(fp.getPlayer()));
                    index += 1;
                }
            }

            break;
        }

        ListIterator<String> it = lines.listIterator();
        while (it.hasNext()) {
            String original = it.next();
            String str = replaceTags(fplayer, original);

            if (original.equalsIgnoreCase("<facmemberlist>")) {
                it.remove();
                continue;
            }

            it.set(str);
        }
        return lines;
    }

    private String getHealth(Player player) {
        String str = "";
        if (player.getHealth() > 13)
            str = ChatColor.GREEN.toString() + (int) player.getHealth();

        else if (player.getHealth() > 7)
            str = ChatColor.GOLD.toString() + (int) player.getHealth();

        else
            str = ChatColor.RED.toString() + (int) player.getHealth();

        return ChatColor.WHITE + "(" + str + ChatColor.WHITE + ")";
    }
}