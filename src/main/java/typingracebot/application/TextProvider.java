package typingracebot.application;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TextProvider {
    private final List<String> texts;
    private final Random random = new Random();

    // Default constructor for App.java
    public TextProvider() {
        this.texts = Arrays.asList(
                "The quick brown fox jumps over the lazy dog.",
                "DevOps is the combination of cultural philosophies, practices, and tools.",
                "Java is a high-level, class-based, object-oriented programming language.",
                "Discord bots are great for learning how to handle asynchronous events.",
                "Typing fast requires consistent practice and good muscle memory."
        );
    }

    public TextProvider(List<String> customTexts) {
        this.texts = customTexts;
    }

    public String getRandomText() {
        return texts.get(random.nextInt(texts.size()));
    }
}
