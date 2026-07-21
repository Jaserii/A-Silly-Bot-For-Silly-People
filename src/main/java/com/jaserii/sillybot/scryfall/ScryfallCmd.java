package com.jaserii.sillybot.scryfall;

import com.jaserii.sillybot.discord_commands.IDiscordCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScryfallCmd implements IDiscordCommand {
    private static final Logger logger = LoggerFactory.getLogger(ScryfallCmd.class);

    private String name = "scryfall";
    private String desc = "Convert natural language to Scryfall search syntax";
    private String param = "query";
    private String option_desc = "Enter natural text here";

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
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, param, option_desc);
    }

    @Override
    public void execute(SlashCommandInteractionEvent commandEvent) {
        commandEvent.deferReply().queue();
        ScryfallService scryfallService = new ScryfallService();

        String query = (commandEvent.getOption(param) != null) ? commandEvent.getOption(param).getAsString() : "";
        if (query.isBlank())
            commandEvent.getHook().sendMessage("Please input something. ANYTHING!").queue();
        else
            commandEvent.getHook().sendMessage(scryfallService.getScryfallURL(query)).queue();
    }
}
