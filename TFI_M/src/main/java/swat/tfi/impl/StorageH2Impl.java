/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.h2.jdbcx.JdbcConnectionPool;

import swat.tfi.Storage;
import swat.tfi.data.Twitterian;
import swat.tfi.exceptions.TFIException;

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

public class StorageH2Impl implements Storage
{
    private static Connection conn;
    private static JdbcConnectionPool cp;
    
    
    public StorageH2Impl()
    {
        createConnection();
    }   
    
    @Override
    public void destroy()
    {
        closeConnection();
    }

    @Override
    public List<Twitterian> getFavouriteFriends()
    {
        List<Twitterian> res = new ArrayList<Twitterian>();
        try
        {
            String sqlString = "SELECT FAVOURITE_ID, SCREEN_NAME, NAME FROM FAVOURITES ";
            
            PreparedStatement select = conn.prepareStatement(sqlString);
            ResultSet result = select.executeQuery();
            
            while (result.next()) 
            { 
                Twitterian t = new Twitterian();
                t.setId(result.getLong(1));
                t.setScreenName(result.getString(2));
                t.setName(result.getString(3));                            
                
                res.add(t);
            }            
        }
        catch (SQLException exception)
        {
            //TODO : :add logging 
            exception.printStackTrace();
            //throw new TFIException("Error while getting favourite twitterian from H2! ", exception);
        }
        
        return res;
    }

    @Override
    public void addToFavouriteFriends(Twitterian twitterian) throws TFIException
    {        
        if (twitterian != null && twitterian.isValid())
        {
            try 
            {
                String insertTableSQL = "INSERT INTO FAVOURITES "
                        + "(FAVOURITE_ID, SCREEN_NAME, NAME) VALUES "
                        + "(?,?,?)";
                
                PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
                preparedStatement.setLong(1, twitterian.getId());
                preparedStatement.setString(2, twitterian.getScreenName());
                preparedStatement.setString(3, twitterian.getName());

                preparedStatement.executeUpdate();
            }
            catch (SQLException exception)
            {
                throw new TFIException("Error while saving to H2! ", exception);
            }
        }
        else
        {
            throw new TFIException("Attempt to add to favourites user with null values!");
        }
    }
    
    private void createConnection()
    {
        cp = JdbcConnectionPool.create("jdbc:h2:tcp://localhost/~/tfiDB.h2.db", "TFI", "tfisecret");
        try
        {
            conn = cp.getConnection();
        }
        catch (SQLException e)
        {
            System.err.println("Caught IOException: " + e.getMessage());
        }  
    }
    
    private static void closeConnection()
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
