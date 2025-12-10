package typingracebot.core;

import org.junit.jupiter.api.Test;
import typingracebot.model.RaceResult;

import static org.junit.jupiter.api.Assertions.*;

class RaceResultTest {

    @Test
    void efficiencyCalculatedCorrectly() {
        RaceResult r = new RaceResult(1L, 10, 2000);
        assertEquals(5.0, r.getEfficiency(), 0.001);
    }
}
