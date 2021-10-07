##!/usr/bin/env bash
# This is testing a purposely malformated XML input
# The content server should send nothing and the Atom server should send back a 204 and not store anything
# The content server should stop is excecution immediately (no heartbeat)
echo Starting PUT Test ...
cd ..
java ContentServer << EOF > outputs/badInput_test_output.txt
evil
inputs/bad_input.txt
localhost:4567
EOF
