#!/usr/bin/sh

# Run the project
# Limit the run time to given number of seconds
ulimit -v "$2"
timeout "$1" /uer/bin/time -v java Main < input.txt > output.txt

# Check if the run was successful
echo $? > exit_code.txt