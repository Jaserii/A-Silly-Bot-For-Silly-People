package com.jaserii.sillybot.lavalink;

import com.jaserii.sillybot.discord_commands.IDiscordCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopCmd implements IDiscordCommand {
    private static final Logger logger = LoggerFactory.getLogger(StopCmd.class);

    private String name = "stop";
    private String desc = "Stop the currently playing track";

    private LavalinkService lavalinkService;

    public StopCmd(LavalinkService lavalinkService) {
        this.lavalinkService = lavalinkService;
    }

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
        commandEvent.deferReply().queue();

        Guild guild = commandEvent.getGuild();
        GuildVoiceState selfVoiceState = guild.getSelfMember().getVoiceState();

        if (selfVoiceState == null || !selfVoiceState.inAudioChannel()) {
            commandEvent.getHook().sendMessage("I'm not even in vc dawg").queue();
            return;
        }
        commandEvent.getHook().sendMessage("Stopped the current track and clearing the queue").queue();
        lavalinkService.getOrCreateMusicManager(commandEvent.getGuild().getIdLong()).stop();
        commandEvent.getJDA().getDirectAudioController().disconnect(guild);
    }
}
