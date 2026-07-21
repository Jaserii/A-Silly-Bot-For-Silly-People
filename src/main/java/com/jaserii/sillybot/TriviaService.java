package com.jaserii.sillybot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TriviaService extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TriviaService.class);
    final String IO_ERR = "IO_ERR";
    final String INTERRUPT_ERR = "INTERRUPT_ERR";

    /// Handle slash commands pertaining to the Trivia Feature.
    /// @param event The slash command event in question.
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null)
            return;

        // Sure, I could use an If statement. But in the future, if I have more commands, switch will already be set
        switch (event.getName()) {
            // Format trivia from OpenTriviaDB response
            case "trivia":
                String fact = StringEscapeUtils.unescapeHtml4(getTrivia());
                logger.info("Trivia Generated: " + fact);
                event.reply(fact).queue();
                break;
        }
    }

    /// Gather a random fact using Open Trivia DB API
    /// @return String fact
    private String getTrivia() {
        String apiUrl = System.getenv("TRIVIA_API_URL");

        if (apiUrl == null) {
            return "Missing API URL for Open Trivia DB";
        }

        String response = getResult(apiUrl);
        logger.info("Raw JSON: " + response);
        switch (response) {
            case IO_ERR:
                return "Erhm... IO Error!";
            case INTERRUPT_ERR:
                return "Erhm... I got interrupted!";
            default:
                try {
                    return parseResult(response);
                } catch (JsonProcessingException e) {
                    return "Something's wrong with the JSON";
                }
        }
    }


    /// Gather a random fact from Open Trivia DB
    /// @param api The API url
    /// @return Returns either the trivia Q&A or an error code
    private String getResult(String api) {
        try (HttpClient client = HttpClient.newHttpClient()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(api))
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

                return response.body();

            } catch (IOException e) {
                return IO_ERR;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return INTERRUPT_ERR;
            }
        }
    }


    /// Parse the question and correct answer from the response body
    /// @param responseBody Contains the response body
    /// @return Return parsed result
    private String parseResult(String responseBody) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(responseBody);

        JsonNode results = root
                .get("results")
                .get(0);

        String suffix = (results.get("type").asText().equals("boolean")) ? " (T/F) " : "";

        String question = results
                .get("question")
                .asText();

        String correctAnswer = results
                .get("correct_answer")
                .asText();

        return question + suffix + " \n||" + correctAnswer + "||";
    }
}
