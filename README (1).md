# 🏁 Typing Race Discord Bot
A fast, multiplayer typing-race bot built with **Java + JDA + Redis**, featuring round-based gameplay, real-time scoring, and automatic race progression.

---

## 🚀 Features

### 🎮 Multiplayer Typing Races
- Unlimited players
- Host-managed race sessions
- 5-round races (configurable)
- Automatically progresses to next rounds
- Clean round-by-round results

### 📊 Smart Accuracy & Scoring
- Correct-word detection with punctuation normalization
- Efficiency formula:  
  **efficiency = correctWords / seconds**
- Total leaderboard after final round

### 🔄 Automatic Next-Round Countdown
After all players submit:
3...
2...
1...
GO!

shell
Copy code

### 🛠️ Clean Layered Architecture
src/main/java/typingracebot
│
├── application
│ ├── RaceManager
│ ├── StatsManager
│ └── TextProvider
│
├── delivery
│ ├── discord
│ │ ├── DiscordBot
│ │ ├── StartRaceCommand
│ │ ├── JoinCommand
│ │ ├── BeginCommand
│ │ ├── StatsCommand
│ │ └── TypeResultListener
│ │
│ └── redis
│ ├── RaceRepository
│ ├── RedisRaceRepository
│ ├── RedisStatsRepository
│ └── StatsRepository
│
└── model
├── Race
├── Round
├── RaceResult
└── PlayerStats

yaml
Copy code

---

## 🧩 Commands

| Command | Description |
|--------|-------------|
| `/start_race` | Host creates a new race |
| `/join` | A player joins the active race |
| `/begin` | Host starts Round 1 |
| *(auto)* | Bot handles typing messages |
| *(auto)* | Countdown + next round |
| *(auto)* | Final leaderboard |

---

## ⚙️ Requirements

### Software
- Java 17+
- Maven
- Redis (local or remote)

### Discord Bot Setup
1. Create a bot → https://discord.com/developers/applications
2. Enable intents:
    - ✔ MESSAGE CONTENT
    - ✔ GUILD MEMBERS
    - ✔ GUILD MESSAGES
3. Invite the bot using an OAuth2 URL with bot permissions.

---

## 📦 Installation

### 1️⃣ Clone the project
```sh
git clone https://github.com/yourname/typing-race-bot.git
cd typing-race-bot
2️⃣ Set Environment Variable
Add to .zshrc, .bashrc, or system env:

sh
Copy code
export DISCORD_TOKEN="YOUR_BOT_TOKEN"
Reload terminal:

sh
Copy code
source ~/.zshrc
3️⃣ Start Redis
sh
Copy code
redis-server
4️⃣ Build & Run
sh
Copy code
mvn clean package
java -jar target/typingracebot.jar
🧠 How Scoring Works
Correct Words
Case-insensitive

Punctuation removed

Compares each word in order

Stops at shortest length (prevents cheating)

Efficiency Formula
ini
Copy code
efficiency = correctWords / seconds
Example:

Correct	Time	Score
8	5.30s	1.51
8	8.00s	1.00

Faster AND more accurate → higher rank.

🧪 Testing
Run all tests:

sh
Copy code
mvn test
Covers:

Word comparison

Round lifecycle

Race creation & joining

Duplicate submissions

Final scoring

🛡️ Error Handling
Bot protects against:

Starting a race when one is active

Beginning before any user joins

Duplicate submissions

Non-host trying to start rounds

Missing Discord token

Redis unavailable

All errors produce clean Discord messages.

📈 Future Improvements
Web dashboard

User profiles + badges

Custom text packs per server

Timed rounds

Ranking system

🤝 Contributing
Pull Requests welcome!

Please ensure:

Clean formatting

Document methods

Tests pass

Meaningful commit messages
