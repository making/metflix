#!/bin/sh

. scripts/common.sh

echo "Create missing services"

current_services=`cf services`
for s in $service;do
    check=`echo $current_services | grep $s`
    case $s in
    "config-server" )
        name=p-config-server
        conf="-c '{\"git\":{\"uri\":\"$git_url\", \"label\":\"$git_label\"}}'"
        ;;
    "eureka-server" )
        name=p-service-registry
        conf=
        ;;
    "hystrix-dashboard" )
        name=p-circuit-breaker-dashboard
        conf=
        ;;
    esac
    if [ "$check" == "" ];then
        echo "++++ Create $s ++++"
        eval "cf create-service $name standard $s $conf"
    fi
done

cf services

echo "Deploy applications"

for d in $dir;do
    app=$d-$suffix
    echo "++++ Deploy $app ++++"
    pushd $d
        cf push $app
    popd
done

cf apps