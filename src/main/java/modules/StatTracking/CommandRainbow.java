package modules.StatTracking;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import main.com.github.courtneyjoew.R6J;
import main.com.github.courtneyjoew.R6Player;
import main.com.github.courtneyjoew.declarations.Platform;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.text.DecimalFormat;

public class CommandRainbow implements CommandExecutor {

    private R6J r6 = StatTracking.instance.rainbowSix;
    private static final DecimalFormat twoPlaces = new DecimalFormat("#.##");

    @Command(label = "rainbow", description = "Pulls user statistics in Rainbow Six Siege", alias = "r6")
    public void rainbow(CommandEvent event) {
        if (event.getArgs().length == 0) {
            event.reply("*You must enter a username to search*");
            return;
        }

        if(r6.playerExists(event.getArgs()[0], Platform.UPLAY)) {

            R6Player player = r6.getPlayerByName(event.getArgs()[0], Platform.UPLAY);

            EmbedBuilder embed = new EmbedBuilder();

            embed.withAuthorName(player.getName());
            embed.withAuthorIcon(player.getAvatarUrl());

            embed.withColor(Color.GRAY);
            embed.appendDesc("Ranked stats in NA UPlay Servers");

            embed.withThumbnail(player.getRank().getIconUrl());
            embed.appendField("**Rank**", player.getRank().getDisplayName(), true);

            embed.appendField("**MMR**", "**" + twoPlaces.format(player.getMmr()) + "** (" + twoPlaces.format(player.getMaxMmr()) + ")", true);
            embed.appendField("**Statistics**", "**Wins: **" + player.getRankedWins() + " **Losses: **" + player.getRankedLosses() + "\n"
                    + "**Win Rate: **" + twoPlaces.format(player.getRankedWins() / (player.getRankedLosses() + player.getRankedWins()*1.0) * 100) + "%" + "\n"
                    + "**Abandons: **" + player.getAbandons(), true);
            embed.appendField("**Skill**", "**Mean: **" + twoPlaces.format(player.getSkill()) + "\n"
                    + " **StDev: **" + twoPlaces.format(player.getSkillStandardDeviation()) + "\n"
                    + " **K/D: **" + twoPlaces.format(player.getKills() / (player.getDeaths() * 1.0)), true);
            //TODO: Wait for ranked K/D to be available instead of global K/D, requires R6J update

            event.reply(embed.build());
        } else {
            event.reply("*That player does not exist!*");
        }
    }
}
