package com.jaserii.sillybot;

import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.NodeOptions;
import dev.arbjerg.lavalink.client.event.*;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Optional;

import static net.dv8tion.jda.api.managers.AudioManager.LOG;

public class Main {
    private static CommandList commandList;

    static void main() throws LoginException, InterruptedException {
        String botToken = System.getenv("BOT_TOKEN");
        LavalinkClient client = new LavalinkClient(
                Helpers.getUserIdFromToken(botToken)
        );
        client.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());

        registerLavalinkListeners(client);
        registerLavalinkNodes(client);

        commandList = new CommandList();

        JDA api = JDABuilder.createDefault(botToken)
                .addEventListeners(commandList)
                .setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(client))
                .build()
                .awaitReady();

        commandList.setJDA(api, client);

        api.updateCommands().addCommands(
                Commands.slash("say", "Says Blehhhhhh"),
                Commands.slash("trivia", "Pull up random trivia"),
                Commands.slash("mtg_search", "Look up cards on Scryfall")
                        .addOption(OptionType.STRING, "query", "Enter what you are looking for"),
                Commands.slash("mtg_help", "[WIP] Ask... eugh.... AI for help")
                        .addOption(OptionType.STRING, "query", "Ask a question"),
                Commands.slash("play", "Play some audio")
                        .addOption(OptionType.STRING, "query", "URL or name"),
                Commands.slash("stop", "End audio"),
                Commands.slash("pause", "(Un)Pause audio"),
                Commands.slash("skip", "Play the next track if available"),
                Commands.slash("track", "Get the current name of the track")
        ).queue();
    }

    private static void registerLavalinkNodes(LavalinkClient client) {
        List.of(
                client.addNode(
                        new NodeOptions.Builder()
                                .setName("localhost")
                                .setServerUri("ws://0.0.0.0")
                                .setPassword("youshallnotpass")
                                .build()
                )

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

    private static void registerLavalinkListeners(LavalinkClient client) {
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
            Optional.ofNullable(commandList.musicManagers.get(event.getGuildId())).ifPresent(
                    (mng) -> mng.scheduler.onTrackStart(event.getTrack())
            );
        });

        client.on(TrackEndEvent.class).subscribe((event) -> {
            Optional.ofNullable(commandList.musicManagers.get(event.getGuildId())).ifPresent(
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
