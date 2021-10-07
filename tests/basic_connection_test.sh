##!/usr/bin/env bash
# This test relies on the Aggregation Server running prior. If it is not the code with display an error.
echo Starting Server ...
cd ..
echo Starting a single ContentServer ...
java ContentServer << EOF
basic
input_file.txt
localhost:4567
EOF
echo Starting Client ...
java Client << EOF
localhost:4567
EOF
