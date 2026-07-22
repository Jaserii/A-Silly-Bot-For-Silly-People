package com.jaserii.sillybot;

import com.jaserii.sillybot.lavalink.LavalinkService;
import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.NodeOptions;
import dev.arbjerg.lavalink.client.event.*;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Optional;

import static net.dv8tion.jda.api.managers.AudioManager.LOG;

public class Main {
    private static CommandList commandList;

    static void main() throws LoginException, InterruptedException {
        String botToken = System.getenv("BOT_TOKEN");

        // Create Lavalink Connections
        LavalinkClient lavalinkClient = new LavalinkClient(
                Helpers.getUserIdFromToken(botToken)
        );
        lavalinkClient.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());
        LavalinkService lavalinkService = new LavalinkService(lavalinkClient);
        lavalinkService.registerLavalinkListeners(lavalinkClient);
        lavalinkService.registerLavalinkNodes(lavalinkClient);

        // Create JDA instance
        CommandListener commandListener = new CommandListener(lavalinkService);
        JDA jda = JDABuilder.createDefault(botToken)
                .addEventListeners(commandListener)
                .setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(lavalinkClient))
                .build()
                .awaitReady();
        jda.updateCommands().addCommands(commandListener.getDiscordCommandData()).queue();
    }
}
