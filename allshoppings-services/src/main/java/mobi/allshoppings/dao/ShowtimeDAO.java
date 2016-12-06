package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Showtime;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

import com.inodes.datanucleus.model.Key;

public interface ShowtimeDAO extends GenericDAO<Showtime> {

	Key createKey(String identifier) throws ASException;
	
	List<Showtime> getUsingCinemaAndDateAndStatusAndRange(String cinemaId, Date date, List<Integer> status, Range range, String order) throws ASException;
	List<Showtime> getUsingCinemaAndDateAndStatusAndRange(PersistenceProvider pp, String cinemaId, Date date, List<Integer> status, Range range, String order, boolean detachable) throws ASException;
	List<Showtime> getUsingCinemaAndMovieAndDate(String cinemaId, String movieId, String showDate, String showTime) throws ASException;
	List<Showtime> getUsingCinemaAndMovieAndDate(PersistenceProvider pp, String cinemaId, String movieId, String showDate, String showTime, boolean detachable) throws ASException;

}
