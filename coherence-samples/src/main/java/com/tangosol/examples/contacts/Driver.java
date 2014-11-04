/*
* Driver.java
*
* Copyright (c) 2000, 2009, Oracle and/or its affiliates. All rights reserved.
*
* Oracle is a registered trademarks of Oracle Corporation and/or its
* affiliates.
*
* This software is the confidential and proprietary information of Oracle
* Corporation. You shall not disclose such confidential and proprietary
* information and shall use it only in accordance with the terms of the
* license agreement you entered into with Oracle.
*
* This notice may not be removed or altered.
*/

package com.tangosol.examples.contacts;


import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

import java.io.FileInputStream;
import java.io.IOException;


/**
* Driver executes all the contact examples. The driver will if not passed a
* cache name use the 'contacts' as as the default cache name. If
* passed a different name, make sure that the changes are reflected in the
* configuration files.
* <p/>
* Before the examples are run, the Driver will populate the cache with random
* contact data.
* <p/>
* Examples are invoked in this order <p/>
* 1) LoaderExample<br/>
* 2) QueryExample <br/>
* 3) ObserverExample <br/>
* 4) BasicExample<br/>
* 5) ProcessorExample<br/>
* @author dag  2009.03.02
*/
public class Driver
    {

    // ----- static methods -------------------------------------------------

    /**
     * Execute Contact examples.
     * <p/>
     * usage: [cache-name] [contacts file]
     *
     * @param asArg command line arguments
     */
    public static void main(String[] asArg)
            throws IOException
        {
        String sCache = asArg.length > 0 ? asArg[0] :
                LoaderExample.CACHENAME;
        String sFile = asArg.length > 1 ? asArg[1] :
                Driver.DEFAULT_DATAFILE;
        NamedCache cache = CacheFactory.getCache(sCache);

        System.out.println("------contacts examples begin------");
        // Load data into cache
        new LoaderExample().load(new FileInputStream(sFile), cache);

        // Run sample queries
        new QueryExample().query(cache);

        // Run sample change observer.
        ObserverExample observer = new ObserverExample();
        observer.observe(cache);

        // Run basic cache commands
        new BasicExample().execute(cache);

        // Run sample entry processor
        new ProcessorExample().execute(cache);

        // Stop observing
        observer.remove(cache);

        CacheFactory.shutdown();
        System.out.println("------contacts examples completed------");
        }

    public final static String DEFAULT_DATAFILE = "contacts.csv";
    }
