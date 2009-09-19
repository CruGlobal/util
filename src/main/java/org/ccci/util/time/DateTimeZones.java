package org.ccci.util.time;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class DateTimeZones
{

    private static Logger log = LoggerFactory.getLogger(DateTimeZones.class);

	// from info at http://www.statoids.com/tus.html
    static
    {
        List<String> americanZoneIds =
                ImmutableList.of(
                    "America/New_York", 
                    "America/Chicago", 
                    "America/Denver", 
                    "America/Phoenix",
                    "America/Los_Angeles", 
                    "America/Anchorage", 
                    "America/Adak", 
                    "Pacific/Honolulu");
        List<DateTimeZone> americanZones = Lists.newArrayList();
        for (String americanZoneId : americanZoneIds)
        {
            americanZones.add(DateTimeZone.forID(americanZoneId));
        }
        AMERICAN_ZONES = americanZones;
    }
    private static final List<DateTimeZone> AMERICAN_ZONES;

    public static List<DateTimeZone> americanZones()
    {
        return AMERICAN_ZONES;
    }

    /**
     * get US DateTimeZones for the given state
     * @param stateCode e.g. "FL"
     * @return
     */
    public static DateTimeZone dateTimeZoneForState(String stateCode)
    {
        return dateTimeZoneForCountryAndRegion("US", stateCode);
    }
    
    /**
     * get a {@link DateTimeZone} for the given country and region
     * @param country see docs for country on {@link #timeZoneIdForCountryAndRegion(String, String)}
     * @param region see docs for region on {@link #timeZoneIdForCountryAndRegion(String, String)}
     * @return the appropriate {@link DateTimeZone}, or null if the country or region is unknown
     */
    public static DateTimeZone dateTimeZoneForCountryAndRegion(String country, String region)
    {
        String timeZoneId = timeZoneIdForCountryAndRegion(country, region);
		return timeZoneId == null ? null : DateTimeZone.forID(timeZoneId);
    }
    

    /**
     * Adapted from http://geoip.cvs.sourceforge.net/geoip/java/source/com/maxmind/geoip/timeZone.java?hideattic=0&revision=1.3&view=markup
     * 
     * @param country e.g. "US" may not be null
     * @param region e.g. "FL" must not be null if <code>country</code> is US or CA
     * @return the appropriate timezone id, or null if the country or region is unknown
     */
    public static String timeZoneIdForCountryAndRegion(String country, String region)
    {
    	Preconditions.checkNotNull(country, "country is null");
    	if (REGION_TIMEZONE_MAPS.containsKey(country))
    	{
    		Preconditions.checkNotNull(region, "region is null");
    		return REGION_TIMEZONE_MAPS.get(country).get(region);
    	} else {
    		if (region != null)
    		{
    			log.warn("regional data is not available for {}; ignoring {}", country, region);
    		}
    		return OTHER_TIMEZONES.get(country);
    	}
    }

    
    private static final Map<String, String> US_TIMEZONES = 
    	ImmutableMap.<String, String>builder()
        .put("AL", "America/Chicago")
        .put("AK", "America/Anchorage")
        .put("AZ", "America/Phoenix")
        .put("AR", "America/Chicago")
        .put("CA", "America/Los_Angeles")
        .put("CO", "America/Denver")
        .put("CT", "America/New_York")
        .put("DE", "America/New_York")
        .put("DC", "America/New_York")
        .put("FL", "America/New_York")
        .put("GA", "America/New_York")
        .put("HI", "Pacific/Honolulu")
        .put("ID", "America/Denver")
        .put("IL", "America/Chicago")
        .put("IN", "America/Indianapolis")
        .put("IA", "America/Chicago")
        .put("KS", "America/Chicago")
        .put("KY", "America/New_York")
        .put("LA", "America/Chicago")
        .put("ME", "America/New_York")
        .put("MD", "America/New_York")
        .put("MA", "America/New_York")
        .put("MI", "America/New_York")
        .put("MN", "America/Chicago")
        .put("MS", "America/Chicago")
        .put("MO", "America/Chicago")
        .put("MT", "America/Denver")
        .put("NE", "America/Chicago")
        .put("NV", "America/Los_Angeles")
        .put("NH", "America/New_York")
        .put("NJ", "America/New_York")
        .put("NM", "America/Denver")
        .put("NY", "America/New_York")
        .put("NC", "America/New_York")
        .put("ND", "America/Chicago")
        .put("OH", "America/New_York")
        .put("OK", "America/Chicago")
        .put("OR", "America/Los_Angeles")
        .put("PA", "America/New_York")
        .put("RI", "America/New_York")
        .put("SC", "America/New_York")
        .put("SD", "America/Chicago")
        .put("TN", "America/Chicago")
        .put("TX", "America/Chicago")
        .put("UT", "America/Denver")
        .put("VT", "America/New_York")
        .put("VA", "America/New_York")
        .put("WA", "America/Los_Angeles")
        .put("WV", "America/New_York")
        .put("WI", "America/Chicago")
        .build();
    
    private final static Map<String, String> CA_TIMEZONES = 
    	ImmutableMap.<String, String>builder()
            .put("AB", "America/Edmonton")
            .put("BC", "America/Vancouver")
            .put("MB", "America/Winnipeg")
            .put("NB", "America/Halifax")
            .put("NF", "America/St_Johns")
            .put("NT", "America/Yellowknife")
            .put("NS", "America/Halifax")
            .put("NU", "America/Rankin_Inlet")
            .put("ON", "America/Rainy_River")
            .put("PE", "America/Halifax")
            .put("QC", "America/Montreal")
            .put("SK", "America/Regina")
            .build();
    
    /** maps country to a map that maps each region to its timezone */
    private final static ImmutableMap<String, Map<String, String>> REGION_TIMEZONE_MAPS = 
    	ImmutableMap.<String, Map<String, String>>builder()
    		.put("US", US_TIMEZONES)
    		.put("CA", CA_TIMEZONES)
    		.build();
    	
    private final static Map<String, String> OTHER_TIMEZONES = 
    	ImmutableMap.<String, String>builder()
            .put("AS", "US/Samoa")
            .put("CI", "Africa/Abidjan")
            .put("GH", "Africa/Accra")
            .put("DZ", "Africa/Algiers")
            .put("ER", "Africa/Asmera")
            .put("ML", "Africa/Bamako")
            .put("CF", "Africa/Bangui")
            .put("GM", "Africa/Banjul")
            .put("GW", "Africa/Bissau")
            .put("CG", "Africa/Brazzaville")
            .put("BI", "Africa/Bujumbura")
            .put("EG", "Africa/Cairo")
            .put("MA", "Africa/Casablanca")
            .put("GN", "Africa/Conakry")
            .put("SN", "Africa/Dakar")
            .put("DJ", "Africa/Djibouti")
            .put("SL", "Africa/Freetown")
            .put("BW", "Africa/Gaborone")
            .put("ZW", "Africa/Harare")
            .put("ZA", "Africa/Johannesburg")
            .put("UG", "Africa/Kampala")
            .put("SD", "Africa/Khartoum")
            .put("RW", "Africa/Kigali")
            .put("NG", "Africa/Lagos")
            .put("GA", "Africa/Libreville")
            .put("TG", "Africa/Lome")
            .put("AO", "Africa/Luanda")
            .put("ZM", "Africa/Lusaka")
            .put("GQ", "Africa/Malabo")
            .put("MZ", "Africa/Maputo")
            .put("LS", "Africa/Maseru")
            .put("SZ", "Africa/Mbabane")
            .put("SO", "Africa/Mogadishu")
            .put("LR", "Africa/Monrovia")
            .put("KE", "Africa/Nairobi")
            .put("TD", "Africa/Ndjamena")
            .put("NE", "Africa/Niamey")
            .put("MR", "Africa/Nouakchott")
            .put("BF", "Africa/Ouagadougou")
            .put("ST", "Africa/Sao_Tome")
            .put("LY", "Africa/Tripoli")
            .put("TN", "Africa/Tunis")
            .put("AI", "America/Anguilla")
            .put("AG", "America/Antigua")
            .put("AW", "America/Aruba")
            .put("BB", "America/Barbados")
            .put("BZ", "America/Belize")
            .put("CO", "America/Bogota")
            .put("VE", "America/Caracas")
            .put("KY", "America/Cayman")
            .put("MX", "America/Chihuahua")
            .put("CR", "America/Costa_Rica")
            .put("DM", "America/Dominica")
            .put("SV", "America/El_Salvador")
            .put("GD", "America/Grenada")
            .put("FR", "Europe/Paris")
            .put("GP", "America/Guadeloupe")
            .put("GT", "America/Guatemala")
            .put("EC", "America/Guayaquil")
            .put("GY", "America/Guyana")
            .put("CU", "America/Havana")
            .put("JM", "America/Jamaica")
            .put("BO", "America/La_Paz")
            .put("PE", "America/Lima")
            .put("NI", "America/Managua")
            .put("MQ", "America/Martinique")
            .put("AR", "America/Mendoza")
            .put("UY", "America/Montevideo")
            .put("MS", "America/Montserrat")
            .put("BS", "America/Nassau")
            .put("PA", "America/Panama")
            .put("SR", "America/Paramaribo")
            .put("PR", "America/Puerto_Rico")
            .put("KN", "America/St_Kitts")
            .put("LC", "America/St_Lucia")
            .put("VC", "America/St_Vincent")
            .put("HN", "America/Tegucigalpa")
            .put("YE", "Asia/Aden")
            .put("KZ", "Asia/Almaty")
            .put("JO", "Asia/Amman")
            .put("TM", "Asia/Ashgabat")
            .put("IQ", "Asia/Baghdad")
            .put("BH", "Asia/Bahrain")
            .put("AZ", "Asia/Baku")
            .put("TH", "Asia/Bangkok")
            .put("LB", "Asia/Beirut")
            .put("KG", "Asia/Bishkek")
            .put("BN", "Asia/Brunei")
            .put("IN", "Asia/Calcutta")
            .put("MN", "Asia/Choibalsan")
            .put("CN", "Asia/Chongqing")
            .put("LK", "Asia/Colombo")
            .put("BD", "Asia/Dhaka")
            .put("AE", "Asia/Dubai")
            .put("TJ", "Asia/Dushanbe")
            .put("HK", "Asia/Hong_Kong")
            .put("TR", "Asia/Istanbul")
            .put("ID", "Asia/Jakarta")
            .put("IL", "Asia/Jerusalem")
            .put("AF", "Asia/Kabul")
            .put("PK", "Asia/Karachi")
            .put("NP", "Asia/Katmandu")
            .put("KW", "Asia/Kuwait")
            .put("MO", "Asia/Macao")
            .put("PH", "Asia/Manila")
            .put("OM", "Asia/Muscat")
            .put("CY", "Asia/Nicosia")
            .put("KP", "Asia/Pyongyang")
            .put("QA", "Asia/Qatar")
            .put("MM", "Asia/Rangoon")
            .put("SA", "Asia/Riyadh")
            .put("KR", "Asia/Seoul")
            .put("SG", "Asia/Singapore")
            .put("TW", "Asia/Taipei")
            .put("UZ", "Asia/Tashkent")
            .put("GE", "Asia/Tbilisi")
            .put("BT", "Asia/Thimphu")
            .put("JP", "Asia/Tokyo")
            .put("LA", "Asia/Vientiane")
            .put("AM", "Asia/Yerevan")
            .put("PT", "Atlantic/Azores")
            .put("BM", "Atlantic/Bermuda")
            .put("CV", "Atlantic/Cape_Verde")
            .put("FO", "Atlantic/Faeroe")
            .put("IS", "Atlantic/Reykjavik")
            .put("GS", "Atlantic/South_Georgia")
            .put("SH", "Atlantic/St_Helena")
            .put("AU", "Australia/Queensland")
            .put("BR", "Brazil/Acre")
            .put("CL", "Chile/Continental")
            .put("NL", "Europe/Amsterdam")
            .put("AD", "Europe/Andorra")
            .put("GR", "Europe/Athens")
            .put("YU", "Europe/Belgrade")
            .put("DE", "Europe/Berlin")
            .put("SK", "Europe/Bratislava")
            .put("BE", "Europe/Brussels")
            .put("RO", "Europe/Bucharest")
            .put("HU", "Europe/Budapest")
            .put("DK", "Europe/Copenhagen")
            .put("IE", "Europe/Dublin")
            .put("GI", "Europe/Gibraltar")
            .put("FI", "Europe/Helsinki")
            .put("UA", "Europe/Kiev")
            .put("SI", "Europe/Ljubljana")
            .put("GB", "Europe/London")
            .put("LU", "Europe/Luxembourg")
            .put("ES", "Europe/Madrid")
            .put("MT", "Europe/Malta")
            .put("BY", "Europe/Minsk")
            .put("MC", "Europe/Monaco")
            .put("RU", "Europe/Moscow")
            .put("NO", "Europe/Oslo")
            .put("CZ", "Europe/Prague")
            .put("LV", "Europe/Riga")
            .put("IT", "Europe/Rome")
            .put("SM", "Europe/San_Marino")
            .put("BA", "Europe/Sarajevo")
            .put("MK", "Europe/Skopje")
            .put("BG", "Europe/Sofia")
            .put("SE", "Europe/Stockholm")
            .put("EE", "Europe/Tallinn")
            .put("AL", "Europe/Tirane")
            .put("LI", "Europe/Vaduz")
            .put("VA", "Europe/Vatican")
            .put("AT", "Europe/Vienna")
            .put("LT", "Europe/Vilnius")
            .put("PL", "Europe/Warsaw")
            .put("HR", "Europe/Zagreb")
            .put("IR", "Asia/Tehran")
            .put("NZ", "Pacific/Auckland")
            .put("MG", "Indian/Antananarivo")
            .put("CX", "Indian/Christmas")
            .put("CC", "Indian/Cocos")
            .put("KM", "Indian/Comoro")
            .put("MV", "Indian/Maldives")
            .put("MU", "Indian/Mauritius")
            .put("YT", "Indian/Mayotte")
            .put("RE", "Indian/Reunion")
            .put("FJ", "Pacific/Fiji")
            .put("TV", "Pacific/Funafuti")
            .put("GU", "Pacific/Guam")
            .put("NR", "Pacific/Nauru")
            .put("NU", "Pacific/Niue")
            .put("NF", "Pacific/Norfolk")
            .put("PW", "Pacific/Palau")
            .put("PN", "Pacific/Pitcairn")
            .put("CK", "Pacific/Rarotonga")
            .put("WS", "Pacific/Samoa")
            .put("KI", "Pacific/Tarawa")
            .put("TO", "Pacific/Tongatapu")
            .put("WF", "Pacific/Wallis")
            .put("TZ", "Africa/Dar_es_Salaam")
            .put("VN", "Asia/Phnom_Penh")
            .put("KH", "Asia/Phnom_Penh")
            .put("CM", "Africa/Lagos")
            .put("DO", "America/Santo_Domingo")
            .put("TL", "Asia/Jakarta")
            .put("ET", "Africa/Addis_Ababa")
            .put("FX", "Europe/Paris")
            .put("GL", "America/Godthab")
            .put("HT", "America/Port-au-Prince")
            .put("CH", "Europe/Zurich")
            .put("AN", "America/Curacao")
            .put("BJ", "Africa/Porto-Novo")
            .put("EH", "Africa/El_Aaiun")
            .put("FK", "Atlantic/Stanley")
            .put("GF", "America/Cayenne")
            .put("IO", "Indian/Chagos")
            .put("MD", "Europe/Chisinau")
            .put("MP", "Pacific/Saipan")
            .put("MW", "Africa/Blantyre")
            .put("NA", "Africa/Windhoek")
            .put("NC", "Pacific/Noumea")
            .put("PG", "Pacific/Port_Moresby")
            .put("PM", "America/Miquelon")
            .put("PS", "Asia/Gaza")
            .put("PY", "America/Asuncion")
            .put("SB", "Pacific/Guadalcanal")
            .put("SC", "Indian/Mahe")
            .put("SJ", "Arctic/Longyearbyen")
            .put("SY", "Asia/Damascus")
            .put("TC", "America/Grand_Turk")
            .put("TF", "Indian/Kerguelen")
            .put("TK", "Pacific/Fakaofo")
            .put("TT", "America/Port_of_Spain")
            .put("VG", "America/Tortola")
            .put("VI", "America/St_Thomas")
            .put("VU", "Pacific/Efate")
            .put("RS", "Europe/Belgrade")
            .put("ME", "Europe/Podgorica")
            .put("AX", "Europe/Mariehamn")
            .put("GG", "Europe/Guernsey")
            .put("IM", "Europe/Isle_of_Man")
            .put("JE", "Europe/Jersey")
            .put("BL", "America/St_Barthelemy")
            .put("MF", "America/Marigot")
            .build();
    
}
