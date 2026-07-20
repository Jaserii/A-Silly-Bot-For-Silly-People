package com.jaserii.sillybot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// Uses an OpenAI-compatible API to decipher natural language,
/// converting it to Scryfall's search params and returning a URL.
public class ScryfallService {
    private static final Logger logger = LoggerFactory.getLogger(ScryfallService.class);

    public ScryfallService() {
        
    }

    /* Public method: Get Scryfall URL
            -Send user query to LLM model, receive syntax results as string
            -Convert syntax string to URL encoding
            -Return final URL
     */

    /// Uses LLM via LangChain4J to determine Scryfall syntax from natural language
    /// @param query What the user has input
    /// @return "Black cards" -> "c:b"
    private String determineSyntax(String query) {
        return "";
    }

    /// Convert syntax to appropriate URL encoding
    /// @param syntax Syntax as determined by LLM
    /// @return "c:b" -> "c%3Ab"
    private String encodeSyntax(String syntax) {
        return "";
    }
}
