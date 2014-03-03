package org.swat.tfi.main;

import java.io.File;
import java.util.List;

import swat.tfi.IncreaseJob;
import swat.tfi.Increaser;
import swat.tfi.data.Twitterian;
import swat.tfi.exceptions.TFIException;
import swat.tfi.impl.IncreaseJobImpl;
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
        IncreaseJob increaseJob = new IncreaseJobImpl(true);
        
//        try
//        {
//            increaser.addToFavouriteFriends(new Twitterian("pusstwi"));
//            increaser.addToFavouriteFriends(new Twitterian("FrazaDnya"));
            increaseJob.follow(50, true, true, false);            
            
//        }
//        catch (TFIException exception)
//        {
//            exception.printStackTrace();
//        }
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
