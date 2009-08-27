package org.ccci.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Maybe this should be an enum, but I'm not sure of all the potential values
 * 
 * @author Matt Drees
 *
 */
public class PayGroup
{

    public static final String TEMPORARY_FULLTIME_HOURLY = "TFH";
    public static final String HOURLY_PART_TIME = "HPT";
    public static final String HOURLY_FULL_TIME = "HFT";
    public static final String US_SUPPORTED = "USS";
    public static final String INTERNATIONAL_SUPPORTED = "INT";
    public static final String RICE_SUPPORTED = "RCE";
    public static final String SALARIED = "SAL";
    
    public static boolean isIntern(String payGroup)
    {
        List<String> nonInternPayGroups = Lists.newArrayList(
            US_SUPPORTED, 
            INTERNATIONAL_SUPPORTED, 
            RICE_SUPPORTED, 
            SALARIED, 
            HOURLY_FULL_TIME,
            HOURLY_PART_TIME);
        
        // Leftovers should be STINTers... STN, SNO
        // What about... REM NOP ?
        // What are MFF, OFF, BEN, DIS, TFH, SNB, SSP  ?
        return !nonInternPayGroups.contains(payGroup);
    }

    public static boolean isHourly(String payGroup)
    {
        List<String> hourlyPayGroups = Lists.newArrayList(
            HOURLY_FULL_TIME, 
            HOURLY_PART_TIME, 
            TEMPORARY_FULLTIME_HOURLY);
        return hourlyPayGroups.contains(payGroup);
    }
}
