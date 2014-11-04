/*
* ProcessorExample.java
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

import com.tangosol.util.filter.EqualsFilter;
import com.tangosol.util.processor.AbstractProcessor;
import com.tangosol.util.InvocableMap;

import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

import com.tangosol.examples.model.Address;
import com.tangosol.examples.model.Contact;

import java.io.IOException;


/**
* ProcessorExample demonstrates how to use a processor to modify data in the
* cache. All Contacts who live in MA will have their work address updated.
*
* @author dag  2009.02.26
*/
public class ProcessorExample
    {
    // ----- ProcessorExample methods -----------------------------------

    /**
    * Perform the example updates to contacts.
    *
    * @param cache  Cache
    */
    public void execute(NamedCache cache)
        {
        System.out.println("------ProcessorExample begins------");
        // People who live in Massachusetts moved to an in-state office
        Address addrWork = new Address("200 Newbury St.", "Yoyodyne, Ltd.",
                "Boston", "MA", "02116", "US");
       //Apply the OfficeUpdater on all contacts which lives in MA
        cache.invokeAll(new EqualsFilter("getHomeAddress.getState", "MA"),
                new OfficeUpdater(addrWork));
        System.out.println("------ProcessorExample completed------");
        }


    // ----- nested class: OfficeUpdater ------------------------------------

    /**
    * OfficeUpdater updates a contact's office address.
    *
    * @author dag  2009.02.26
    */
    public static class OfficeUpdater
            extends AbstractProcessor
            implements PortableObject
        {
        // ----- constructors -------------------------------------------

        /**
        * Default constructor (necessary for PortableObject implementation).
        */
        public OfficeUpdater()
            {
            }

        /**
        * Construct an OfficeUpdater with a new work Address.
        *
        * @param addrWork  the new work address.
        */
        public OfficeUpdater(Address addrWork)
            {
            m_addrWork = addrWork;
            }

        // ----- InvocableMap.EntryProcessor interface ------------------

        /**
        * {@inheritDoc}
        */
        public Object process(InvocableMap.Entry entry)
            {
            Contact contact = (Contact) entry.getValue();

            contact.setWorkAddress(m_addrWork);
            entry.setValue(contact);
            return null;
            }

        // ----- PortableObject interface -------------------------------

        /**
        * {@inheritDoc}
        */
        public void readExternal(PofReader reader)
                throws IOException
            {
            m_addrWork = (Address) reader.readObject(WORK_ADDRESS);
            }

        /**
        * {@inheritDoc}
        */
        public void writeExternal(PofWriter writer)
                throws IOException
            {
            writer.writeObject(WORK_ADDRESS, m_addrWork);
            }


        // ----- constants ----------------------------------------------

        /**
        * The POF index for the WorkAddress property
        */
        public static final int WORK_ADDRESS = 0;


        // ----- data members -------------------------------------------

        /**
        * New work address.
        */
        private Address m_addrWork;
        }
    }
