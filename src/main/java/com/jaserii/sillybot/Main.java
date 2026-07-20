package com.jaserii.sillybot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import javax.security.auth.login.LoginException;

public class Main {
    static void main() throws LoginException, InterruptedException {
        JDA api = JDABuilder.createDefault(System.getenv("BOT_TOKEN"))
                .addEventListeners(new CommandList())
                .build()
                .awaitReady();

        api.updateCommands().addCommands(
                Commands.slash("say", "Says Blehhhhhh"),
                Commands.slash("trivia", "Pull up random trivia"),
                Commands.slash("mtg_search", "Look up cards on Scryfall")
                        .addOption(OptionType.STRING, "query", "Enter what you are looking for"),
                Commands.slash("mtg_help", "[WIP] Ask... eugh.... AI for help")
                        .addOption(OptionType.STRING, "query", "Ask a question")
        ).queue();
    }
}
