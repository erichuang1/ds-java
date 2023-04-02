cd ./bin
while true
do
    timestamp=$(date +"%H:%M:%S")
    echo -e "======================================================="
    echo -e "$timestamp Starting a new instance of ds-server..."
    # ./ds-server -c S1testConfigs/ds-S1-config01--wk6.xml -i
    ./ds-server -c S1testConfigs/ds-S1-config02--wk6.xml -v brief -p 51200 -n
done
