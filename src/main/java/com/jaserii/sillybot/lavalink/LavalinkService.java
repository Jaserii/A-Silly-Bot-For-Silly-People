package com.jaserii.sillybot.lavalink;

import com.jaserii.sillybot.lavalink.sample.GuildMusicManager;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.NodeOptions;
import dev.arbjerg.lavalink.client.event.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.dv8tion.jda.api.managers.AudioManager.LOG;

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
                System.out.println("mng is null");
                mng = new GuildMusicManager(guildId, lavalinkClient);
                musicManagers.put(guildId, mng);
            }
            return mng;
        }
    }


    /// Registers a Lavalink Node with the Lavalink Spring Boot Server
    /// @param client Adds the node to the client
    public void registerLavalinkNodes(LavalinkClient client) {
        List.of(
                client.addNode(
                        new NodeOptions.Builder()
                                .setName(System.getenv("LAVALINK_HOST"))
                                .setServerUri("ws://" + System.getenv("LAVALINK_HOST") + ":" + System.getenv("LAVALINK_PORT"))
                                .setPassword(System.getenv("LAVALINK_PASSWORD"))
                                .build()
                )
                /* ===== Uncomment if you wanna test it locally and not via Docker
                ,
                client.addNode(
                        new NodeOptions.Builder()
                                .setName("localhost")
                                .setServerUri("ws://localhost")
                                .setPassword(System.getenv("LAVALINK_PASSWORD"))
                                .build()
                )
                */

        ).forEach((node) -> {
            node.on(TrackStartEvent.class).subscribe((event) -> {
                final LavalinkNode node1 = event.getNode();

                LOG.trace(
                        "{}: track started: {}",
                        node1.getName(),
                        event.getTrack().getInfo()
                );
            });
        });
    }

    /// Adds Listeners to the node associated with a Client
    /// @param client
    public void registerLavalinkListeners(LavalinkClient client) {
        client.on(ReadyEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            LOG.info(
                    "Node '{}' is ready, session id is '{}'!",
                    node.getName(),
                    event.getSessionId()
            );
        });

        client.on(StatsEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            LOG.info(
                    "Node '{}' has stats, current players: {}/{} (link count {})",
                    node.getName(),
                    event.getPlayingPlayers(),
                    event.getPlayers(),
                    client.getLinks().size()
            );
        });

        client.on(TrackStartEvent.class).subscribe((event) -> {
            Optional.ofNullable(musicManagers.get(event.getGuildId())).ifPresent(
                    (mng) -> mng.scheduler.onTrackStart(event.getTrack())
            );
        });

        client.on(TrackEndEvent.class).subscribe((event) -> {
            Optional.ofNullable(musicManagers.get(event.getGuildId())).ifPresent(
                    (mng) -> mng.scheduler.onTrackEnd(event.getEndReason())
            );
        });

        client.on(EmittedEvent.class).subscribe((event) -> {
            if (event instanceof TrackStartEvent) {
                LOG.info("Is a track start event!");
            }

            final var node = event.getNode();

            LOG.info(
                    "Node '{}' emitted event: {}",
                    node.getName(),
                    event
            );
        });
    }
}
