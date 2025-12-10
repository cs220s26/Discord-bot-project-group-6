package typingracebot.delivery.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import typingracebot.application.StatsManager;
import typingracebot.model.PlayerStats;

public class StatsCommand extends ListenerAdapter {

    private final StatsManager statsManager;

    public StatsCommand(StatsManager statsManager) {
        this.statsManager = statsManager;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equals("stats")) return;

        long userId = event.getUser().getIdLong();
        PlayerStats stats = statsManager.getStats(userId);

        if (stats == null) {
            event.reply("📊 No stats available yet. Join a race to begin!")
                    .queue();
            return;
        }

        double totalSeconds = stats.getTotalMilliseconds() / 1000.0;
        double efficiencyScore = totalSeconds == 0
                ? 0
                : stats.getTotalCorrectWords() / totalSeconds;

        event.reply(
                "📊 **Your Typing Stats**\n\n" +
                        "🏁 Total Races: **" + stats.getTotalRaces() + "**\n" +
                        "🔤 Total Correct Words: **" + stats.getTotalCorrectWords() + "**\n" +
                        "⏱️ Total Time: **" + String.format("%.3f", totalSeconds) + "s**\n" +
                        "🔥 Efficiency Score: **" + String.format("%.3f", efficiencyScore) + "**\n" +
                        "🏆 Best Single-Round Correct Words: **" + stats.getBestScore() + "**"
        ).queue();
    }
}
