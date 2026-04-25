# Discord-bot-project-group-6

## Overview

This project is a **multiplayer Typing Race Discord bot** written in Java (JDA) with Redis as its storage layer, deployed to AWS EC2 using a full DevOps toolchain. Players join a race, type a target passage, and the bot scores them on correctness and speed across multiple rounds before posting a final leaderboard.

The purpose of the project is to demonstrate the DevOps practices from CSCI 220: source control on GitHub, secrets management through AWS Secrets Manager, build and static analysis with Maven and Checkstyle, packaging into a single deployable jar, automated deployment onto an EC2 instance managed by SystemD, and CI/CD through GitHub Actions.

**System diagram:** _to be added — will show the flow from Discord → EC2 (bot process under SystemD) → Redis, plus the GitHub Actions CI/CD pipeline and AWS Secrets Manager credential path._

## Features

**Multiplayer typing races**
- Unlimited players per race
- Host-managed sessions with 5 configurable rounds
- Automatic round progression with clean round-by-round results

**Smart accuracy and scoring**
- Correct-word detection with punctuation normalization
- Efficiency formula: `correctWords / seconds`
- Total leaderboard posted after the final round

**Automatic next-round countdown** — when all players submit, the bot counts down `3... 2... 1... GO!` before starting the next round.

---

## Dev Setup/Execution

This section covers running the bot on a local development laptop.

### Prerequisites

- **Java 21** (Amazon Corretto 21 recommended to match production)
- **Maven 3.9+**
- **Redis** installed and running locally (`redis-server`)
- **Git**
- An **AWS Learner Lab** account with access to Secrets Manager
- A **Discord bot application** with a token — intents enabled: `MESSAGE CONTENT`, `GUILD MEMBERS`, `GUILD MESSAGES`

### 1. Clone the repo

```sh
git clone https://github.com/cs220s26/Discord-bot-project-group-6.git
cd Discord-bot-project-group-6
```

### 2. Store your Discord token in AWS Secrets Manager

The bot does **not** read the token from a `.env` file. It loads it from AWS Secrets Manager at startup. Only do this step once per project setup.

1. Open the AWS Console → Secrets Manager → "Store a new secret"
2. Choose "Other type of secret"
3. Key: `DISCORD_TOKEN`, Value: `<your bot token>`
4. Secret name: `220_Discord_Token`
5. Accept defaults through the rest of the wizard and save

### 3. Configure local AWS credentials

Learner Lab credentials are temporary and must be refreshed every session.

```sh
mkdir -p ~/.aws
```

Launch the Learner Lab → click **"AWS Details"** → copy the text under **"AWS CLI"** → paste it into `~/.aws/credentials`. The file should look like:

```
[default]
aws_access_key_id=...
aws_secret_access_key=...
aws_session_token=...
```

### 4. Start Redis

```sh
redis-server
```

In a second terminal, seed the database with a starting state:

```sh
./scripts/redis-init.sh new-deployment    # fresh empty state
# or
./scripts/redis-init.sh existing-dataset  # load seed data
```

### 5. Build and run

```sh
mvn clean package
java -jar target/discord-typing-race-1.0-SNAPSHOT-jar-with-dependencies.jar
```

If everything is wired correctly, the bot will come online in your Discord server and respond to `/start_race`, `/join`, `/begin`, and `/stats`.

---

## Prod Setup/Execution

This section covers running the bot on an EC2 instance for the first time. Automated redeploys are covered in the CI/CD sections below.

### 1. Launch the EC2 instance

- **AMI:** Amazon Linux 2023
- **Instance type:** `t2.micro` (free tier eligible)
- **Security group:** allow inbound SSH (port 22) from your IP; no other inbound ports are required — the bot talks to Discord outbound only
- **Advanced details → IAM instance profile:** select **`LabInstanceProfile`**. This attaches the `LabRole` so the instance can read from Secrets Manager without storing any credentials file.

### Easy way
### 2. Copy the Bash file onto the ec2 instance

First ssh into the instance and create the ec2_init.sh file

```sh
nano ec2_init.sh
```

Now copy the data within the ec2_init.sh from within this repo and paste it into the file you made on the ec2 instance

### 3. Running the file

In the terminal run these two lines

```sh
chmod +x ec2_init.sh
sudo ./ec2_init.sh
```

Once you run those two commands a wall of text should appear, wait till it stops and it shows you the status of the service file.
Now you can just press 'q' to exit the viewer and the discord bot should be working.

---

### Manual way
### 2. Install Java, Maven, Git, and Redis

SSH into the instance and run:

```sh
sudo yum install -y maven-amazon-corretto21 git redis6
```

Installing `maven-amazon-corretto21` pulls in the matching JDK, ensuring only **one** version of Java is on the box.

### 3. Configure and start Redis

```sh
sudo systemctl enable redis6
sudo systemctl start redis6
```

Then seed the database using the deployment script:

```sh
cd ~/Discord-bot-project-group-6
./scripts/redis-init.sh new-deployment
```

### 4. Clone the repo and build

```sh
cd ~
git clone https://github.com/cs220s26/Discord-bot-project-group-6.git
cd Discord-bot-project-group-6
mvn package
```

