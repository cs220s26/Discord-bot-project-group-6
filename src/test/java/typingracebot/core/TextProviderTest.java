package typingracebot.core;

import org.junit.jupiter.api.Test;
import typingracebot.application.TextProvider;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextProviderTest {

    @Test
    void returnsRandomText() {
        TextProvider tp = new TextProvider(List.of("a", "b", "c"));
        String text = tp.getRandomText();
        assertTrue(List.of("a", "b", "c").contains(text));
    }

    @Test
    void returnsFallbackIfEmpty() {
        TextProvider tp = new TextProvider(List.of());
        assertNotNull(tp.getRandomText());
    }
}
