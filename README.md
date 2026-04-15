
# Discord-bot-project-group-6

Here is your README.md formatted with proper Markdown syntax so it looks professional on GitHub.I have fixed the broken code blocks, organized the tables, and corrected the "shellCopy code" errors that often happen during copy-pasting.🏁 Typing Race Discord BotA fast, multiplayer typing-race bot built with Java + JDA + Redis, featuring round-based gameplay, real-time scoring, and automatic race progression.🚀 Features🎮 Multiplayer Typing RacesUnlimited players: Join the race at any time before it starts.Host-managed: The person who starts the race controls when the countdown begins.5-Round Cycle: Configurable rounds that automatically progress.Real-time Results: Clean, round-by-round scoreboards.📊 Smart Accuracy & ScoringWord Detection: Case-insensitive and punctuation-normalized comparison.Efficiency Formula:$$efficiency = \frac{correctWords}{seconds}$$Final Standings: Total leaderboard calculated after the final round.🛠️ Project ArchitecturePlaintextsrc/main/java/typingracebot
│
├── application/         # Core logic (RaceManager, StatsManager)
├── delivery/
│   ├── discord/         # JDA Commands & Listeners
│   └── redis/           # Data persistence (Repositories)
└── model/               # Data objects (Race, Player, Round)
🧩 CommandsCommandDescription/start_raceHost creates a new race session/joinA player joins the active race/beginHost triggers the Round 1 countdown(auto)Bot listens for typing messages automatically(auto)Automatic countdown for subsequent rounds⚙️ RequirementsSoftwareJava 17+MavenRedis (Must be running on port 6379)Discord Bot SetupCreate a bot at the Discord Developer Portal.Enable Gateway Intents:✅ MESSAGE CONTENT✅ GUILD MEMBERS✅ GUILD MESSAGESCopy your Bot Token.📦 Installation1️⃣ Clone and NavigateBashgit clone https://github.com/cs220s26/Discord-bot-project-group-6.git
cd Discord-bot-project-group-6
2️⃣ Set Environment VariableThe bot looks for a system variable named DISCORD_TOKEN.Bash# Mac/Linux
export DISCORD_TOKEN="YOUR_BOT_TOKEN_HERE"

# Windows (PowerShell)
$Env:DISCORD_TOKEN="YOUR_BOT_TOKEN_HERE"
3️⃣ Build and RunBash# Start Redis first
redis-server

# Build and execute
mvn clean package
java -jar target/typing-race-bot.jar
🧠 How Scoring WorksThe bot compares the user's input to the target text word-by-word. It is case-insensitive and ignores punctuation to ensure fairness.Example Comparison:Target: "The quick brown fox."Input: "the quick brown fox"Result: 4/4 Correct.Efficiency Calculation:If you get 8 words correct in 5.30 seconds:$$8 / 5.3 = 1.51 \text{ (Score)}$$🛡️ Error HandlingThe bot includes built-in protections for:Race State: Prevents starting multiple races in one channel.Player Validation: Prevents duplicate joins or starting with 0 players.Permissions: Only the host (the person who ran /start_race) can use /begin.Database: Clean error messages if Redis is unavailable.🧪 TestingRun the test suite to verify word comparison and race lifecycles:Bashmvn test
🤝 ContributingOpen a Pull Request.Ensure mvn clean package passes locally.Write meaningful commit messages.


testing 
