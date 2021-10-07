##!/usr/bin/env bash
# This test relies on the Aggregation Server running prior. If it is not the code with display an error.
echo Starting Basic Connection Test ...
cd ..
echo Starting a single ContentServer ...
java ContentServer << EOF & > outputs/basic_connection_output.txt
basic
input_file.txt
localhost:4567
EOF
sleep 2
kill -s 2 9
echo Starting Client ...
java Client << EOF > outputs/basic_connection_output.txt
localhost:4567
EOF
