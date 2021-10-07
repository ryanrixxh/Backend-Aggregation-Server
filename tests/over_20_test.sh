##!/usr/bin/env bash-
parallel -u ::: './tester.sh 1 1' './tester.sh 2 2' './tester.sh 3 3' './tester.sh 4 4' './tester.sh 5 5' './tester.sh 6 6' './tester.sh 7 7' './tester.sh 8 8' './tester.sh 9 9' './tester.sh 10 10' './tester.sh 11 11' './tester.sh 12 12'
