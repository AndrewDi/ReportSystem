package com.andrew.Common;

import com.alibaba.druid.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ArrayUtils {

    /**
     * Convert Csv Output to List with String Array
     * @return
     */
    public static List<String[]> strToList(String str){
        List<String[]> arrays=new ArrayList<>();
        List<String> list = Arrays.asList(str.split("\n"));
        for (String data:list) {
            if(StringUtils.isEmpty(data))
                continue;
            String[] arrary=data.split("\\|");
            arrays.add(arrary);
        }
        return arrays;
    }

    /**
     * Convert Spring MVC Result Format to ECharts Format
     * @param datas
     * @return
     */
    public static Map<String,Object[]> listToMap(List<Map<String,Object>> datas){
        Map<String,Object[]> jsonFormat = new HashMap<>();
        Object[] xaxis= new Object[datas.size()];
        Object[] yaxis=new Object[datas.size()];
        int i=0;

        for(Map<String,Object> data:datas)
        {
            LocalDateTime localDateTime=LocalDateTime.parse(data.values().toArray()[0].toString().substring(0,19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            xaxis[i] = localDateTime.toLocalDate().toString()+" "+localDateTime.toLocalTime().toString();
            yaxis[i] = data.values().toArray()[1];
            i++;
        }
        jsonFormat.put("xaxis",xaxis);
        jsonFormat.put("yaxis",yaxis);
        return jsonFormat;
    }
}
