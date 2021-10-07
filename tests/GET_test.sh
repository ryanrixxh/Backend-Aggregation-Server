##!/usr/bin/env bash
# This is a basic GET request tester.
# On the command line it takes a single input. The domain:portnumber
echo Starting GET Test ...
cd ..
java Client << EOF > outputs/GET_test_output.txt
$1
EOF
