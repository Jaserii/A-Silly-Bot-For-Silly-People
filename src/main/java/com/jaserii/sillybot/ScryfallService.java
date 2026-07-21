package com.jaserii.sillybot;

import com.jaserii.sillybot.gemini_assistants.IHelpAssistant;
import com.jaserii.sillybot.gemini_assistants.ISyntaxAssistant;
import dev.langchain4j.exception.HttpException;
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
    private ISyntaxAssistant syntaxAssistant;

    public ScryfallService() {

        /* --- Uncomment if you want to Use OpenAI API format ---
        syntaxModel = OpenAiChatModel.builder()
               .apiKey(System.getenv("API_KEY"))
                .baseUrl(System.getenv("BASE_URL"))
                .modelName(System.getenv("AI_MODEL"))
                .build();
        */

        syntaxModel = GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("API_KEY"))
                .modelName(System.getenv("AI_MODEL"))
                .returnThinking(false)
                .timeout(Duration.ofMinutes(5))
                .build();

        syntaxAssistant = AiServices.create(ISyntaxAssistant.class, syntaxModel);
    }

    /// Returns a URL containing cards matching user request.
    /// @param query The query asked by the user
    /// @return URL response
    public String getScryfallURL(String query) {
        try {
            String syntax = syntaxAssistant.chat(query);
            logger.info("Converted syntax: " + syntax);
            String url = "https://scryfall.com/search?q=" + URLEncoder.encode(syntax, StandardCharsets.UTF_8);
            logger.info("URL: " + url);
            return url;
        } catch (HttpException e) {
            logger.error("HTTPException: " + e.statusCode());
            // Check if the status code is 429 (Too Many Requests)
            if (e.statusCode() == 429) {
                return "Error: The daily AI request limit has been reached. Please try again tomorrow.";
            }
            // Handle other HTTP errors (e.g., 401 Unauthorized, 500 Server Error)
            return "Error: An API error occurred (Status: " + e.statusCode() + ").";
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "Something went wrong. Whoever developed this catch message just lumped all exceptions together";
        }
    }
}
