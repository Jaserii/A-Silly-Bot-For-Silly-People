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
        Convert user question to Scryfall search syntax. CRITICAL: ONLY SCRYFALL 
        AND MAGIC: THE GATHERING RELATED PROMPTS WILL BE ANSWERED.
        Example: If a user says 'Gimme some black cards with mana 3 or less', you return
        'cmc<=3 color=black'
        """)
        String chat(String userMessage);
    }

    public ScryfallService() {
        model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(System.getenv("AI_MODEL"))
                .build();
        assistant = AiServices.create(Assistant.class, model);
    }

    /// Returns a URL containing cards matching user request.
    /// @param query
    /// @return
    public String getScryfallURL(String query) {
        String syntax = assistant.chat(query);
        return "https://scryfall.com/search?q=" + URLEncoder.encode(syntax, StandardCharsets.UTF_8);
    }
}
