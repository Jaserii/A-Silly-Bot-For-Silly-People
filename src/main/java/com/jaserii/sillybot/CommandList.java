package com.jaserii.sillybot;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandList extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CommandList.class);
    private TriviaService triviaService = new TriviaService();
    private ScryfallService scryfallService = new ScryfallService();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        switch (event.getName()) {
            case "say":
                event.reply("Blehhhhhh").queue();
                break;
            case "trivia":
                String fact = StringEscapeUtils.unescapeHtml4(triviaService.getTrivia());
                logger.info(fact);
                event.reply(fact).queue();
                break;
            case "mtg":
                String query = (event.getOption("query") != null) ? event.getOption("query").getAsString() : "";
                if (query.isBlank())
                    event.reply("Please input something. ANYTHING!").queue();
                else
                    event.getHook().sendMessage(scryfallService.getScryfallURL(query)).queue();
                break;
        }
    }
}
