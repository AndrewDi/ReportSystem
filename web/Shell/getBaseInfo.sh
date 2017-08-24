#!/bin/sh

OSType="";
Major="";
Patch="";

memsize="";
swapsize="";
swapfree="";
hostname="";
cpusize="";

if [ -f /etc/SuSE-release ];then
   ##Get Full OSLevel
   OSType="SLES";
   Major=$(awk -F'=' '$1~/VERSION/{print $2}' /etc/SuSE-release);
   Patch=$(awk -F'=' '$1~/PATCHLEVEL/{gsub(/ /,"",$2);print "SP"$2}' /etc/SuSE-release);

   ##Get Memsize and Swapsize
   freem=$(free -m);
   memsize=$(echo "${freem}"|awk '$1~/Mem/{print $2}');
   swapsize=$(echo "${freem}"|awk '$1~/Swap/{print $2}');
   swapfree=$(echo "${freem}"|awk '$1~/Swap/{print $4}');
   cpusize=$(grep processor /proc/cpuinfo|wc -l);
elif [ -f /etc/redhat-release ];then
   ##Get Full OSLevel
   OSType="RHEL";
   Major=$(awk -F'=' '$1~/VERSION/{print $(NF-1),$NF}' /etc/redhat-release);

   ##Get Memsize and Swapsize
   freem=$(free -m);
   memsize=$(echo "${freem}"|awk '$1~/Mem/{print $2}');
   swapsize=$(echo "${freem}"|awk '$1~/Swap/{print $2}');
   swapfree=$(echo "${freem}"|awk '$1~/Swap/{print $4}');
   cpusize=$(grep processor /proc/cpuinfo|wc -l);
elif [ -f /usr/bin/oslevel ];then
   ##Get Full OSLevel
   OSType="AIX ";
   Major=$(oslevel -s);
fi

echo "oslevel|${OSType}${Major} ${Patch}";
echo "memsize|${memsize:-unknow}";
echo "swapsize|${swapsize:-unknow}";
echo "swapfree|${swapfree:-unknow}";
echo "hostname|$(hostname)";
echo "cpusize|${cpusize:-unknow}";