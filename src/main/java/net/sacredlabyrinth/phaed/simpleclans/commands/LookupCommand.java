package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

/**
 * @author phaed
 */
public class LookupCommand {
    public LookupCommand() {
    }

    /**
     * Execute the command
     *
     * @param sender
     * @param arg
     */
    public void execute(CommandSender sender, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();
        NumberFormat formatter = new DecimalFormat("#.#");

        String playerName = null;

        if (arg.length == 0) {
            if (!plugin.getPermissionsManager().has(sender, "simpleclans.member.lookup")) {
                ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                return;
            }
            if (!(sender instanceof Player)) {
                ChatBlock.sendMessage(sender, ChatColor.RED + "You must specify a player name if you wish to use this command from the console.");
                return;
            }
            playerName = sender.getName();
        } else if (arg.length == 1) {
            if (!plugin.getPermissionsManager().has(sender, "simpleclans.anyone.lookup")) {
                ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                return;
            }
            playerName = arg[0];
        } else {
            ChatBlock.sendMessage(sender, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.lookup.tag"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        ClanPlayer targetCp = plugin.getClanManager().getAnyClanPlayer(playerName);
        ClanPlayer myCp = null;
        if (sender instanceof Player) {
            myCp = plugin.getClanManager().getClanPlayer(((Player)sender).getUniqueId());
        }
        Clan myClan = myCp == null ? null : myCp.getClan();

        if (targetCp != null) {
            Clan targetClan = targetCp.getClan();

            ChatBlock.sendBlank(sender);
            ChatBlock.saySingle(sender, MessageFormat.format(plugin.getLang("s.sender.info"), plugin.getSettingsManager().getPageClanNameColor() + targetCp.getName() + subColor) + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
            ChatBlock.sendBlank(sender);

            String clanName = ChatColor.WHITE + plugin.getLang("none");

            if (targetClan != null) {
                clanName = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + targetClan.getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getPageClanNameColor() + targetClan.getName();
            }

            String status = targetClan == null ? ChatColor.WHITE + plugin.getLang("free.agent") : (targetCp.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() + plugin.getLang("leader") : (targetCp.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() + plugin.getLang("trusted") : plugin.getSettingsManager().getPageUnTrustedColor() + plugin.getLang("untrusted")));
            String rank = ChatColor.WHITE + "" + Helper.parseColors(targetCp.getRank());
            String joinDate = ChatColor.WHITE + "" + targetCp.getJoinDateString();
            String lastSeen = ChatColor.WHITE + "" + targetCp.getLastSeenString();
            String inactive = ChatColor.WHITE + "" + targetCp.getInactiveDays() + subColor + "/" + ChatColor.WHITE + plugin.getSettingsManager().getPurgePlayers() + " days";
            String rival = ChatColor.WHITE + "" + targetCp.getRivalKills();
            String neutral = ChatColor.WHITE + "" + targetCp.getNeutralKills();
            String civilian = ChatColor.WHITE + "" + targetCp.getCivilianKills();
            String deaths = ChatColor.WHITE + "" + targetCp.getDeaths();
            String kdr = ChatColor.YELLOW + "" + formatter.format(targetCp.getKDR());
            String pastClans = ChatColor.WHITE + "" + targetCp.getPastClansString(headColor + ", ");

            ChatBlock.sendMessage(sender, "  " + subColor + MessageFormat.format(plugin.getLang("clan.0"), clanName));
            ChatBlock.sendMessage(sender, "  " + subColor + MessageFormat.format(plugin.getLang("rank.0"), rank));
            ChatBlock.sendMessage(sender, "  " + subColor + MessageFormat.format(plugin.getLang("status.0"), status));
            ChatBlock.sendMessage(sender, "  " + subColor + MessageFormat.format(plugin.getLang("kdr.0"), kdr));
            ChatBlock.sendMessage(sender, "  " + subColor + plugin.getLang("kill.totals") + " " + headColor + "[" + plugin.getLang("rival") + ":" + rival + " " + headColor + "" + plugin.getLang("neutral") + ":" + neutral + " " + headColor + "" + plugin.getLang("civilian") + ":" + civilian + headColor + "]");
            ChatBlock.sendMessage(sender, "  " + subColor + MessageFormat.format(plugin.getLang("deaths.0"), deaths));
            ChatBlock.sendMessage(sender, "  " + subColor + MessageFormat.format(plugin.getLang("join.date.0"), joinDate));
            ChatBlock.sendMessage(sender, "  " + subColor + MessageFormat.format(plugin.getLang("last.seen.0"), lastSeen));
            ChatBlock.sendMessage(sender, "  " + subColor + MessageFormat.format(plugin.getLang("past.clans.0"), pastClans));
            ChatBlock.sendMessage(sender, "  " + subColor + MessageFormat.format(plugin.getLang("inactive.0"), inactive));

            if (arg.length == 1 && !targetCp.equals(myCp)) {
                String killType = ChatColor.GRAY + plugin.getLang("neutral");

                if (targetClan == null) {
                    killType = ChatColor.DARK_GRAY + plugin.getLang("civilian");
                } else if (myClan != null && myClan.isRival(targetClan.getTag())) {
                    killType = ChatColor.WHITE + plugin.getLang("rival");
                }

                ChatBlock.sendMessage(sender, "  " + subColor + MessageFormat.format(plugin.getLang("kill.type.0"), killType));
            }

            ChatBlock.sendBlank(sender);
        } else {
            ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("no.sender.data.found"));

            if (arg.length == 1 && myClan != null) {
                ChatBlock.sendBlank(sender);
                ChatBlock.sendMessage(sender, MessageFormat.format(plugin.getLang("kill.type.civilian"), ChatColor.DARK_GRAY));
            }
        }
    }
}
