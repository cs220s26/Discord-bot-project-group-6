package typingracebot.application;

import java.util.List;
import java.util.Random;

public class TextProvider {

    private final List<String> texts;
    private final Random random = new Random();

    public TextProvider(List<String> texts) {
        this.texts = texts;
    }

    public String getRandomText() {
        if (texts == null || texts.isEmpty()) {
            return "Typing races are fun and improve your speed!";
        }
        int index = random.nextInt(texts.size());
        return texts.get(index);
    }
}
