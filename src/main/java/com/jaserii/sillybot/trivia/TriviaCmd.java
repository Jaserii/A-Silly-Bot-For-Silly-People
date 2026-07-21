package com.jaserii.sillybot.trivia;

import com.jaserii.sillybot.discord_commands.IDiscordCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TriviaCmd implements IDiscordCommand {
    private static final Logger logger = LoggerFactory.getLogger(TriviaCmd.class);

    private String name = "trivia";
    private String desc = "Pull up random trivia";


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent commandEvent) {
        TriviaService triviaService = new TriviaService();
        String fact = StringEscapeUtils.unescapeHtml4(triviaService.getTrivia());
        logger.info(fact);
        commandEvent.reply(fact).queue();
    }
}
