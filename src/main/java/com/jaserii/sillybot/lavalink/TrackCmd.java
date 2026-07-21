package com.jaserii.sillybot.lavalink;

import com.jaserii.sillybot.discord_commands.IDiscordCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackCmd implements IDiscordCommand {
    private static final Logger logger = LoggerFactory.getLogger(TrackCmd.class);

    private String name = "track";
    private String desc = "Get the name of the currently played track";

    private LavalinkService lavalinkService;

    public TrackCmd(LavalinkService lavalinkService) {
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
        lavalinkService.getLavalinkClient().getOrCreateLink(guild.getIdLong())
                .getPlayer()
                .subscribe((player) -> {
                    commandEvent.getHook().sendMessage(player.getTrack().getInfo().getTitle()).queue();
                });
    }
}
