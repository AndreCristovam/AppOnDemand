package com.example.ondemandflix.model;

import java.util.List;

public class MovieDatail {

    private final Movie movie;
    private final List<Movie> moviesSimliar;

    public MovieDatail(Movie movie, List<Movie> moviesSimliar) {
        this.movie = movie;
        this.moviesSimliar = moviesSimliar;
    }

    public Movie getMovie() {
        return movie;
    }

    public List<Movie> getMoviesSimliar() {
        return moviesSimliar;
    }
}
