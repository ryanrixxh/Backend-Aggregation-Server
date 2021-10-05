##!/usr/bin/env bash
java ContentServer << EOF
$1
inputs/input_file$2.txt
localhost:4567
EOF
