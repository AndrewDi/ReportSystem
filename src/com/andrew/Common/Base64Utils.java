package com.andrew.Common;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64Utils {

    public static String Encode(String context){
        BASE64Encoder encoder=new BASE64Encoder();
        try {
            return encoder.encode(context.getBytes("UTF-8"));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String Decode(String context){
        BASE64Decoder decoder=new BASE64Decoder();
        try {
            return new String(decoder.decodeBuffer(context), "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
