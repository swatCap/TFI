/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi.exceptions;


/**
 * <b>Предназначение:</b><br/>
 *   <p></p>
 *
 * <br/><b>Описание:</b><br/>
 *   <p></p>
 * 
 * <br/>Создан Feb 28, 2014, 2:28:27 PM<br/>
 *
 * @author Sviatoslav Lobach (s.lobach@parkcode.com.ua)
 */

public class TFIException extends Exception
{

    public TFIException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TFIException(String message)
    {
        super(message);
    }

    public TFIException(Exception cause)
    {
        super(cause);
    }      
    

}
