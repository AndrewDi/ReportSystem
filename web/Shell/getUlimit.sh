#!/bin/sh
instname=$(ps -ef|grep -iE "db2sysc[ ]*0"|awk '{print $1}');
hard=$(ulimit -u ${instname} -Ha|awk -F"[()]" '{print $1,$3}'  OFS='|');
soft=$(ulimit -u ${instname} -Sa);
echo "${hard}"|while read line
do
  name=$(echo "${line}"|awk -F"|" '{print $1}');
  hard_limit=$(echo "${line}"|awk -F"|" '{print $2}');
  soft_limit=$(echo "${soft}"|awk -F"[()]" -v name="${name}" '$1==name{print $3}');
  echo "${name}|${soft_limit}|${hard_limit}";
done