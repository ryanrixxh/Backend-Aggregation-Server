##!/usr/bin/env bash
java ContentServer << EOF
$1
input_file.txt
localhost:4567
EOF
