package com.jaserii.sillybot;

import com.jaserii.sillybot.lavaplayer.MusicService;
import com.jaserii.sillybot.lavaplayer.PlayerManager;
import com.jaserii.sillybot.lavaplayer.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class CommandList extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CommandList.class);
    private TriviaService triviaService = new TriviaService();
    private ScryfallService scryfallService = new ScryfallService();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        switch (event.getName()) {
            case "say":
                event.getHook().sendMessage("Blehhhhhh").queue();
                break;

            case "trivia":
                String fact = StringEscapeUtils.unescapeHtml4(triviaService.getTrivia());
                logger.info(fact);
                event.getHook().sendMessage(fact).queue();
                break;

            case "mtg_search":
                String query = (event.getOption("query") != null) ? event.getOption("query").getAsString() : "";
                if (query.isBlank())
                    event.reply("Please input something. ANYTHING!").queue();
                else
                    event.getHook().sendMessage(scryfallService.getScryfallURL(query)).queue();
                break;

            case "mtg_help":
                query = (event.getOption("query") != null) ? event.getOption("query").getAsString() : "";
                if (query.isBlank())
                    event.reply("Please input something. ANYTHING!").queue();
                else
                    event.getHook().sendMessage(scryfallService.getMTGHelp(query)).queue();
                break;

            case "play":
                // Make sure we only respond to events that occur in a guild
                if (!event.isFromGuild()) return;
                if (event.getUser().isBot()) return;

                Member member = event.getMember();
                GuildVoiceState memberVoiceState = member.getVoiceState();

                if(!memberVoiceState.inAudioChannel()) {
                    event.getHook().sendMessage("You need to be in a voice channel").queue();
                    return;
                }

                Member self = event.getGuild().getSelfMember();
                GuildVoiceState selfVoiceState = self.getVoiceState();

                if(!selfVoiceState.inAudioChannel()) {
                    event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
                } else {
                    if(selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
                        event.getHook().sendMessage("You need to be in the same channel as me").queue();
                        return;
                    }
                }

                String name = event.getOption("query").getAsString();
                try {
                    new URI(name);
                } catch (URISyntaxException e) {
                    name = "ytsearch:" + name;
                }

                PlayerManager playerManager = PlayerManager.get();
                event.getHook().sendMessage("Playing").queue();
                playerManager.play(event.getGuild(), name);
        }
    }
}
