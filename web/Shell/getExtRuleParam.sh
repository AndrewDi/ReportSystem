#!/bin/ksh

sysctl vm.swappiness|awk -F'=' '{gsub("\\.","_",$1);printf("%s|%s|\n",$1,$2) }'