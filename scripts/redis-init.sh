#!/bin/bash
# redis-init.sh — put Redis into a known state for the typing race bot.
# Usage: ./scripts/redis-init.sh {reset|new-deployment|existing-dataset}

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

  existing-dataset)
    echo "Seeding Redis with a mid-life dataset..."
    $REDIS_CLI FLUSHALL
    $REDIS_CLI SET "stats:1433867623610847263" "99.75"
    echo "Done. Seeded stats entries."
    ;;

  *)
    echo "Unknown mode: $MODE"
    echo "Usage: $0 {reset|existing-dataset}"
    exit 1
    ;;
esac
