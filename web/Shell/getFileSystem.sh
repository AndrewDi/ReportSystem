#!/bin/sh
df -Pm|awk '$1!="Filesystem"{print $1,$2,$3,$4,$5,$6}' OFS='|'