The `LabRole` attached in step 1 grants Secrets Manager access — no `~/.aws/credentials` file is needed on the EC2 instance.

### 5. Install the SystemD unit

Copy the provided unit file into place and enable it:

```sh
sudo cp deploy/typingracebot.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable typingracebot
sudo systemctl start typingracebot
```

Check status and logs:

```sh
sudo systemctl status typingracebot
sudo journalctl -u typingracebot -f
```

The bot now starts automatically on reboot and restarts on failure.

---

## CI/CD Setup

All automation lives in the `.github/` folder. Configuration happens in two places: workflow files in `.github/workflows/` and secrets in the repo's GitHub Settings.

### Workflow files

- **`.github/workflows/ci.yml`** — runs on every push and pull request to `main`. Executes Checkstyle and the JUnit test suite.
- **`.github/workflows/cd.yml`** — triggered manually via `workflow_dispatch`. SSHs into the EC2 instance, pulls the latest code, rebuilds the jar, and restarts the SystemD service.

### GitHub Secrets

Navigate to **Settings → Secrets and variables → Actions** in the repo and add the following repository secrets:

| Secret name | Purpose |
|---|---|
| `EC2_HOST` | Public DNS or IP of the EC2 instance |
| `EC2_USER` | SSH username (`ec2-user` for Amazon Linux) |
| `EC2_SSH_KEY` | Private key (contents of the `.pem` file) used to SSH into the instance |

The CI workflow does **not** need AWS credentials because it only runs tests and Checkstyle — it never touches AWS. The CD workflow only needs SSH access; once it's on the box, the `LabRole` handles AWS permissions.

### EC2-side preparation

- The EC2 instance must already be launched with `LabInstanceProfile` attached (see Prod Setup).
- The repo must already be cloned to `~/Discord-bot-project-group-6` on the instance.
- The SystemD unit must already be installed and enabled.

CD does not bootstrap a fresh server — it only redeploys onto a server that has been through the Prod Setup steps once.

---

## CI/CD Execution

### CI — triggers and behavior

**Trigger:** any `git push` to any branch, or any pull request targeting `main`.

**Steps:**
1. Check out the repo
2. Set up JDK 21 (Corretto)
3. Cache the local Maven repository
4. Run `mvn checkstyle:check` — static analysis fails the build on any violation
5. Run `mvn test` — JUnit unit tests fail the build on any failure

If either step fails, the commit or PR is marked red on GitHub. A green check means the code compiles, passes style rules, and all tests pass.

### CD — triggers and behavior

**Trigger:** manual, via the **"Run workflow"** button in the Actions tab (`workflow_dispatch`). This is intentional — redeploys happen on demand, not on every push.

**Steps:**
1. Check out the repo
2. Set up the SSH key from `EC2_SSH_KEY`
3. SSH into `EC2_HOST` as `EC2_USER`
4. On the instance:
   - `cd ~/Discord-bot-project-group-6`
   - `git pull`
   - `mvn package`
   - `sudo systemctl restart typingracebot`
5. Tail a few lines of `journalctl -u typingracebot` to confirm the service came back up

After a successful CD run, the bot is running the latest code from `main` on EC2.

---

## Technologies Used

- [Java 21 (Amazon Corretto)](https://aws.amazon.com/corretto/)
- [Maven](https://maven.apache.org/)
- [Checkstyle](https://checkstyle.sourceforge.io/)
- [JUnit](https://junit.org/)
- [Redis](https://redis.io/) / [Jedis client](https://github.com/redis/jedis)
- [JDA (Java Discord API)](https://github.com/discord-jda/JDA)
- [AWS EC2](https://aws.amazon.com/ec2/)
- [AWS Secrets Manager](https://aws.amazon.com/secrets-manager/)
- [AWS SDK for Java v2](https://aws.amazon.com/sdk-for-java/)
- [Gson](https://github.com/google/gson)
- [GitHub Actions](https://github.com/features/actions)
- [SystemD](https://systemd.io/)
- [Amazon Linux 2023](https://aws.amazon.com/linux/amazon-linux-2023/)

---

## Background

Sources consulted while building this project:

- [AWS Docs: Set up an Apache Maven project](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup-project-maven.html)
- [AWS Docs: Get a Secrets Manager secret value using the Java AWS SDK](https://docs.aws.amazon.com/secretsmanager/latest/userguide/retrieving-secrets_java.html)
- [AWS Docs: Amazon Corretto](https://docs.aws.amazon.com/corretto/)
- [JDA Getting Started Wiki](https://jda.wiki/introduction/jda/)
- [Discord Developer Portal — Bot setup](https://discord.com/developers/applications)
- [Red Hat — Creating and running SystemD unit files](https://www.redhat.com/sysadmin/create-systemd-unit-file)
- [GitHub Docs — Using secrets in GitHub Actions](https://docs.github.com/en/actions/security-guides/using-secrets-in-github-actions)
- [GitHub Docs — Workflow dispatch events](https://docs.github.com/en/actions/using-workflows/events-that-trigger-workflows#workflow_dispatch)

