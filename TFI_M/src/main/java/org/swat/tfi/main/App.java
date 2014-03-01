package org.swat.tfi.main;

import java.io.File;
import java.util.List;

import swat.tfi.Increaser;
import swat.tfi.data.Twitterian;
import swat.tfi.exceptions.TFIException;
import swat.tfi.impl.IncreaserImpl;
import swat.tfi.utils.TwitteriansReadUtils;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Increaser increaser = new IncreaserImpl();
        try
        {
            increaser.follow(15, true, true, false);
        }
        catch (TFIException exception)
        {
            exception.printStackTrace();
        }
//        increaser.unfollowAllWhoDoesntFollowMeExceptFavourites();
        
//        List<Twitterian> favourites = increaser.getFavouriteFriends();
//        
//        if (favourites != null && !favourites.isEmpty())
//        {
//            for (Twitterian twitterian : favourites)
//            {
//                System.out.println(twitterian.getScreenName() + " ");
//            }
//        }
        
//        File f = new File("twitterians.json");
                
//        List<Twitterian> favourites = TwitteriansReadUtils.readFavouritesFromFile(f);
//        
//        List<Twitterian> failed = increaser.addToFavouriteFriends(favourites);        
//        
//        if (failed != null && !failed.isEmpty())
//        {
//            for (Twitterian twitterian : failed)
//            {
//                System.out.println(twitterian.getScreenName() + " ");
//            }
//        }
    }
}
