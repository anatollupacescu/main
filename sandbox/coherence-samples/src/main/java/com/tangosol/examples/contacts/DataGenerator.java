/*
* DataGenerator.java
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
import com.tangosol.examples.model.PhoneNumber;
import com.tangosol.util.Base;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.Collections;
import java.util.Random;


/**
* DataGenerator generates random Contact information and store the result in a
* CSV file. The data can may then be loaded with the LoaderExample.
*
* @author dag  2009.02.19
*/
public class DataGenerator
    {
    // ----- static methods -------------------------------------------------

    /**
    * Generate contacts.
    *
    * usage: [file-name] [contact-count]
    *
    * @param asArg  command line arguments
    *
    * @throws IOException if file cannot be written
    */
    public static void main(String[] asArg)
            throws IOException
        {
        String       sFile = asArg.length > 0 ? asArg[0] : Driver.DEFAULT_DATAFILE;
        int          cCon  = asArg.length > 1 ? Integer.parseInt(asArg[1]) : 1000;
        OutputStream out   = new FileOutputStream(sFile);

        generate(out, cCon);
        out.close();
        }

    /**
    * Generate the contacts and write them to a file.
    *
    * @param out        output stream for contacts
    * @param cContacts  number of contacts to create
    *
    * @throws IOException if file cannot be written
    */
    public static void generate(OutputStream out, int cContacts)
            throws IOException
        {
        PrintWriter writer = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(out)));

        for (int i = 0; i < cContacts; ++i)
            {
            StringBuffer sb = new StringBuffer(256);

            //contact person
            sb.append("John,")
              .append(getRandomName())
              .append(',');

            // random birth date in millis before or after the epoch

            Date birthdate = new Date(getRandomDateInMillis());
            sb.append(birthdate.toString())
              .append(',');

            // home and work addresses
            sb.append(Integer.toString(Base.getRandom().nextInt(999)))
              .append(" Beacon St.,,") /*street1,empty street2*/
              .append(getRandomName()) /*random city name*/
              .append(',')
              .append(getRandomState())
              .append(',')
              .append(getRandomZip())
              .append(",US,Yoyodyne Propulsion Systems,")
              .append("330 Lectroid Rd.,Grover's Mill,")
              .append(getRandomState())
              .append(',')
              .append(getRandomZip())
              .append(",US,");

            // home and work phone numbers
            sb.append("home,")
              .append(Base.toDelimitedString(getRandomPhoneDigits(), ","))
              .append(",work,")
              .append(Base.toDelimitedString(getRandomPhoneDigits(), ","))
              .append(',');

            writer.println(sb);
            }
        writer.flush();
        }

    /**
    * Return a random name.
    *
    * @return a randonm name
    */
    private static String getRandomName()
        {
        Random rand = Base.getRandom();
        int    cCh  = 4 + rand.nextInt(7);
        char[] ach  = new char[cCh];

        ach[0] = (char) ('A' + rand.nextInt(26));
        for (int of = 1; of < cCh; ++of)
            {
            ach[of] = (char) ('a' + rand.nextInt(26));
            }
        return new String(ach);
        }

    /**
    * Return a random phone muber.
    * <p/>
    * The phone number contains including access, country, area code, and local
    * number.
    *
    * @return a random phone number
    */
    private static int[] getRandomPhoneDigits()
        {
        Random rand = Base.getRandom();
        return new int[] {
            11,                   // access code
            rand.nextInt(99),     // country code
            rand.nextInt(999),    // area code
            rand.nextInt(9999999) // local number
            };
        }

    /**
    * Return a random Phone.
    *
    * @return a random phone
    */
    private static PhoneNumber getRandomPhone()
        {
        int[] anPhone = getRandomPhoneDigits();

        return new PhoneNumber((short)anPhone[0], (short)anPhone[1],
                (short)anPhone[2], anPhone[3]);

        }
    /**
    * Return a random Zip code.
    *
    * @return a random Zip code
    */
    private static String getRandomZip()
        {
        return Base.toDecString(Base.getRandom().nextInt(99999), 5);
        }

    /**
    * Return a random state.
    *
    * @return a random state
    */
    private static String getRandomState()
        {
        return STATE_CODES[Base.getRandom().nextInt(STATE_CODES.length)];
        }

    /**
    * Return a random date in millis before or after the epoch.
    *
    * @return a random date in millis before or after the epoch
    */
    private static long getRandomDateInMillis()
        {
        return (Base.getRandom().nextInt(40) - 20) * Contact.MILLIS_IN_YEAR;
        }

    /**
    * Generate a Contact with random information.
    *
    * @return a Contact with random information
    */
    public static Contact generateContact()
        {
        return new Contact("John",
                getRandomName(),
                new Address("1500 Boylston St.", null, getRandomName(),
                    getRandomState(), getRandomZip(), "US"),
                new Address("8 Yawkey Way", null, getRandomName(),
                    getRandomState(), getRandomZip(), "US"),
                Collections.singletonMap("work", getRandomPhone()),
                new Date(getRandomDateInMillis()));
        }


    // ----- constants ------------------------------------------------------

    /**
    * US Postal Service two letter postal codes.
    */
    private static final String[] STATE_CODES = {
            "AL", "AK", "AS", "AZ", "AR", "CA", "CO", "CT", "DE", "OF", "DC",
            "FM", "FL", "GA", "GU", "HI", "ID", "IL", "IN", "IA", "KS", "KY",
            "LA", "ME", "MH", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE",
            "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "MP", "OH", "OK", "OR",
            "PW", "PA", "PR", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VI",
            "VA", "WA", "WV", "WI", "WY"
        };
    }
