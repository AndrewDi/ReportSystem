package com.andrew.Common;

import com.alibaba.druid.util.StringUtils;
import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {

    /**
     * Convert Csv Output to List with String Array
     * @return
     */
    public static List<String[]> strToList(String str){
        List<String[]> arrays=new ArrayList<>();
        List<String> list = Splitter.on("\n").trimResults().splitToList(str);
        for (String data:list) {
            if(StringUtils.isEmpty(data))
                continue;
            String[] arrary=data.split("\\|");
            arrays.add(arrary);
        }
        return arrays;
    }
}
