package com.jaserii.sillybot;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface IDiscordCommand {
    /// Get the name of the commandEvent as a String
    /// @return commandEvent name
    String getName();

    /// Get the description of the commandEvent as a String
    /// @return commandEvent description
    String getDescription();

    /// Returns a SlashCommandData to be added to JDA via updateCommands().
    /// @return SlashCommandData
    SlashCommandData getCommandData();

    /// Executes the main logic of the feature associated with the DiscordCommand
    /// @param commandEvent Info related to the slash command used
    void execute(SlashCommandInteractionEvent commandEvent);
}
