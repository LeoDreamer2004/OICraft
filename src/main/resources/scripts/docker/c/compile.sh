#!/usr/bin/sh

# Compile the project
# Limit the compile time to 5 seconds
timeout 5s gcc -O2 -o main Main.c
