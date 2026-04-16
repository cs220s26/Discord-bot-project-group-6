package typingracebot.delivery.discord;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import typingracebot.application.RaceManager;
import typingracebot.application.StatsManager;
import typingracebot.model.RaceResult;

public class TypeResultListener extends ListenerAdapter {
    private final RaceManager raceManager;
    private final StatsManager statsManager;

    public TypeResultListener(RaceManager raceManager, StatsManager statsManager) {
        this.raceManager = raceManager;
        this.statsManager = statsManager;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        long guildId = event.getGuild().getIdLong();
        long userId = event.getAuthor().getIdLong();
        String content = event.getMessage().getContentRaw();

        RaceResult result = raceManager.recordTyping(guildId, userId, content);

        if (result != null) {
            // Update stats in Redis
            statsManager.updateStats(userId, result.getEfficiency());

            event.getMessage().addReaction(net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode("✅")).queue();

            if (raceManager.isRoundFinished(guildId)) {
                if (raceManager.isFinalRound(guildId)) {
                    event.getChannel().sendMessage("🏁 **Race Finished!** Check /stats to see your progress.").queue();
                    raceManager.clearRace(guildId);
                } else {
                    event.getChannel().sendMessage("🔔 Round complete! Host, use `/begin` for the next round.").queue();
                }
            }
        }
    }
}