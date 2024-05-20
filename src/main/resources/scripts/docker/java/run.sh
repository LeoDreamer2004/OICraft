#!/usr/bin/bash

# Run the project
# Limit the run time to given number of seconds
# shellcheck disable=SC2039
ulimit -v "$2"
START=$(date +%s.%N)
timeout "$1s" /usr/bin/time -f "Memory Usage: %M" java Main < input.txt > output.txt
echo "Exit code: $?"
END=$(date +%s.%N)

# Calculate the execution time
ELAPSED=$(echo "$END - $START" | bc)
echo "Used time: $ELAPSED"