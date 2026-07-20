package com.jaserii.sillybot;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandList extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CommandList.class);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "say":
                event.reply("Blehhhhhh").queue();
                break;
            case "trivia":
                String fact = StringEscapeUtils.unescapeHtml4(new TriviaService().getTrivia());
                logger.info(fact);
                event.reply(fact).queue();
                break;
        }
    }
}
