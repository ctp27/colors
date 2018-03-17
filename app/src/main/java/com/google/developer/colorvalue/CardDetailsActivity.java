package com.google.developer.colorvalue;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.developer.colorvalue.data.Card;
import com.google.developer.colorvalue.data.CardProvider;
import com.google.developer.colorvalue.ui.ColorView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardDetailsActivity extends AppCompatActivity {

    public static final String INTENT_PARCEL_EXTRA = "card_detail_extra_object";

    private Card thisCard;

    @BindView(R.id.card_details_color_view)
    ColorView colorView;

    @BindView(R.id.card_details_color_name)
    TextView colorNameView;

    @BindView(R.id.card_details_color_hex)
    TextView colorHexView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        ButterKnife.bind(this);

        Intent receivedIntent = getIntent();
        if(receivedIntent.hasExtra(INTENT_PARCEL_EXTRA)){
            thisCard = receivedIntent.getParcelableExtra(INTENT_PARCEL_EXTRA);
        }

        colorView.setBackgroundColor(thisCard.getColorInt());
        colorNameView.setText(thisCard.getName());
        colorHexView.setText(thisCard.getHex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.action_delete:
                Uri uri = ContentUris.withAppendedId(CardProvider.Contract.CONTENT_URI,thisCard.getID());
                getContentResolver().delete(uri,null,null);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
