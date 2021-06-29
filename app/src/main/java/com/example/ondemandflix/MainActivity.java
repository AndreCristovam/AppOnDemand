package com.example.ondemandflix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.ondemandflix.model.Category;
import com.example.ondemandflix.model.Movie;
import com.example.ondemandflix.util.CategoryTask;
import com.example.ondemandflix.util.ImageDownloaderTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CategoryTask.CategoryLoader {

    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_main);

        // Lista de categorias
        List<Category> categories = new ArrayList<>();


        mainAdapter = new MainAdapter(categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(mainAdapter);

        CategoryTask categoryTask = new CategoryTask(this);
        categoryTask.setCategoryLoader(this);
        categoryTask.execute("https://tiagoaguiar.co/api/netflix/home");
    }

    @Override
    public void onResult(List<Category> categories) {
        mainAdapter.setCategories(categories);
        mainAdapter.notifyDataSetChanged();
    }

    // ViewHolder dos Filmes
    private static class MovieHolder extends RecyclerView.ViewHolder {

        final ImageView imageViewCover;

        public MovieHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            imageViewCover = itemView.findViewById(R.id.image_view_cover);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(getAdapterPosition());
                }
            });
        }

    }

    // ViewHolder das Categorias
    private  static  class CategoryHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        RecyclerView recyclerView;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            recyclerView = itemView.findViewById(R.id.recycler_view_movie);
        }
    }

    //---------------- Lógica dos Itens Horizontal -----------------//

    private class MainAdapter extends RecyclerView.Adapter<CategoryHolder> {
        // Lista de Referencia para utilizar no metodo onBindViewHolder
        private List<Category> categories;

        private MainAdapter(List<Category> categories){
            this.categories = categories;
        }

        @NonNull
        @Override
        // Infla o Layout
        public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CategoryHolder(getLayoutInflater().inflate(R.layout.category_item, parent, false));
        }

        // Busca o Item da Celula
        @Override
        public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
            Category category = categories.get(position);
            holder.textViewTitle.setText(category.getName());
            holder.recyclerView.setAdapter(new MovieAdapter(category.getMovies()));
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.HORIZONTAL, false));
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        private void setCategories(List<Category> categories) {
            this.categories.clear();
            this.categories.addAll(categories);
        }
    }

    //---------------- Lógica dos Itens Vertical -----------------//

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> implements OnItemClickListener{
        // Lista de Referencia para utilizar no metodo onBindViewHolder
        private final List<Movie> movies;

        private MovieAdapter(List<Movie> movies){
            this.movies = movies;
        }

        @Override
        public void onClick(int position) {
            if (movies.get(position).getId() <= 3) {
                Intent intent = new Intent(MainActivity.this, MovieActivity.class);
                intent.putExtra("id", movies.get(position).getId());
                startActivity(intent);
            }
        }

        @NonNull
        @Override
        // Infla o Layout
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.movie_item, parent, false);
            return new MovieHolder(view, this);
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

    interface OnItemClickListener {
        void onClick(int position);
    }
}

