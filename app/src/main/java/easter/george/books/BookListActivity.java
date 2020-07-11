package easter.george.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity {
private ProgressBar mLoadingProgress;
private RecyclerView rvBooks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        rvBooks = (RecyclerView) findViewById(R.id.rv_books);
        LinearLayoutManager booksLayoutManageer= new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false);
        rvBooks.setLayoutManager( booksLayoutManageer);
        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading);
        try {
            URL bookURL = ApiUtil.buildUrl("cooking");
           new BooksQueryTask().execute(bookURL);
        } catch (Exception e) {
            Log.d("error", e.getMessage());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.book_list_menu,menu);
       final MenuItem searchItem = menu.findItem(R.id.action_search);
       final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
       searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
        return true;
    }
    public  class BooksQueryTask extends AsyncTask<URL,Void,String>{

        @Override
        protected String doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String result = null;
            try {
                result = ApiUtil.getJson(searchURL);
            }catch (IOException e){
                Log.e("error", e.getMessage());
            }
            return  result;
        }

        @Override
        protected void onPostExecute(String result) {

            TextView tvError = (TextView)findViewById(R.id.tvError);
            if (result==null){
                rvBooks.setVisibility(View.INVISIBLE);
                tvError.setVisibility(View.VISIBLE);
            }
            else {
               rvBooks.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.INVISIBLE);
            }
            mLoadingProgress.setVisibility(View.INVISIBLE);
            ArrayList<Book> books = ApiUtil.getBooksFromJson(result);
            String resultString ="";

            BooksAdapter adapter = new BooksAdapter(books);
            rvBooks.setAdapter(adapter);
            //tvResult.setText(resultString);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgress.setVisibility(View.VISIBLE);
        }
    }
}