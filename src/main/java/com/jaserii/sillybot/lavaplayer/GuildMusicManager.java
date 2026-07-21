package com.jaserii.sillybot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;

public class GuildMusicManager {
    private TrackScheduler trackScheduler;
    private MusicService musicService;

    public GuildMusicManager(AudioPlayerManager manager, Guild guild) {
        AudioPlayer player = manager.createPlayer();
        trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);
        musicService = new MusicService(player, guild);
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    public MusicService getAudioForwarder() {
        return musicService;
    }
}
