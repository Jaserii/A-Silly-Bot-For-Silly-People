package com.jaserii.sillybot;

import com.jaserii.sillybot.lavalink.GuildMusicManager;
import dev.arbjerg.lavalink.client.LavalinkClient;

import java.util.HashMap;
import java.util.Map;

public class LavalinkService {

    // Vars specific to LavalinkClient
    private LavalinkClient lavalinkClient;
    private final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

    public LavalinkService(LavalinkClient lavalinkClient) {
        this.lavalinkClient = lavalinkClient;
    }

    public LavalinkClient getLavalinkClient() {
        return lavalinkClient;
    }


    /// Retrieve a GuildMusicManager
    /// @param guildId
    /// @return
    public GuildMusicManager getOrCreateMusicManager(long guildId) {
        synchronized(this) {
            var mng = musicManagers.get(guildId);

            if (mng == null) {
                mng = new GuildMusicManager(guildId, lavalinkClient);
                musicManagers.put(guildId, mng);
            }

            return mng;
        }
    }

}
