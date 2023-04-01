cd ./bin
while true
do
    timestamp=$(date +"%H:%M:%S")
    echo -e "@@@@@@@@@@@@@@@@@@ $timestamp Restarting... @@@@@@@@@@@@@@@@@@"
    # ./ds-server -c S1testConfigs/ds-S1-config04--wk6.xml -i
    ./ds-server -c S1testConfigs/ds-S1-config08--wk6.xml -v brief -p 51200 -n
done
