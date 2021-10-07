##!/usr/bin/env bash
# This is a basic put request tester.
# On the command line it takes two inputs, the first being the desired id
# and the second being the input file you want to parse and send
echo Starting PUT Test ...
cd ..
java ContentServer << EOF
$1
$2
localhost:4567
EOF
