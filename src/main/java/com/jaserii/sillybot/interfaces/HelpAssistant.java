package com.jaserii.sillybot.interfaces;

import dev.langchain4j.service.SystemMessage;

public interface HelpAssistant {
    @SystemMessage("""
        CRITICAL: Only answer questions related to Magic: The Gathering. Keep answers short and concise.
        
        IMPORTANT: Use short answers. Limit to 1500 characters.
        """)
    String chat(String userMessage);
}
