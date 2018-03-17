package com.google.developer.colorvalue.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.developer.colorvalue.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private Cursor mCursor;
    private CardAdapterCallbacks mCallback;

    public interface CardAdapterCallbacks{
        void onCardClicked(Card clickedCard);
    }

    public CardAdapter(CardAdapterCallbacks mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public void onBindViewHolder(CardAdapter.ViewHolder holder, int position) {

        // bind data to view
        mCursor.moveToPosition(position);
        String hexValue = mCursor.getString(mCursor.getColumnIndex(CardProvider.Contract.Columns.COLOR_HEX));

        holder.name.setText(hexValue);
        holder.itemView.setBackgroundColor(Color.parseColor(hexValue));
    }

    @Override
    public int getItemCount() {
        if(mCursor==null){
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * Return a {@link Card} represented by this item in the adapter.
     * Method is used to run machine tests.
     *
     * @param position Cursor item position
     * @return A new {@link Card}
     */
    public Card getItem(int position) {
        if (mCursor.moveToPosition(position)) {
            return new Card(mCursor);
        }
        return null;
    }

    /**
     * @param data update cursor
     */
    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
    }

    /**
     * An Recycler item view
     */
    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        @BindView(R.id.color_name)
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }


        @Override
        public void onClick(View view) {

            int position  = getAdapterPosition();
            Card thisCard = getItem(position);
            mCallback.onCardClicked(thisCard);

        }
    }
}
