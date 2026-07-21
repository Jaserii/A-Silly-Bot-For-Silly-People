package com.jaserii.sillybot.discord_commands;

import com.jaserii.sillybot.LavalinkService;
import com.jaserii.sillybot.lavalink.AudioLoader;
import com.jaserii.sillybot.lavalink.GuildMusicManager;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PlayCmd implements IDiscordCommand {
    private static final Logger logger = LoggerFactory.getLogger(PlayCmd.class);

    private String name = "play";
    private String desc = "Play audio from YouTube (and other sources)";
    private String param = "query";
    private String param_desc = "Enter URL or search here";

    private LavalinkService lavalinkService;

    public PlayCmd(LavalinkClient lavalinkClient) {
        lavalinkService = new LavalinkService(lavalinkClient);
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
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, param, param_desc);
    }

    @Override
    public void execute(SlashCommandInteractionEvent commandEvent) {
        commandEvent.deferReply().queue();
        JDA jda = commandEvent.getJDA();
        Guild guild = commandEvent.getGuild();

        // Check if input is empty
        String query = (commandEvent.getOption("query") != null) ? commandEvent.getOption("query").getAsString() : "";
        if (query.isBlank()) {
            commandEvent.getHook().sendMessage("Please input something. ANYTHING!").queue();
            return;
        }

        // Check if member that triggered event is in a call themselves
        GuildVoiceState memberVoiceState = commandEvent.getMember().getVoiceState();
        if(!memberVoiceState.inAudioChannel()) {
            commandEvent.getHook().sendMessage("You need to be in a voice channel").queue();
            return;
        }

        AudioChannel audioChannel = memberVoiceState.getChannel();
        jda.getDirectAudioController().connect(audioChannel);

        final long guildId = guild.getIdLong();
        final Link link = lavalinkService.getLavalinkClient().getOrCreateLink(guildId);
        final var musicManager = lavalinkService.getOrCreateMusicManager(guildId);

        if (!query.startsWith("http")) {
            query = "ytsearch:" + query;
        }

        link.loadItem(query).subscribe(new AudioLoader(commandEvent, musicManager));
    }



}
