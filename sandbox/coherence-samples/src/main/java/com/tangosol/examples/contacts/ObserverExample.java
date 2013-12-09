/*
* ObserverExample.java
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


import com.tangosol.examples.model.Contact;
import com.tangosol.net.NamedCache;
import com.tangosol.util.MapEvent;
import com.tangosol.util.MapListener;


/**
* ObserverExample demonstrates how to use a MapListener to monitor cache events.
*
* @author dag  2009.03.04
*/
public class ObserverExample
    {
    // ----- ObserverExample methods -------------------------------------

    /**
    * Observe changes to the contacts.
    *
    * @param cache  target cache
    */
    public void observe(NamedCache cache)
        {
        System.out.println("------ObserverExample begins------");
        m_listener = new ContactChangeListener();
        cache.addMapListener(m_listener);
        System.out.println("------ContactChangleListener added------");
        }

    /**
    * Stop observing changes to the contacts.
    *
    * @param cache  target cache
    */
    public void remove(NamedCache cache)
        {
        cache.removeMapListener(m_listener);
        System.out.println("------ContactChangeListener removed------");
        System.out.println("------ObserverExample completed------");
        }

    // ----- inner class: ContactChangeListener -------------------------

    /**
    * ContactChangeListener listens for changes to Contacts.
    *
    * @author dag  2009.02.27
    */
    public class ContactChangeListener
            implements MapListener
        {
        // ----- MapListener interface ------------------------------------------
        /**
        * {@inheritDoc}
        */
        public void entryInserted(MapEvent event)
            {
            System.out.println("entry inserted:");
            Contact contactNew = (Contact) event.getNewValue();
            System.out.println(contactNew);
            }

        /**
        * {@inheritDoc}
        */
        public void entryUpdated(MapEvent event)
            {
            System.out.println("entry updated");
            System.out.println("old value:");
            Contact contactOld = (Contact) event.getOldValue();
            System.out.println(contactOld);
            System.out.println("new value:");
            Contact contactNew = (Contact) event.getNewValue();
            System.out.println(contactNew);
            }

        /**
        * {@inheritDoc}
        */
        public void entryDeleted(MapEvent event)
            {
            System.out.println("entry deleted:");
            Contact contactOld = (Contact) event.getOldValue();
            System.out.println(contactOld);
            }
        }

    // ----- data members --------------------------------------------------

    /**
    *  The MapListener observing changes
    */
    private ContactChangeListener m_listener;
    }
