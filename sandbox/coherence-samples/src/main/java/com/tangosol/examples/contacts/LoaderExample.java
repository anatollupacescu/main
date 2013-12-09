/*
* LoaderExample.java
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


import com.tangosol.examples.model.Address;
import com.tangosol.examples.model.Contact;
import com.tangosol.examples.model.ContactId;
import com.tangosol.examples.model.PhoneNumber;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;


/**
* LoaderExample loads contacts into the cache from a file or stream.
* <p/>
* Demonstrates the most effective way of inserting data into a cache using the
* Map.putAll() method. This will allow for minimizing the number of network
* roundtrips between the application and the cache.
*
* @author dag  2009.02.20
*/
public class LoaderExample
    {
    // ----- static methods -------------------------------------------------

    /**
    * Load contacts from a CSV file, then populate the cache with the data.
    * <p/>
    * The first argument is the name of the datafile to load. The second
    * argument will be treated as the name of the cache to populate.
    * <p/>
    * usage: [file name] [cache name]
    *
    * @param asArg  command line arguments
    *
    * @throws IOException if file cannot be read
    */
    public static void main(String[] asArg)
            throws IOException
        {
        String sFile  = asArg.length > 0 ? asArg[0] : Driver.DEFAULT_DATAFILE;
        String sCache = asArg.length > 1 ? asArg[1] : CACHENAME;

        System.out.println("input file: " + sFile);
        System.out.println("cache name: " + sCache);

        new LoaderExample().load(new FileInputStream(sFile),
                CacheFactory.getCache(sCache));

        CacheFactory.shutdown();
        }

    /**
    * Load contacts from the inputstream and insert them into the cache.
    *
     * @param in     stream containing contacts
     * @param cache  target cache
    *
    * @throws IOException on read error
    */
    public void load(InputStream in, NamedCache cache)
            throws IOException
        {
        BufferedReader reader    = new BufferedReader(new InputStreamReader(in));
        Map            mapBatch  = new HashMap(BATCH_SIZE);
        int            cContacts = 0;
        Contact        contact;

        System.out.println("------LoaderExample begins------");
        while ((contact = readContact(reader)) != null)
            {
            mapBatch.put(new ContactId(contact.getFirstName(),
                    contact.getLastName()), contact);
            ++cContacts;

            // When reached the BATCH_SIZE threashold transfer the records to
            // the cache.
            if (cContacts % BATCH_SIZE == 0)
                {
                // minimize the network roundtrips by using putAll()
                cache.putAll(mapBatch);
                mapBatch.clear();
                System.out.print('.');
                System.out.flush();
                }
            }

        // insert the final batch
        if (!mapBatch.isEmpty())
            {
            cache.putAll(mapBatch);
            }

        System.out.println("Added " + cContacts + " entries to cache");
        System.out.println("------LoaderExample completed------");
        }

    /**
    * Read a single contact from the supplied stream.
    *
    * @param reader  the stream from which to read a contact
    *
    * @return the contact or null upon reaching end of stream
    *
    * @throws IOException  on read error
    */
    public Contact readContact(BufferedReader reader)
            throws IOException
        {
        String sRecord = reader.readLine();
        if (sRecord == null)
            {
            return null;
            }

        String[] asPart     = sRecord.split(",");
        int      ofPart     = 0;
        String   sFirstName = asPart[ofPart++];
        String   sLastName  = asPart[ofPart++];

        Date dtBirth = Date.valueOf(asPart[ofPart++]);

        Address  addrHome   = new Address(
                /*streetline1*/ asPart[ofPart++],
                /*streetline2*/ asPart[ofPart++],
                /*city*/        asPart[ofPart++],
                /*state*/       asPart[ofPart++],
                /*zip*/         asPart[ofPart++],
                /*country*/     asPart[ofPart++]);
        Address  addrWork   = new Address(
                /*streetline1*/ asPart[ofPart++],
                /*streetline2*/ asPart[ofPart++],
                /*city*/        asPart[ofPart++],
                /*state*/       asPart[ofPart++],
                /*zip*/         asPart[ofPart++],
                /*country*/     asPart[ofPart++]);
        Map      mapTelNum  = new HashMap();

        for (int c = asPart.length; ofPart < c; )
            {
            mapTelNum.put(/*type*/ asPart[ofPart++],
                    new PhoneNumber(
                    /*access code*/  Short.parseShort(asPart[ofPart++]),
                    /*country code*/ Short.parseShort(asPart[ofPart++]),
                    /*area code*/    Short.parseShort(asPart[ofPart++]),
                    /*local num*/    Long.parseLong(asPart[ofPart++])));
            }

        return  new Contact(sFirstName, sLastName, addrHome,
                addrWork, mapTelNum, dtBirth);
        }


    // ----- constants ------------------------------------------------------

    /**
    * Default cache name.
    */
    public static final String CACHENAME = "contacts";

    /**
    * The maximum number of contacts to load at a time.
    */
    private static final int BATCH_SIZE = 1024;
    }
