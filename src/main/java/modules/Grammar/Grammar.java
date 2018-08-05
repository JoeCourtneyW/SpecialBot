package modules.Grammar;

import main.GuildOptions.GuildOptions;
import main.SpecialModule;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

public class Grammar implements SpecialModule {

    JLanguageTool langTool;

    @Override
    public boolean onLoad() {
        bot.registerHandlers(this);
        langTool = new JLanguageTool(new AmericanEnglish());
        return true;
    }

    @EventSubscriber
    public void onMessage(MessageReceivedEvent event) throws IOException {
        GuildOptions options = bot.getGuildOptions(event.getGuild());
        if (!event.getMessage().getContent().startsWith(options.PREFIX)) {

            List<RuleMatch> matches = langTool.check(event.getMessage().getContent());
            StringJoiner correctionMessage = new StringJoiner(" ");
            for (RuleMatch match : matches) {
                correctionMessage.add(match.getMessage());
            }

            if(matches.size() > 0)
                bot.sendChannelMessage(event.getAuthor().mention() + ", " + correctionMessage.toString(), event.getChannel());
        }
    }


    public String getName() {
        return "Grammar";
    }

    public String getVersion() {
        return "0.1";
    }
}
