package com.konker.konkersensors.conn.ErrorCollector;

/**
 * Created by erico on 28/10/2016.
 */

public class ErrorObject {
    public String message=null;
    public Integer errorCode=0;
    public ErrorObject(String message,  Integer errorCode){
        this.message=message;
        this.errorCode=errorCode;
    }

}
