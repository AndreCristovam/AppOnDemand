package com.example.ondemandflix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ondemandflix.model.Movie;
import com.example.ondemandflix.model.MovieDatail;
import com.example.ondemandflix.util.ImageDownloaderTask;
import com.example.ondemandflix.util.MovieDatailTask;

import java.util.ArrayList;
import java.util.List;


public class MovieActivity extends AppCompatActivity implements MovieDatailTask.MovieDetailLoader {

    private TextView txtTitle;
    private TextView txtDesc;
    private TextView txtCast;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private ImageView imgCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setSupportActionBar(findViewById(R.id.toolbar));

        txtTitle = findViewById(R.id.text_view_title);
        txtDesc = findViewById(R.id.text_view_desc);
        txtCast = findViewById(R.id.text_view_cast);
        recyclerView = findViewById(R.id.recycler_view_similar);
        imgCover = findViewById(R.id.image_view_cover);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            getSupportActionBar().setTitle(null);
        }


        List<Movie> movies = new ArrayList<>();
        movieAdapter = new MovieAdapter(movies);
        recyclerView.setAdapter(movieAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // Instanciando os detalhes dos filmes
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id = extras.getInt("id");
            MovieDatailTask movieDatailTask = new MovieDatailTask(this);
            movieDatailTask.setMovieDetailLoader(this);
            movieDatailTask.execute("https://tiagoaguiar.co/api/netflix/" + id);

        }
    }

    // Botão de Voltar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(MovieDatail movieDatail) {
        txtTitle.setText(movieDatail.getMovie().getTitle());
        txtDesc.setText(movieDatail.getMovie().getDesc());
        txtCast.setText(movieDatail.getMovie().getCast());

        ImageDownloaderTask imageDownloaderTask = new ImageDownloaderTask(imgCover);
        imageDownloaderTask.setShadowEnabled(true);
        imageDownloaderTask.execute(movieDatail.getMovie().getCoverUrl());

        movieAdapter.setMovies(movieDatail.getMoviesSimliar());
        movieAdapter.notifyDataSetChanged();
    }

    // ViewHolder dos Similares
    private static class MovieHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewCover;

        public MovieHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCover = itemView.findViewById(R.id.image_view_cover);
        }

    }

    // Adapter dos Similares
    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {
        // Lista de Referencia para utilizar no metodo onBindViewHolder
        final List<Movie> movies;

        private MovieAdapter(List<Movie> movies){
            this.movies = movies;
        }

        public void setMovies(List<Movie> movies){
            this.movies.clear();
            this.movies.addAll(movies);
        }

        @NonNull
        @Override
        // Infla o Layout
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MovieHolder(getLayoutInflater().inflate(R.layout.movie_item_similar, parent, false));
        }

        // Busca o Item da Celula
        @Override
        public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
            Movie movie = movies.get(position);
            new ImageDownloaderTask(holder.imageViewCover).execute(movie.getCoverUrl());
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }
    }
}