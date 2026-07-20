package com.jaserii.sillybot;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/// Uses an OpenAI-compatible API to decipher natural language,
/// converting it to Scryfall's search params and returning a URL.
public class ScryfallService {
    private static final Logger logger = LoggerFactory.getLogger(ScryfallService.class);
    private OpenAiChatModel model;
    private final Assistant assistant;

    interface Assistant {
        @SystemMessage("""
        Convert user question to Scryfall search syntax: https://scryfall.com/docs/syntax
        
        Convert the user's natural-language Magic: The Gathering search
                        into valid Scryfall search syntax.
        
        Return only the Scryfall query.
        Do not use Markdown.
        Do not explain the query.
        Do not wrap the query in quotation marks.
        Use only documented Scryfall operators.

        Important rules:
        - Use c: for card colors.
        - Use id: for Commander color identity.
        - Use t: for card types.
        - Use o: for Oracle text.
        - Use f: for format legality.
        - Use mv for mana value.
        - Use parentheses for OR expressions.
        - Use a minus sign to exclude a condition.
        """)
        String chat(String userMessage);
    }

    public ScryfallService() {
        model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .baseUrl(System.getenv("BASE_URL"))
                .modelName(System.getenv("AI_MODEL"))
                .build();
        assistant = AiServices.create(Assistant.class, model);
    }

    /// Returns a URL containing cards matching user request.
    /// @param query
    /// @return
    public String getScryfallURL(String query) {
        String syntax = assistant.chat(query);
        logger.info(syntax);
        String url = "https://scryfall.com/search?q=" + URLEncoder.encode(syntax, StandardCharsets.UTF_8);
        logger.info(url);
        return url;
    }
}
