config="../S2TestScript/S2TestConfigs/config16-long-high.xml"
# config="ds-config01--wk9.xml"
# ./ds-server -c $config -i;exit
while true; do
    timestamp=$(date +"%H:%M:%S")
    echo -e "======================================================="
    echo -e "$timestamp Starting a new instance of ds-server..."
    # ./ds-server -c $config -v brief -p 50000 -n
    # ./ds-client -a bf

    ./ds-server -c $config -v brief -p 50000 -n > ff.log
    cat ff.log
    echo -e "\nPress Enter to restart..."
    read -r
done
