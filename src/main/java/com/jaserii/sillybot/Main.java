package com.jaserii.sillybot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import javax.security.auth.login.LoginException;

public class Main {
    static void main() throws LoginException, InterruptedException {
        JDA api = JDABuilder.createDefault(System.getenv("BOT_TOKEN"))
                .addEventListeners(new CommandList())
                .build()
                .awaitReady();

        api.updateCommands().addCommands(
                Commands.slash("say", "Says Blehhhhhh")
        ).queue();

        //TriviaService triviaService = new TriviaService();
        //System.out.println(triviaService.getTrivia());
    }
}
