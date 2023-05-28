while true; do
    timestamp=$(date +"%H:%M:%S")
    echo -e "======================================================="
    echo -e "$timestamp Starting a new instance of ds-server..."
    ./ds-server -c ds-config01--wk9.xml -v brief -p 50000 -n
    # ./ds-server -c ds-config01--wk9.xml -i
    # ./ds-client -a bf

    # ./ds-server -c ds-config01--wk9.xml -v brief -p 50000 -n > ff.log
    # cat ff.log
    # echo -e "\nPress Enter to restart..."
    # read -r
done
