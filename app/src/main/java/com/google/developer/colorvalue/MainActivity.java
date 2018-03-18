package com.google.developer.colorvalue;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.developer.colorvalue.data.Card;
import com.google.developer.colorvalue.data.CardAdapter;
import com.google.developer.colorvalue.data.CardProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
                implements LoaderManager.LoaderCallbacks<Cursor>,
                CardAdapter.CardAdapterCallbacks{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CURSOR_LOADER_ID=2011;

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.recycler_empty_text)
    TextView recyclerEmptyView;

    private CardAdapter mCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);

        mCardAdapter = new CardAdapter(this);
        recycler.setAdapter(mCardAdapter);
        recycler.setHasFixedSize(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                : Launch add activity
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID,null,this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id){
            case CURSOR_LOADER_ID:
                return new CursorLoader(this, CardProvider.Contract.CONTENT_URI,
                        null,null,null,null);
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        int id = loader.getId();

        if(id == CURSOR_LOADER_ID){

            if(data!=null && data.getCount()>0) {

                hideDefaultMessage();
                mCardAdapter.swapCursor(data);
            }
            else {
                displayDefaultMessage();
            }

        }

    }

    private void hideDefaultMessage() {
        recycler.setVisibility(View.VISIBLE);
        recyclerEmptyView.setVisibility(View.INVISIBLE);
    }

    private void displayDefaultMessage() {
        recyclerEmptyView.setVisibility(View.VISIBLE);
        recycler.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCardAdapter.swapCursor(null);
    }


    @Override
    public void onCardClicked(Card clickedCard) {
//        : Launch new activity
        Log.d(TAG, clickedCard.getName());
        Intent intent = new Intent(this,CardActivity.class);
        intent.putExtra(CardActivity.INTENT_PARCEL_EXTRA,clickedCard);
        startActivity(intent);

    }



}
