package com.salekur.bachelor.classes;

import android.location.Address;
import android.location.Geocoder;
import android.text.format.DateFormat;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class TimeDateAndAddress {
    private String Latitude, Longitude;
    public String DayInFullName, DayInShortName, DayInNumber, MonthInFullName, MonthInShortName, MonthInNumber, YearInFullNumber, YearInShortNumber, TimeInTogether;
    public String ShortAddress, FullCountryName, ShortCountryCode, DivisionName, PostalCode, DistrictName, CityName;

    public TimeDateAndAddress (Date date) {
        DayInFullName = (String) DateFormat.format("EEEE", date); // Thursday
        DayInShortName = (String) DateFormat.format("ddd", date); // Thu
        DayInNumber= (String) DateFormat.format("dd",   date); // 20

        MonthInFullName  = (String) DateFormat.format("MMMM",  date); // June
        MonthInShortName  = (String) DateFormat.format("MMM",  date); // Jun
        MonthInNumber= (String) DateFormat.format("MM",   date); // 06

        YearInFullNumber = (String) DateFormat.format("yyyy", date); // 2018
        YearInShortNumber = (String) DateFormat.format("yy", date); // 18

        TimeInTogether = (String) DateFormat.format("h:mma", date); // 11:52AM
        TimeInTogether = TimeInTogether.toLowerCase(); //11:52am
    }

    public TimeDateAndAddress (Geocoder geocoder, Double latitude, Double longitude) {
        Latitude = String.valueOf(latitude);
        Longitude = String.valueOf(longitude);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address address = addresses.get(0);

            ShortAddress = address.getAddressLine(0);
            FullCountryName = address.getCountryName();
            ShortCountryCode = address.getCountryCode();
            DivisionName = address.getAdminArea();
            PostalCode = address.getPostalCode();
            DistrictName = address.getSubAdminArea();
            CityName = address.getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getDayInFullName() {
        return DayInFullName;
    }

    public void setDayInFullName(String dayInFullName) {
        DayInFullName = dayInFullName;
    }

    public String getDayInShortName() {
        return DayInShortName;
    }

    public void setDayInShortName(String dayInShortName) {
        DayInShortName = dayInShortName;
    }

    public String getDayInNumber() {
        return DayInNumber;
    }

    public void setDayInNumber(String dayInNumber) {
        DayInNumber = dayInNumber;
    }

    public String getMonthInFullName() {
        return MonthInFullName;
    }

    public void setMonthInFullName(String monthInFullName) {
        MonthInFullName = monthInFullName;
    }

    public String getMonthInShortName() {
        return MonthInShortName;
    }

    public void setMonthInShortName(String monthInShortName) {
        MonthInShortName = monthInShortName;
    }

    public String getMonthInNumber() {
        return MonthInNumber;
    }

    public void setMonthInNumber(String monthInNumber) {
        MonthInNumber = monthInNumber;
    }

    public String getYearInFullNumber() {
        return YearInFullNumber;
    }

    public void setYearInFullNumber(String yearInFullNumber) {
        YearInFullNumber = yearInFullNumber;
    }

    public String getYearInShortNumber() {
        return YearInShortNumber;
    }

    public void setYearInShortNumber(String yearInShortNumber) {
        YearInShortNumber = yearInShortNumber;
    }

    public String getTimeInTogether() {
        return TimeInTogether;
    }

    public void setTimeInTogether(String timeInTogether) {
        TimeInTogether = timeInTogether;
    }

    public String getShortAddress() {
        return ShortAddress;
    }

    public void setShortAddress(String shortAddress) {
        ShortAddress = shortAddress;
    }

    public String getFullCountryName() {
        return FullCountryName;
    }

    public void setFullCountryName(String fullCountryName) {
        FullCountryName = fullCountryName;
    }

    public String getShortCountryCode() {
        return ShortCountryCode;
    }

    public void setShortCountryCode(String shortCountryCode) {
        ShortCountryCode = shortCountryCode;
    }

    public String getDivisionName() {
        return DivisionName;
    }

    public void setDivisionName(String divisionName) {
        DivisionName = divisionName;
    }

    public String getPostalCode() {
        return PostalCode;
    }

    public void setPostalCode(String postalCode) {
        PostalCode = postalCode;
    }

    public String getDistrictName() {
        return DistrictName;
    }

    public void setDistrictName(String districtName) {
        DistrictName = districtName;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }
}
