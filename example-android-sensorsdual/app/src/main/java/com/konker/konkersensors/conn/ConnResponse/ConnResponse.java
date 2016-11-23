package com.konker.konkersensors.conn.ConnResponse;

/**
 * Created by erico on 31/10/2016.
 */

public class ConnResponse {
    //public static Set<String> responseSet = Collections.synchronizedSet(new HashSet<String>());
    public static String lastResponse=null;
    public static void addResponse(String responseMessage){
       lastResponse=responseMessage;
    }


    public static void clear(){
        lastResponse=null;
    }


}
