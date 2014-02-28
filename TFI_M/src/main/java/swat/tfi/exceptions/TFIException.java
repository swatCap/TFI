/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi.exceptions;

import twitter4j.TwitterException;
import twitter4j.internal.http.HttpResponse;

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

public class TFIException extends TwitterException
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

    public TFIException(String message, HttpResponse res)
    {
        super(message, res);
    }

    public TFIException(String message, Exception cause, int statusCode)
    {
        super(message, cause, statusCode);
    }

    

}
