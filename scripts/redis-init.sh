#!/bin/bash
# redis-init.sh — put Redis into a known state for the typing race bot.
# Usage: ./scripts/redis-init.sh {reset|existing-dataset}
# I recomend changing 702535501130891285 within "stats:702535501130891285" "99.75" to your string (You can find your string after playing a round in the typing race bot then checking:
# redis6-cli 
# keys
# The one that says "stats:" is the user stats while "race:" is each race's stats you can also compare your efficiency score in discord then compare each "stats:" efficiency score to
# find yours.

set -e

# Use redis6-cli on Amazon Linux, redis-cli everywhere else
if command -v redis6-cli > /dev/null 2>&1; then
  REDIS_CLI="redis6-cli"
else
  REDIS_CLI="redis-cli"
fi

MODE="${1:-}"

if [ -z "$MODE" ]; then
  echo "Usage: $0 {reset|existing-dataset}"
  exit 1
fi

if ! $REDIS_CLI ping > /dev/null 2>&1; then
  echo "Error: can't reach Redis. Is redis-server running?"
  exit 1
fi

case "$MODE" in
  reset)
    echo "Resetting Redis..."
    $REDIS_CLI FLUSHALL
    echo "Done. Redis is empty."
    ;;
# changes a user(s) efficiency score listed in redis although it is targeted so you must specify who's score you're changing.
# you could also make a race key we don't do that here since it can only be seen in redis. 
  existing-dataset)
    echo "Seeding Redis with a mid-life dataset..."
    $REDIS_CLI FLUSHALL
    $REDIS_CLI SET "stats:702535501130891285" "99.75"
    echo "Done. Seeded stats entries."
    ;;

  *)
    echo "Unknown mode: $MODE"
    echo "Usage: $0 {reset|existing-dataset}"
    exit 1
    ;;
esac
