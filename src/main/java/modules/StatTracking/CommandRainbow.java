package modules.StatTracking;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import main.Commands.PermissionLevel;
import main.com.github.joecourtneyw.R6J;
import main.com.github.joecourtneyw.R6Player;
import main.com.github.joecourtneyw.declarations.Platform;
import main.com.github.joecourtneyw.declarations.Region;
import main.com.github.joecourtneyw.stats.OperatorStats;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.text.DecimalFormat;

public class CommandRainbow implements CommandExecutor {

    private R6J r6 = StatTracking.instance.rainbowSix;
    private static final DecimalFormat twoPlaces = new DecimalFormat("#.##");

    @Command(label = "rainbow",
            description = "Pulls user statistics in Rainbow Six: Siege",
            alias = "r6",
            permissionLevel = PermissionLevel.MEMBER)
    public void rainbow(CommandEvent event) {
        if (event.getArgs().length == 0) {
            event.reply("*You must enter a username to search*");
            return;
        }

        if (r6.playerExists(event.getArgs()[0], Platform.UPLAY)) {

            R6Player player;
            Region region = Region.NA;
            if (event.getArgs().length == 2) {
                if (event.getArgs()[1].equalsIgnoreCase("EU"))
                    region = Region.EU;
                else if (event.getArgs()[1].equalsIgnoreCase("AS") || event.getArgs()[1].equalsIgnoreCase("ASIA"))
                    region = Region.ASIA;
                else
                    region = Region.NA;
            }

            player = r6.getPlayerByName(event.getArgs()[0], Platform.UPLAY, region);

            EmbedBuilder embed = new EmbedBuilder();

            embed.withAuthorName(player.getName());
            embed.withAuthorIcon(player.getAvatarUrl());

            embed.withColor(Color.GRAY);
            embed.appendDesc("Ranked stats in " + region.name() + " UPlay Servers");

            embed.withThumbnail(player.getRank().getIconUrl());
            embed.appendField("**Rank**", player.getRank().getDisplayName(), true);

            embed.appendField("**MMR**", "**" + twoPlaces.format(player.getMmr()) + "** (" + twoPlaces.format(player.getMaxMmr()) + ")", true);
            embed.appendField("**Statistics**", "**Wins: **" + player.getRankedWins() + " **Losses: **" + player.getRankedLosses() + "\n"
                    + "**Win Rate: **" + twoPlaces.format(player.getRankedWins() / (player.getRankedLosses() + player.getRankedWins() * 1.0) * 100) + "%" + "\n"
                    + "**Abandons: **" + player.getAbandons(), true);
            embed.appendField("**Skill**", "**Mean: **" + twoPlaces.format(player.getSkill()) + "\n"
                    + " **StDev: **" + twoPlaces.format(player.getSkillStandardDeviation()) + "\n"
                    + " **K/D: **" + twoPlaces.format(player.getRankedKills() / (player.getRankedDeaths() * 1.0)), true);

            event.reply(embed.build());
        } else {
            event.reply("*That player does not exist!*");
        }
    }

    @Command(label = "rainbowOp",
            description = "Pulls operator statistics from Rainbow Six: Siege",
            alias = "r6op",
            permissionLevel = PermissionLevel.MEMBER)
    public void rainbowOp(CommandEvent event) {
        if (event.getArgs().length == 0) {
            event.reply("*You must enter a username to search*");
            return;
        } else if (event.getArgs().length == 1) {
            event.reply("*You must put either 'atk' or 'def' after the username*");
            return;
        } else if (event.getArgs().length == 2) {
            if (!(event.getArgs()[1].equalsIgnoreCase("atk") || event.getArgs()[1].equalsIgnoreCase("def"))) {
                event.reply("*You must put either 'atk' or 'def' after the username*");
                return;
            }
        }

        if (r6.playerExists(event.getArgs()[0], Platform.UPLAY)) {
            R6Player player = r6.getPlayerByName(event.getArgs()[0], Platform.UPLAY);
            OperatorStats opStats = player.getTopOperator(event.getArgs()[1]);

            EmbedBuilder embed = new EmbedBuilder();

            embed.withAuthorName(player.getName());
            embed.withAuthorIcon(player.getAvatarUrl());

            embed.withColor(Color.GRAY);
            embed.appendDesc("Operator stats");

            embed.withThumbnail(player.getTopOperator(event.getArgs()[1]).getOperator().getBadgeUrl());
            embed.appendField("Top " + event.getArgs()[1].toUpperCase() + " Operator", player.getTopOperator(event.getArgs()[1]).getOperator().getDisplayName() + " - " + twoPlaces.format(opStats.getTimePlayed() / 3600.0) + "hrs", true);

            embed.appendField("**Statistics**", "**Wins: **" + opStats.getWins() + " **Losses: **" + opStats.getLosses() + "\n"
                    + "**Win Rate: **" + twoPlaces.format(opStats.getWins() / (opStats.getLosses() + opStats.getWins() * 1.0) * 100) + "%", true);

            embed.appendField("**Skill**", "**Kills: **" + opStats.getKills() + " **Deaths: **" + opStats.getDeaths() + "\n"
                    + " **K/D: **" + twoPlaces.format(opStats.getKills() / (opStats.getDeaths() * 1.0)), true);

            event.reply(embed.build());

        }
    }
}
