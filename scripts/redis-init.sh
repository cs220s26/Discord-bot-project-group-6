#!/bin/bash
# redis-init.sh — put Redis into a known state for the typing race bot.
# Usage: ./scripts/redis-init.sh {new-deployment|existing-dataset}

set -e

MODE="${1:-}"

if [ -z "$MODE" ]; then
  echo "Usage: $0 {new-deployment|existing-dataset}"
  exit 1
fi

if ! redis-cli ping > /dev/null 2>&1; then
  echo "Error: can't reach Redis. Is redis-server running?"
  exit 1
fi

case "$MODE" in
  new-deployment)
    echo "Wiping Redis for a fresh deployment..."
    redis-cli FLUSHALL
    echo "Done. Redis is empty."
    ;;

  existing-dataset)
    echo "Seeding Redis with a mid-life dataset..."
    redis-cli FLUSHALL

    # StatsManager stores cumulative efficiency as a float: key "stats:{userId}"
    # (see src/main/java/typingracebot/application/StatsManager.java)
    redis-cli SET "stats:111111111111111111" "42.75"
    redis-cli SET "stats:222222222222222222" "18.50"
    redis-cli SET "stats:333333333333333333" "67.20"

    # Active races (key "race:{guildId}") are transient and managed
    # by the bot at runtime, a fresh server should have no active races.

    echo "Done. Seeded 3 player stats entries."
    ;;

  *)
    echo "Unknown mode: $MODE"
    exit 1
    ;;
esac
