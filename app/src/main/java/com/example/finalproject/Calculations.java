package com.example.finalproject;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Calculations {

    private String calculateTimeBetweenDates(String startDate) {
        String endDate = timeStampToString(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date1 = null;

        try {
            date1 = (Date) sdf.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = (Date) sdf.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean isNegative = false;
        long difference = date2.getTime() - date1.getTime();

        if (difference < 0) {
            difference = -(difference);
            isNegative = true;
        }

        long minutes = difference / 60 / 1000;
        long hours = difference / 60 / 1000 / 60;
        long days = (difference / 60 / 1000 / 60 ) / 24;
        long months = (difference / 60 / 1000 / 60 ) / 24 / (365 / 12);
        long years = difference / 60 / 1000 / 60 /24 / 365;

        if (isNegative) {
            if (minutes < 240) {
                return "Starts in " + minutes + " minutes";
            } else if (hours < 48) {
                return "Starts in " + hours + " hours";
            } else if (days < 61) {
                return "Starts in " + days + " days";
            } else if (months < 24) {
                return "Starts in " + months + " months";
            } else {
                return "Starts in " + years + " years";
            }
        } else {
            if (minutes < 240) {
                return "Started " + minutes + " minutes ago";
            } else if (hours < 48) {
                return "Started " + hours + " hours ago";
            } else if (days < 61) {
                return "Started " + days + " days ago";
            } else if (months < 24) {
                return "Started " + months + " months ago";
            } else {
                return "Started " + years + " years ago";
            }
        }
    }

    private String timeStampToString(long timeStamp) {
        Timestamp stamp = new Timestamp(timeStamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String date = sdf.format((new Date(stamp.getTime())));
        return date;
    }

    String cleanDate(int _day, int _months, int _year) {
        String day = Integer.toString(_day);
        String months = Integer.toString(_months);

        if (_day < 10) {
            day = "0" + _day;
        }
        if (_months < 9) {
            months = "0" + (_months + 1);
        }
        return day + "/" + months + "/" + _year;
    }

    String cleanTime(int _hour, int _minute) {
        String hour = Integer.toString(_hour);
        String minute = Integer.toString(_minute);

        if (_hour < 10) {
            hour = "0" + _hour;
        }
        if (_minute < 10) {
            minute = "0" + _minute;
        }

        return hour + ":" + minute;
    }

}
