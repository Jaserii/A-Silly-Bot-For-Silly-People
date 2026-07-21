package com.jaserii.sillybot.agentsystem;

import dev.langchain4j.service.SystemMessage;

public interface ISyntaxAssistant {
    @SystemMessage("""
        Convert user question to Scryfall search syntax: https://scryfall.com/docs/syntax
        
        Convert the user's natural-language Magic: The Gathering search
                        into valid Scryfall search syntax. Ensure that the cards are in English.
        
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
