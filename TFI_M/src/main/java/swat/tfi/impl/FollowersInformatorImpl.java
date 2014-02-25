/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi.impl;

import swat.tfi.FollowersInformator;
import twitter4j.conf.ConfigurationBuilder;

/**
 * <b>Предназначение:</b><br/>
 *   <p></p>
 *
 * <br/><b>Описание:</b><br/>
 *   <p></p>
 * 
 * <br/>Создан Feb 25, 2014, 6:52:43 PM<br/>
 *
 * @author Sviatoslav Lobach (s.lobach@parkcode.com.ua)
 */

public class FollowersInformatorImpl implements FollowersInformator
{

    public FollowersInformatorImpl()
    {
        ConfigurationBuilder cb = new ConfigurationBuilder();
cb.setDebugEnabled(true)
  .setOAuthConsumerKey("*********************")
  .setOAuthConsumerSecret("******************************************")
  .setOAuthAccessToken("**************************************************")
  .setOAuthAccessTokenSecret("******************************************");
TwitterFactory tf = new TwitterFactory(cb.build());
Twitter twitter = tf.getInstance();
    }

}
