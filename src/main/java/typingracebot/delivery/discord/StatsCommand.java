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
                "📊 **Stats for "
                        + event.getUser().getName() + "**\n"
                        + "Total Efficiency Score: **"
                        + String.format("%.2f", totalEfficiency)
                        + "**").queue();
    }
}
