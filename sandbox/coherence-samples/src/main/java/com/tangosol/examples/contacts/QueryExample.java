/*
* QueryExample.java
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


import com.tangosol.net.NamedCache;

import com.tangosol.util.aggregator.Count;
import com.tangosol.util.aggregator.DoubleAverage;
import com.tangosol.util.aggregator.LongMax;
import com.tangosol.util.aggregator.LongMin;

import com.tangosol.util.extractor.ChainedExtractor;
import com.tangosol.util.extractor.KeyExtractor;
import com.tangosol.util.extractor.ReflectionExtractor;

import com.tangosol.util.filter.AlwaysFilter;
import com.tangosol.util.filter.AndFilter;
import com.tangosol.util.filter.EqualsFilter;
import com.tangosol.util.filter.GreaterFilter;
import com.tangosol.util.filter.LikeFilter;
import com.tangosol.util.filter.NotEqualsFilter;

import java.util.Iterator;
import java.util.Set;


/**
* QueryExample runs sample queries for contacts.
* <p/>
* The purpose of this example is to show how to create Extractors on cache
* data and how to create a KeyExtractor for the cache keys.
* <p/>
* It also illustrates how to use the indexes to filter the dataset in order to
* efficiently create a matching set.
* <p/>
* Finally the example demonstrates how to use some of the built-in cache
* aggregators to do simple computational tasks on the cache data.
*
* @author dag 2009.02.23
*/
public class QueryExample
    {
    // ----- QueryExample methods ---------------------------------------

    /**
    * Create indexes in the cache and query it for data.
    *
    * @param cache cache to query
    */
    public void query(NamedCache cache)
        {
        System.out.println("------QueryExample begins------");
        // Add indexes to make queries more efficient
        // Ordered index applied to fields used in range and like filter queries
        cache.addIndex(new ReflectionExtractor("getAge"), /*fOrdered*/ true,
                /*comparator*/ null);
        cache.addIndex(new KeyExtractor(new ReflectionExtractor("getLastName")),
                /*fOrdered*/ true, /*comparator*/ null);
        cache.addIndex(new ChainedExtractor("getHomeAddress.getCity"),
                /*fOrdered*/ true, /*comparator*/ null);
        cache.addIndex(new ChainedExtractor("getHomeAddress.getState"),
                /*fOrdered*/ false, /*comparator*/ null);
        cache.addIndex(new ChainedExtractor("getWorkAddress.getState"),
                /*fOrdered*/ false, /*comparator*/ null);

        // Find all contacts who live in Massachusetts
        Set setResults = cache.entrySet(new EqualsFilter(
                "getHomeAddress.getState", "MA"));
        printResults("MA Residents", setResults);

        // Find all contacts who live in Massachusetts and work elsewhere
        setResults = cache.entrySet(new AndFilter(
                new EqualsFilter("getHomeAddress.getState", "MA"),
                new NotEqualsFilter("getWorkAddress.getState", "MA")));
        printResults("MA Residents, Work Elsewhere", setResults);

        // Find all contacts whose city name begins with 'S'
        setResults = cache.entrySet(new LikeFilter("getHomeAddress.getCity",
                "S%"));
        printResults("City Begins with S", setResults);

        final int nAge = 58;
        // Find all contacts who are older than nAge
        setResults = cache.entrySet(new GreaterFilter("getAge", nAge));
        printResults("Age > " + nAge, setResults);

        // Find all contacts with last name beginning with 'S' that live
        // in Massachusetts. Uses both key and value in the query
        setResults = cache.entrySet(new AndFilter(
                new LikeFilter(new KeyExtractor("getLastName"), "S%",
                        (char) 0, false),
                new EqualsFilter("getHomeAddress.getState", "MA")));
        printResults("Last Name Begins with S and State Is MA", setResults);

        // Count contacts who are older than nAge for the entire cache dataset.
        System.out.println("count > " + nAge + ": " + cache.aggregate(
                new GreaterFilter("getAge", nAge), new Count()));

        // Find minimum age for the entire cache dataset.
        System.out.println("min age: " + cache.aggregate(AlwaysFilter.INSTANCE,
                new LongMin("getAge")));

        // Calculate average age for the entire cache dataset.
        System.out.println("avg age: " + cache.aggregate(AlwaysFilter.INSTANCE,
                new DoubleAverage("getAge")));

        // Find maximum age for the entire cache dataset.
        System.out.println("max age: " + cache.aggregate(AlwaysFilter.INSTANCE,
                new LongMax("getAge")));
        System.out.println("------QueryExample completed------");
        }

    /**
    * Print results of the query
    *
    * @param sTitle     the title that describes the results
    *
    * @param setResults a set of query results
    */
    private void printResults(String sTitle, Set setResults)
        {
        System.out.println(sTitle);
        for (Iterator iter = setResults.iterator(); iter.hasNext();)
            {
            System.out.println(iter.next());
            }
        }
    }