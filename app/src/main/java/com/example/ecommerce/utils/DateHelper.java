package com.example.ecommerce.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateHelper {

    public static String dateFormat = "EEE MMM dd HH:mm:ss O yyyy";
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

    public static String formatDate(String date){
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, formatter);
        return zonedDateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    public static String timeAgo(ZonedDateTime pastDate) {
        ZonedDateTime now = ZonedDateTime.now();
        Duration duration = Duration.between(pastDate, now);

        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (seconds < 60) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minutes ago";
        } else if (hours < 24) {
            return hours + " hours " + (minutes % 60) + " minutes ago";
        } else if (days < 30) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else if (days < 365) {
            return (days / 30) + " month" + ((days / 30) > 1 ? "s" : "") + " ago";
        } else {
            return (days / 365) + " year" + ((days / 365) > 1 ? "s" : "") + " ago";
        }
    }

    public static ZonedDateTime convertStringToZonedDateTime(String date){
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, formatter);
        return zonedDateTime;
    }

    public static String getTimeStamp(){
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        return zonedDateTime.format(formatter);
    }
}
