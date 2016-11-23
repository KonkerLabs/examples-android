package com.konker.konkersensors.conn.ErrorCollector;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by erico on 18/10/2016.
 */

public class ErrorCollector {

    public static Set<ErrorObject> errorSet = Collections.synchronizedSet(new HashSet<ErrorObject>());

    public static void addError(String errorMessage){
        ErrorObject err =new ErrorObject(errorMessage, 0);

        errorSet.add(err);
    }

    public static void addError(String errorMessage,int errorCode){
        ErrorObject err =new ErrorObject(errorMessage, errorCode);
        errorSet.add(err);
    }


    public static boolean hasError(){
        return errorSet.size()!=0;
    }

    public static void clear(){
        errorSet.clear();
    }

    public static ErrorObject getFirstErrorMessage(){
        Iterator iter = errorSet.iterator();

        return (ErrorObject)iter.next();
    }


}
