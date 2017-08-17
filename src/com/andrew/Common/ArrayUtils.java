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

    public static List<Map<String,Object>> strToMapList(String str){
        List<Map<String,Object>> arrays=new ArrayList<>();
        List<String> list = Arrays.asList(str.split("\n"));
        for (String data:list) {
            if(StringUtils.isEmpty(data))
                continue;
            String[] arrary=data.split("\\|");
            Map<String,Object> map=new HashMap<>();
            map.put("NAME",arrary[0].trim());
            map.put("VALUE",arrary[1].trim());
            if(arrary.length==2){
                map.put("VALUE_FLAGS",null);
            }
            else {
                map.put("VALUE_FLAGS",arrary[2].trim());
            }

            arrays.add(map);
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
            xaxis[i] = data.values().toArray()[0];
            yaxis[i] = data.values().toArray()[1];
            i++;
        }
        jsonFormat.put("xaxis",xaxis);
        jsonFormat.put("yaxis",yaxis);
        return jsonFormat;
    }

}
