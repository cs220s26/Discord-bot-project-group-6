package typingracebot.core;

import org.junit.jupiter.api.Test;
import typingracebot.model.Round;

import static org.junit.jupiter.api.Assertions.*;

class RoundTest {

    @Test
    void countsCorrectWordsIgnoringCase() {
        Round r = new Round(1, "Hello World Test", 0);
        assertEquals(3, r.countCorrectWords("hello world test"));
    }

    @Test
    void stopsAtShortestLength() {
        Round r = new Round(1, "a b c", 0);
        assertEquals(2, r.countCorrectWords("a b x y z"));
    }

    @Test
    void returnsZeroIfNullInput() {
        Round r = new Round(1, "hello world", 0);
        assertEquals(0, r.countCorrectWords(null));
    }
}
