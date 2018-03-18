package io.toro.pairprogramming.services;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.toro.pairprogramming.models.User;

@Service
public class UserTimeZoneMatcherService {
    private DateTimeFormatter localDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<User> matchPartnerToUserByTimeZone(List<User> userList, User user){
        convertPartnerStartTimeToMyTimeZone( userList, user );
        convertPartnerEndTimeToMyTimeZone( userList, user );
        List<User> filteredUserListByMatchingTimeZone = filterUserListByMatchingTimeZone( userList, user );
        return filterUserListByWorkShiftDuration(filteredUserListByMatchingTimeZone);
    }

    /**
     * This method converts the possible
     * partner's start time to your time zone
     * and set it to the list's start time
     * @param userList list of possible partners
     * @param user your information to be extracted
     */
    private void convertPartnerStartTimeToMyTimeZone(List<User> userList, User user){
        userList.forEach( x -> x.getWorkShift().setStartTime(
                        Timestamp.valueOf(
                                LocalDateTime.parse(x.getWorkShift().getStartTime().toString().substring( 0,19 ), localDateTimeFormat)
                                        .atZone(ZoneId.of( x.getWorkShift().getTimeZone() ) )
                                        .withZoneSameInstant( ZoneId.of( user.getWorkShift().getTimeZone() ) )
                                        .toLocalDateTime() ) ) );
    }

    /**
     * This method converts the possible
     * partner's end time to your time zone
     * and set it to the list's end time
     * @param userList list of possible partners
     * @param user your information to be extracted
     */
    private void convertPartnerEndTimeToMyTimeZone(List<User> userList, User user){
        userList.forEach( x -> x.getWorkShift().setEndTime(
                        Timestamp.valueOf(
                                LocalDateTime.parse(x.getWorkShift().getEndTime().toString().substring( 0,19 ), localDateTimeFormat)
                                        .atZone(ZoneId.of( x.getWorkShift().getTimeZone() ) )
                                        .withZoneSameInstant( ZoneId.of( user.getWorkShift().getTimeZone() ) )
                                        .toLocalDateTime() ) ) );
    }

    /**
     * This method filter out your possible
     * partner's list by matching your timezone
     * and theirs
     * @param userList list of possible partners
     * @param user your information to be extracted
     * @return the filtered list by matching your timezone
     */
    private List<User> filterUserListByMatchingTimeZone(List<User> userList, User user){
        return userList.stream().filter( x ->
                        isPartnerStartTimeBeforeUserStartTime( user, x ) && isPartnerEndTimeAfterUserEndTime( user, x )  ||
                                isPartnerStartTimeEqualsToUserStartTime( user, x ) && isPartnerEndTimeEqualsToUserEndTime( user, x ) )
                .collect( Collectors.toList());
    }

    /**
     * This method arrange the list by the
     * highest workshift time duration available
     * @param filteredUserListByMatchingTimeZone the filtered list by matching timezone
     * @return the filtered list by highest workshift duration
     */
    private List<User> filterUserListByWorkShiftDuration(List<User> filteredUserListByMatchingTimeZone){
        List<User> reversedList= filteredUserListByMatchingTimeZone.stream()
                .sorted( Comparator.comparingLong( o -> o.getWorkShift().getDuration() ) )
                .collect(Collectors.toList());
        Collections.reverse( reversedList);
        return reversedList;
    }

    /**
     * This method checks if user end time is
     * equal to partner end time
     * @param user your information to be extracted
     * @param x stream of userList
     * @return true if user end time is equal to partner end time
     */
    private boolean isPartnerEndTimeEqualsToUserEndTime( User user, User x ) {
        return LocalTime.parse( user.getWorkShift().getEndTime().toString()
                .substring( 11, x.getWorkShift().getEndTime().toString().length() ) )
                .equals( LocalTime.parse( x.getWorkShift().getEndTime().toString()
                                .substring( 11, x.getWorkShift().getEndTime().toString().length() ) ) );
    }

    /**
     * This method checks if user start time is
     * equal to partner start time
     * @param user your information to be extracted
     * @param x stream of userList
     * @return true if user start time is equal to partner start time
     */
    private boolean isPartnerStartTimeEqualsToUserStartTime( User user, User x ) {
        return LocalTime.parse( user.getWorkShift().getStartTime().toString()
            .substring( 11, x.getWorkShift().getStartTime().toString().length() ) )
            .equals(LocalTime.parse( x.getWorkShift().getStartTime().toString()
                      .substring( 11, x.getWorkShift().getStartTime().toString().length() ) ) );
    }

    /**
     * This method checks if the user end time is
     * after partner end time
     * @param user your information to be extracted
     * @param x stream of userList
     * @return true if user end time is after partner end time time
     */
    private boolean isPartnerEndTimeAfterUserEndTime( User user, User x ) {
        return LocalTime.parse( x.getWorkShift().getEndTime().toString()
                .substring( 11, x.getWorkShift().getEndTime().toString().length() ) )
                .isAfter(LocalTime.parse( user.getWorkShift().getEndTime().toString()
                        .substring( 11, x.getWorkShift().getEndTime().toString().length() ) ) );
    }

    /**
     * This method checks if the user start time is
     * before partner start time
     * @param user your information to be extracted
     * @param x stream of userList
     * @return true if user start time is before partner start time
     */
    private boolean isPartnerStartTimeBeforeUserStartTime( User user, User x ) {
        return LocalTime.parse( x.getWorkShift().getStartTime().toString()
                .substring( 11, x.getWorkShift().getStartTime().toString().length() ) )
                .isBefore(LocalTime.parse( user.getWorkShift().getStartTime().toString()
                        .substring( 11, x.getWorkShift().getStartTime().toString().length() ) ) );
    }

}
