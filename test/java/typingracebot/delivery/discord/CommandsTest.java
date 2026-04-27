/*
 * Read CONTRIBUTING.md before making any changes to this file
 */

package typingracebot.delivery.discord;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;
import typingracebot.model.Round;

public class CommandsTest {

    @Test
    public void startRaceMessageIncludesHostAndRounds() {
        assertEquals(
                "🔥🚗 **Typing Race Created!**\n\n"
                        + "Host: <@42>\n"
                        + "Rounds: 5\n\n"
                        + "**How to Play:**\n"
                        + "• Players join using `/join`\n"
                        + "• Host starts Round 1 using `/begin`\n"
                        + "• Type fast & accurately to score points\n"
                        + "• Score = correct words / time\n"
                        + "• Highest total score after all rounds wins 🏁",
                StartRaceCommand.buildStartRaceMessage(42L, 5));
    }

    @Test
    public void joinMessageIncludesUserMention() {
        assertEquals(
                "🚗 Racer <@42> has joined the grid!",
                JoinCommand.buildJoinMessage(42L));
    }

    @Test
    public void beginMessageShowsRoundNumberAndText() {
        Round round = new Round(2, "hello world", 123L);

        assertEquals(
                "🔥 **Round 2 — GO!**\n\n"
                        + "Type this paragraph:\n"
                        + "hello world",
                BeginCommand.buildBeginMessage(round));
    }

    @Test
    public void statsMessageFormatsEfficiencyToTwoDecimals() {
        assertEquals(
                "📊 **Stats for Person**\nTotal Efficiency Score: **12.35**",
                StatsCommand.buildStatsMessage("Person", 12.3456));
    }
}
