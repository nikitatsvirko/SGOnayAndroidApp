package com.application.nikita.sgonayapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.application.nikita.sgonayapp.R;
import com.application.nikita.sgonayapp.activities.GamesActivity;
import com.application.nikita.sgonayapp.entities.Game;
import static com.application.nikita.sgonayapp.app.AppConfig.*;

import java.util.ArrayList;

/**
 * Created by Konstantin on 02.04.2017.
 */

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameHolder> {

    public interface OnItemClickListener {
        void onItemClick(Game game);
    }

    private Context mContext;
    private ArrayList<Game> mGames;
    private final OnItemClickListener mListener;

    public GameAdapter(Context context, ArrayList<Game> games, OnItemClickListener listener) {
        mContext = context;
        mGames = games;
        mListener = listener;
    }

    @Override
    public GameHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.game_item, parent, false);
        return new GameHolder(view);
    }

    @Override
    public void onBindViewHolder(GameHolder holder, int position) {
        Game game = mGames.get(position);
        holder.bindGame(game, mListener);
    }

    @Override
    public int getItemCount() {
        return mGames.size();
    }

    class GameHolder extends RecyclerView.ViewHolder {

        private TextView mGameNameTextView;
        private TextView mGameDateTextView;
        private TextView mGameTimeOutTextView;
        private TextView mGameTitleTextView;

        private Game mGame;

        public void bindGame(final Game game, final OnItemClickListener listener) {
            mGame = game;
            mGameNameTextView.setText(mContext.getResources().getString(R.string.sgonay_text, mGame.getNumber()));
            mGameDateTextView.setText(mGame.getDate());
            if (mGame.getScheme().equals("3")) {
                mGameTimeOutTextView.setText("");
            } else {
                mGameTimeOutTextView.setText(mContext.getResources().getString(R.string.minutes_text, mGame.getTimeOut()));
            }
            mGameTitleTextView.setText(mGame.getTitle());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(game);
                }
            });
        }

        public GameHolder(View itemView) {
            super(itemView);

            mGameNameTextView = (TextView) itemView.findViewById(R.id.game_name_text_view);
            mGameDateTextView = (TextView) itemView.findViewById(R.id.game_date_text_view);
            mGameTimeOutTextView = (TextView) itemView.findViewById(R.id.game_time_out_text_view);
            mGameTitleTextView = (TextView) itemView.findViewById(R.id.game_title_text_view);
        }
    }
}

