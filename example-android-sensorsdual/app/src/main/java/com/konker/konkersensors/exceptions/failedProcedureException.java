package com.konker.konkersensors.exceptions;


//cutomized exception
public class failedProcedureException extends Exception
{
    //Parameterless Constructor
    public failedProcedureException() {}
    public int errorCode=0;
    //Constructor that accepts a message
    public failedProcedureException(String message)
    {
        super(message);
    }

    //Constructor that accepts a message
    public failedProcedureException(String message, int errorCode)
    {   super(message);
        this.errorCode=errorCode;
    }
}

