/*
* BasicExample.java
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

import com.tangosol.examples.model.ContactId;
import com.tangosol.examples.model.Contact;

/**
* BasicExample shows basic cache operations like adding, getting and removing
* data.
*
* @author dag  2009.03.04
*/
public class BasicExample
    {
    // ----- BasicExample methods -------------------------------------------

    /**
    * Execute a cycle of basic operations.
    *
    * @param cache  target cache
    */
    public void execute(NamedCache cache)
        {
        Contact   contact   = DataGenerator.generateContact();
        ContactId contactId = new ContactId(contact.getFirstName(),
                contact.getLastName());

        System.out.println("------BasicExample begins------");
        // associate a ContactId with a Contact in the cache
        cache.put(contactId, contact);

        // retrieve the Contact associated with a ContactId from the cache
        contact = (Contact) cache.get(contactId);

        // remove mapping of ContactId to Contact from the cache.
        cache.remove(contactId);
        System.out.println("------BasicExample completed------");
        }
    }
