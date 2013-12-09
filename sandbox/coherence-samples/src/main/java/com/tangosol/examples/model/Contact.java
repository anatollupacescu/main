/*
* Contact.java
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

package com.tangosol.examples.model;


import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;

import java.io.IOException;

import java.sql.Date;

import java.util.Iterator;
import java.util.Map;


/**
* Contact represents information needed to contact a person.
* <p/>
* The type implements PortableObject for efficient cross-platform
* serialization..
*
* @author dag  2009.02.17
*/
public class Contact
        implements PortableObject
    {
    // ----- constructors ---------------------------------------------------

    /**
    * Default constructor (necessary for PortableObject implementation).
    */
    public Contact()
        {
        }

    /**
    * Construct Contact
    *
    * @param sFirstName      the first name
    * @param sLastName       the last name
    * @param addrHome        the home address
    * @param addrWork        the work address
    * @param mapPhoneNumber  map string number type (e.g. "work") to
    *                        PhoneNumber
    * @param dtBirth         date of birth
    */
    public Contact(String sFirstName, String sLastName, Address addrHome,
            Address addrWork, Map mapPhoneNumber, java.sql.Date dtBirth)
        {
        m_sFirstName     = sFirstName;
        m_sLastName      = sLastName;
        m_addrHome       = addrHome;
        m_addrWork       = addrWork;
        m_mapPhoneNumber = mapPhoneNumber;
        m_dtBirth        = dtBirth;
        }


    // ----- accessors ------------------------------------------------------

    /**
    * Return the first name.
    *
    * @return the first name
    */
    public String getFirstName()
        {
        return m_sFirstName;
        }

    /**
    * Set the first name.
    *
    * @param sFirstName  the first name
    */
    public void setFirstName(String sFirstName)
        {
        m_sFirstName = sFirstName;
        }

    /**
    * Return the last name.
    *
    * @return the last name
    */
    public String getLastName()
        {
        return m_sLastName;
        }


    /**
    * Set the last name.
    *
    * @param sLastName  the last name
    */
    public void setLastName(String sLastName)
        {
        m_sLastName = sLastName;
        }

   /**
    * Return the home address.
    *
    * @return the home address
    */
    public Address getHomeAddress()
        {
        return m_addrHome;
        }

    /**
    * Set the home address.
    *
    * @param addrHome  the home address
    */
    public void setHomeAddress(Address addrHome)
        {
        m_addrHome = addrHome;
        }

    /**
    * Return the work address.
    *
    * @return the work address
    */
    public Address getWorkAddress()
        {
        return m_addrWork;
        }

    /**
    * Set the work address.
    *
    * @param addrWork  the work address
    */
    public void setWorkAddress(Address addrWork)
        {
        m_addrWork = addrWork;
        }

    /**
    * Get all phone numbers.
    *
    * @return a map of phone numbers
    */
    public Map getPhoneNumbers()
        {
        return m_mapPhoneNumber;
        }

    /**
    * Set the list of phone numbers.
    *
    * @param mapTelNumber  a map of phone numbers
    */
    public void setPhoneNumbers(Map mapTelNumber)
        {
        m_mapPhoneNumber = mapTelNumber;
        }

    /**
    * Get the date of birth.
    *
    * @return the date of birth
    */
    public Date getBirthDate()
        {
        return m_dtBirth;
        }

    /**
    * Set the date of birth.
    *
    * @param dtBirth  the date of birth
    */
    public void setBirthDate(Date dtBirth)
        {
        m_dtBirth = dtBirth;
        }

    /**
    * Get age.
    *
    * @return age
    */
    public int getAge()
        {
        return (int) ((System.currentTimeMillis() - m_dtBirth.getTime()) /
                MILLIS_IN_YEAR);
        }


    // ----- PortableObject interface ---------------------------------------

    /**
    * {@inheritDoc}
    */
    public void readExternal(PofReader reader)
            throws IOException
        {
        m_sFirstName     = reader.readString(FIRSTNAME);
        m_sLastName      = reader.readString(LASTNAME);
        m_addrHome       = (Address) reader.readObject(HOME_ADDRESS);
        m_addrWork       = (Address) reader.readObject(WORK_ADDRESS);
        m_mapPhoneNumber = reader.readMap(PHONE_NUMBERS, null);
        m_dtBirth        = new Date(reader.readDate(BIRTH_DATE).getTime());
        }

    /**
    * {@inheritDoc}
    */
    public void writeExternal(PofWriter writer)
            throws IOException
        {
        writer.writeString(FIRSTNAME, m_sFirstName);
        writer.writeString(LASTNAME, m_sLastName);
        writer.writeObject(HOME_ADDRESS, m_addrHome);
        writer.writeObject(WORK_ADDRESS, m_addrWork);
        writer.writeMap(PHONE_NUMBERS, m_mapPhoneNumber);
        writer.writeDate(BIRTH_DATE, new java.util.Date(m_dtBirth.getTime()));
        }


    // ----- Object methods -------------------------------------------------

    /**
    * {@inheritDoc}
    */
    public String toString()
        {
        StringBuffer sb = new StringBuffer(getFirstName())
                .append(" ")
                .append(getLastName())
                .append("\nAddresses")
                .append("\nHome: ").append(getHomeAddress())
                .append("\nWork: ").append(getWorkAddress())
                .append("\nPhone Numbers");

        for (Iterator iter = m_mapPhoneNumber.entrySet().iterator();
             iter.hasNext(); )
            {
            Map.Entry entry = (Map.Entry) iter.next();
            sb.append("\n")
               .append(entry.getKey()).append(": ").append(entry.getValue());
            }
        return sb.append("\nBirth Date: ").append(getBirthDate()).toString();
        }


    // ----- constants -------------------------------------------------------

    /**
    * The POF index for the FirstName property
    */
    public static final int FIRSTNAME = 0;

    /**
    * The POF index for the LastName property
    */
    public static final int LASTNAME = 1;

    /**
    * The POF index for the HomeAddress property
    */
    public static final int HOME_ADDRESS = 2;

    /**
    * The POF index for the WorkAddress property
    */
    public static final int WORK_ADDRESS = 3;

    /**
    * The POF index for the PhoneNumbers property
    */
    public static final int PHONE_NUMBERS = 4;

    /**
    * The POF index for the BirthDate property
    */
    public static final int BIRTH_DATE = 5;


    // ----- data members ---------------------------------------------------

    /**
    * First name.
    */
    private String m_sFirstName;

    /**
    * Last name.
    */
    private String m_sLastName;

    /**
    * Home address.
    */
    private Address m_addrHome;

    /**
    * Work address.
    */
    private Address m_addrWork;

    /**
    *  Maps phone number type (such as "work", "home") to PhoneNumber.
    */
    private Map m_mapPhoneNumber;

    /**
    * Birth Date.
    */
    private Date m_dtBirth;

    /**
    * Approximate number of millis in a year ignoring things such as leap
    * years. Suitable for example use only.
    */
    public static final long MILLIS_IN_YEAR = 1000L * 60L * 60L * 24L * 365L;
    }
