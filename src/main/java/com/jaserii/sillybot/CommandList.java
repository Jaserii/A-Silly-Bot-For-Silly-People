package com.jaserii.sillybot;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.text.StringEscapeUtils;

public class CommandList extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "say":
                event.reply("Blehhhhhh").queue();
                break;
            case "trivia":
                String fact = StringEscapeUtils.unescapeHtml4(new TriviaService().getTrivia());
                System.out.println(fact);
                event.reply(fact).queue();
                break;
        }
    }
}
