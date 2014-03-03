/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <b>Предназначение:</b><br/>
 *   <p></p>
 *
 * <br/><b>Описание:</b><br/>
 *   <p></p>
 * 
 * <br/>Создан Mar 3, 2014, 11:14:52 AM<br/>
 *
 * @author Sviatoslav Lobach (s.lobach@parkcode.com.ua)
 */

public class DateTimeUtils
{

    private final static String DEFAULT_DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";

    public static String dateToString(final Date date, final String format)
    {
        if (date != null)
        {
            if (!"".equals(format) && format != null)
            {
                return new SimpleDateFormat(format).format(date);
            }
            else
            {
                return new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(date);
            }
        }

        return null;
    }

    public static Date stringToDate(final String date, final String format)
    {
        try
        {
            if (date != null)
            {
                if (!"".equals(format) && format != null)
                {
                    return new SimpleDateFormat(format).parse(date);
                }
                else
                {
                    return new SimpleDateFormat(DEFAULT_DATE_FORMAT).parse(date);
                }
            }

            return null;
        }
        catch (ParseException ex)
        {
            return null;
        }
    }

    public static String dateToString(final Date dt)
    {
        if (dt != null)
        {
            return new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(dt);
        }
        return null;
    }

    public static Date stringToDate(final String s)
    {
        try
        {
            return s != null ? new SimpleDateFormat(DEFAULT_DATE_FORMAT).parse(s) : null;
        }
        catch (ParseException ex)
        {
            return null;
        }
    }

    public static int dateToInt(Date date)
    {
        if (date != null)
        {
            Long dateLongSecs = date.getTime() / 1000;
            return dateLongSecs.intValue();
        }
        
        return 0;
    }
}
