package com.jaserii.sillybot;

import com.jaserii.sillybot.discord_commands.IDiscordCommand;
import com.jaserii.sillybot.discord_commands.PlayCmd;
import com.jaserii.sillybot.discord_commands.ScryfallCmd;
import com.jaserii.sillybot.discord_commands.TriviaCmd;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandListener extends ListenerAdapter {
    private final Map<String, IDiscordCommand> commands = new HashMap<>();

    /// Update constructor with each new command
    public CommandListener(LavalinkClient lavalinkClient) {
        registerCommand(new TriviaCmd());
        registerCommand(new ScryfallCmd());
        registerCommand(new PlayCmd(lavalinkClient));
    }

    private void registerCommand(IDiscordCommand cmd) {
        // Use the command data name as the map key
        commands.put(cmd.getCommandData().getName(), cmd);
    }

    /// Duplicate HashMap to ArrayList, returning ArrayList for JDA.updateCommand()
    /// @return ArrayList version of HashMap commands
    public List<SlashCommandData> getDiscordCommandData() {
        List<SlashCommandData> dataList = new ArrayList<>();
        for (IDiscordCommand cmd : commands.values()) {
            dataList.add(cmd.getCommandData());
        }
        return dataList;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        IDiscordCommand command = commands.get(event.getName());
        if (command != null) {
            command.execute(event);
        }
    }




}
