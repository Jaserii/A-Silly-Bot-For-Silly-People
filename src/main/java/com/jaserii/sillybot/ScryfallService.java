package com.jaserii.sillybot;

import com.jaserii.sillybot.interfaces.HelpAssistant;
import com.jaserii.sillybot.interfaces.SyntaxAssistant;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/// Uses an OpenAI-compatible API to decipher natural language,
/// converting it to Scryfall's search params and returning a URL.
public class ScryfallService {
    private static final Logger logger = LoggerFactory.getLogger(ScryfallService.class);
    private ChatModel syntaxModel;
    private ChatModel helpModel;
    private SyntaxAssistant syntaxAssistant;
    private HelpAssistant helpAssistant;

    public ScryfallService() {

        /* --- Uncomment if you want to Use OpenAI API format ---
        syntaxModel = OpenAiChatModel.builder()
               .apiKey(System.getenv("API_KEY"))
                .baseUrl(System.getenv("BASE_URL"))
                .modelName(System.getenv("AI_MODEL"))
                .build();

        helpModel = OpenAiChatModel.builder()
               .apiKey(System.getenv("API_KEY"))
                .baseUrl(System.getenv("BASE_URL"))
                .modelName(System.getenv("AI_MODEL"))
                .build();
        */

        syntaxModel = GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("API_KEY"))
                .modelName(System.getenv("AI_MODEL"))
                .returnThinking(false)
                .build();

        helpModel = GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("API_KEY"))
                .modelName(System.getenv("AI_MODEL"))
                .returnThinking(false)
                .build();

        syntaxAssistant = AiServices.create(SyntaxAssistant.class, syntaxModel);
        helpAssistant = AiServices.create(HelpAssistant.class, helpModel);
    }

    /// Returns a URL containing cards matching user request.
    /// @param query The query asked by the user
    /// @return URL response
    public String getScryfallURL(String query) {
        try {
            String syntax = syntaxAssistant.chat(query);
            logger.info(syntax);
            String url = "https://scryfall.com/search?q=" + URLEncoder.encode(syntax, StandardCharsets.UTF_8);
            logger.info(url);
            return url;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "===SEARCH ERROR===";
        }
    }

    /// Return a generated response to a user question
    /// @param query The question asked by the user
    /// @return Generated and URL response
    public String getMTGHelp(String query) {
        try {
            String response = helpAssistant.chat(query);
            logger.info(response);
            return response;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "===HELP ERROR===";
        }
    }
}
