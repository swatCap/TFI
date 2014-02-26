/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;

/**
 * <b>Предназначение:</b><br/>
 *   <p></p>
 *
 * <br/><b>Описание:</b><br/>
 *   <p></p>
 * 
 * <br/>Создан Feb 26, 2014, 10:48:11 AM<br/>
 *
 * @author Sviatoslav Lobach (s.lobach@parkcode.com.ua)
 */

public class DatabaseOperator
{
    private static Connection conn;
    private static JdbcConnectionPool cp;
    
    
    public DatabaseOperator()
    {
        createConnection();
    }

    private void createConnection()
    {
        cp = JdbcConnectionPool.create("jdbc:h2:tcp://localhost/~/tfiDB", "tfi", "secret");
        try
        {
            conn = cp.getConnection();
        }
        catch (SQLException e)
        {
            System.err.println("Caught IOException: " + e.getMessage());
        }  
    }
    
    public static void closeConnection()
    {
        try
        {
            conn.close();
        }
        catch (SQLException e)
        {
            System.err.println("Caught IOException: " + e.getMessage());
        }
        
        cp.dispose();
    }
}
