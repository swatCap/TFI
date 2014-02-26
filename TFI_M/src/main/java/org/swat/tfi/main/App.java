package org.swat.tfi.main;

import swat.tfi.FollowersInformator;
import swat.tfi.impl.IncreaserImpl;
import twitter4j.IDs;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        FollowersInformator followersInformator = new IncreaserImpl();
        IDs followers = followersInformator.getMyFollowersIDs();
        IDs friends = followersInformator.getMyFriends();
        followers.getIDs();
    }
}
