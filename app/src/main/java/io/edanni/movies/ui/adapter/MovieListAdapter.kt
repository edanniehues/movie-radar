package io.edanni.movies.ui.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.edanni.movies.R
import io.edanni.movies.infrastructure.api.dto.Movie
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import java.text.DateFormat
import java.util.*

/**
 * Created by eduardo on 20/11/2017.
 */
class MovieListAdapter(private val context: Context) : BaseAdapter() {
    var movies: List<Movie> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var movieClickHandler: (Movie) -> Unit = {}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout: ViewGroup = convertView as ViewGroup? ?: (inflater.inflate(R.layout.movie_list_item, parent, false) as ViewGroup)
        layout.setOnClickListener { onMovieClick(movies[position]) }
        bindView(movies[position], layout)
        return layout
    }

    override fun getItem(i: Int): Any = movies[i]

    override fun getItemId(i: Int): Long = i.toLong()

    override fun getCount(): Int = movies.size

    private fun bindView(movie: Movie, layout: ViewGroup) {
        val image = layout.findViewById<ImageView>(R.id.moviePoster)
        val movieTitle = layout.findViewById<TextView>(R.id.movieTitle)
        val genre = layout.findViewById<TextView>(R.id.genre)
        val releaseDate = layout.findViewById<TextView>(R.id.releaseDate)
        if (movie.posterPath != null) {
            Picasso.with(context).load(movie.posterPath).into(image)
            movieTitle.visibility = View.INVISIBLE
        } else {
            movieTitle.visibility = View.VISIBLE
            image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.poster_placeholder))
        }
        movieTitle.text = movie.title.toUpperCase()
        genre.text = movie.genres.firstOrNull()?.name ?: ""

        if (movie.releaseDate != null) {
            val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
            val releaseDateAsDate = DateTimeUtils.toDate(movie.releaseDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
            if (movie.releaseDate.isAfter(LocalDate.now())) {
                releaseDate.text = context.resources.getString(R.string.movie_releases_at, dateFormat.format(releaseDateAsDate))
            } else {
                releaseDate.text = context.resources.getString(R.string.movie_released_in, dateFormat.format(releaseDateAsDate))
            }
        } else {
            releaseDate.text = ""
        }
    }

    private fun onMovieClick(movie: Movie) = movieClickHandler(movie)

    interface MovieClickHandler {
        fun clicked(movie: Movie)
    }
}