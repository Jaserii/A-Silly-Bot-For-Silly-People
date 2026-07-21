package com.jaserii.sillybot;

import com.jaserii.sillybot.lavalink.AudioLoader;
import com.jaserii.sillybot.lavalink.GuildMusicManager;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class CommandList extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CommandList.class);
    private TriviaService triviaService = new TriviaService();
    private ScryfallService scryfallService = new ScryfallService();

    private JDA api;
    private LavalinkClient client;
    public final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

    public void setJDA(JDA api, LavalinkClient client) {
        this.api = api;
        this.client = client;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        Guild guild = event.getGuild();
        if (guild == null)
            return;

        switch (event.getName()) {
            case "say":
                event.reply("Blehhhhhh").queue();
                break;

            case "trivia":
                String fact = StringEscapeUtils.unescapeHtml4(triviaService.getTrivia());
                logger.info(fact);
                event.getHook().sendMessage(fact).queue();
                break;

            case "mtg_search":
                String query = (event.getOption("query") != null) ? event.getOption("query").getAsString() : "";
                if (query.isBlank())
                    event.getHook().sendMessage("Please input something. ANYTHING!").queue();
                else
                    event.getHook().sendMessage(scryfallService.getScryfallURL(query)).queue();
                break;

            case "mtg_help":
                query = (event.getOption("query") != null) ? event.getOption("query").getAsString() : "";
                if (query.isBlank())
                    event.getHook().sendMessage("Please input something. ANYTHING!").queue();
                else
                    event.getHook().sendMessage(scryfallService.getMTGHelp(query)).queue();
                break;

            case "play":
                query = (event.getOption("query") != null) ? event.getOption("query").getAsString() : "";

                if (query.isBlank()) {
                    event.getHook().sendMessage("Please input something. ANYTHING!").queue();
                    return;
                }

                GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
                if(!memberVoiceState.inAudioChannel()) {
                    event.getHook().sendMessage("You need to be in a voice channel").queue();
                    return;
                }

                AudioChannel audioChannel = memberVoiceState.getChannel();
                api.getDirectAudioController().connect(audioChannel);

                query = event.getOption("query").getAsString();
                final long guildId = guild.getIdLong();
                final Link link = this.client.getOrCreateLink(guildId);
                final var mngr = this.getOrCreateMusicManager(guildId);

                link.loadItem(query).subscribe(new AudioLoader(event, mngr));
                break;

            case "stop":
                GuildVoiceState selfVoiceState = guild.getSelfMember().getVoiceState();
                if (selfVoiceState == null || !selfVoiceState.inAudioChannel()) {
                    event.getHook().sendMessage("I'm not even in vc dawg").queue();
                    break;
                }
                event.getHook().sendMessage("Stopped the current track and clearing the queue").queue();
                this.getOrCreateMusicManager(event.getGuild().getIdLong()).stop();
                event.getJDA().getDirectAudioController().disconnect(guild);
                break;

            case "pause":
                selfVoiceState = guild.getSelfMember().getVoiceState();
                if (selfVoiceState == null || !selfVoiceState.inAudioChannel()) {
                    event.getHook().sendMessage("I'm not even in vc dawg").queue();
                    break;
                }
                this.client.getOrCreateLink(guild.getIdLong())
                        .getPlayer()
                        .flatMap((player) -> player.setPaused(!player.getPaused()))
                        .subscribe((player) -> {
                            event.getHook().sendMessage("Player has been " + (player.getPaused() ? "paused" : "resumed") + "!").queue();
                        });
                break;

            case "skip":
                selfVoiceState = guild.getSelfMember().getVoiceState();
                if (selfVoiceState == null || !selfVoiceState.inAudioChannel()) {
                    event.getHook().sendMessage("I'm not even in vc dawg").queue();
                    break;
                }
                this.getOrCreateMusicManager(event.getGuild().getIdLong()).skip();
                event.getHook().sendMessage("Skipping current song").queue();
                break;

            case "track":
                selfVoiceState = guild.getSelfMember().getVoiceState();
                if (selfVoiceState == null || !selfVoiceState.inAudioChannel()) {
                    event.getHook().sendMessage("I'm not even in vc dawg").queue();
                    break;
                }
                this.client.getOrCreateLink(guild.getIdLong())
                        .getPlayer()
                        .subscribe((player) -> {
                            event.getHook().sendMessage(player.getTrack().getInfo().getTitle()).queue();
                        });
                break;
        }
    }

    private GuildMusicManager getOrCreateMusicManager(long guildId) {
        synchronized(this) {
            var mng = this.musicManagers.get(guildId);

            if (mng == null) {
                mng = new GuildMusicManager(guildId, this.client);
                this.musicManagers.put(guildId, mng);
            }

            return mng;
        }
    }
}
