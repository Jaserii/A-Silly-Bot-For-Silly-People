package com.jaserii.sillybot;

import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import javax.security.auth.login.LoginException;

public class Main {
    static void main() throws LoginException, InterruptedException {
        String botToken = System.getenv("BOT_TOKEN");
        LavalinkClient client = new LavalinkClient(
                Helpers.getUserIdFromToken(botToken)
        );

        JDA api = JDABuilder.createDefault(botToken)
                .addEventListeners(new CommandList())
                .setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(client))
                .build()
                .awaitReady();

        api.updateCommands().addCommands(
                Commands.slash("say", "Says Blehhhhhh"),
                Commands.slash("trivia", "Pull up random trivia"),
                Commands.slash("mtg_search", "Look up cards on Scryfall")
                        .addOption(OptionType.STRING, "query", "Enter what you are looking for"),
                Commands.slash("mtg_help", "[WIP] Ask... eugh.... AI for help")
                        .addOption(OptionType.STRING, "query", "Ask a question"),
                Commands.slash("play", "Play some audio")
                        .addOption(OptionType.STRING, "query", "URL or name")
        ).queue();
    }
}
