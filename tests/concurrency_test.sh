##!/usr/bin/env bash-
# This tests PUT request arrivals that occur at exactly the same time.
echo Starting Concurrency Test ...
parallel -u ::: './PUT_test.sh 1 inputs/input_file1.txt' './PUT_test.sh 2 inputs/input_file2.txt' './PUT_test.sh 3 inputs/input_file3.txt' './PUT_test.sh 4 inputs/input_file4.txt' > ../outputs/concurrency_test_output.txt
wait
