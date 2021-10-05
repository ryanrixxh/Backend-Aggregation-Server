echo Starting Server ...
java AtomServer
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
