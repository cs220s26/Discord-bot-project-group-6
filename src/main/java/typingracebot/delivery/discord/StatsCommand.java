package typingracebot.delivery.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import typingracebot.application.StatsManager;

public class StatsCommand extends ListenerAdapter {
    private final StatsManager statsManager;

    public StatsCommand(StatsManager statsManager) {
        this.statsManager = statsManager;
    }

    static String buildStatsMessage(String userName, double totalEfficiency) {
        return "📊 **Stats for " + userName + "**\n"
                + "Total Efficiency Score: **"
                + String.format("%.2f", totalEfficiency)
                + "**";
    }

    @Override
    public void onSlashCommandInteraction(
            @NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("stats")) {
            return;
        }

        event.deferReply().queue();

        long userId = event.getUser().getIdLong();
        double totalEfficiency = statsManager.getUserStats(userId);

        event.getHook().sendMessage(
                buildStatsMessage(event.getUser().getName(), totalEfficiency)).queue();
    }
}
