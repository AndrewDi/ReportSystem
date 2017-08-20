#!/bin/ksh

/usr/sbin/sysctl -a|awk -F'=' '{gsub("\\|","",$2);printf("%s|%s|\n",$1,$2) }' 2>/dev/null