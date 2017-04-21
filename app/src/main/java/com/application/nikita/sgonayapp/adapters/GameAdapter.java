package com.application.nikita.sgonayapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.application.nikita.sgonayapp.R;
import com.application.nikita.sgonayapp.entities.Game;
import static com.application.nikita.sgonayapp.app.AppConfig.*;

import java.util.ArrayList;

/**
 * Created by Konstantin on 02.04.2017.
 */

public class GameAdapter extends BaseAdapter {

    Context context;
    ArrayList<Game> games;
    LayoutInflater inflater;

    public GameAdapter(Context context, ArrayList<Game> games){
        this.context = context;
        this.games = games;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public Object getItem(int position) {
        return games.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view  = convertView;
        if (view == null) {
            view = this.inflater.inflate(R.layout.game_item, parent, false);
        }

        Game g = getGame(position);

        ((TextView) view.findViewById(R.id.gameName)).setText(SGONAY_TEXT + g.getNumber());
        ((TextView) view.findViewById(R.id.gameDate)).setText(g.getDate());
        ((TextView) view.findViewById(R.id.gameTimeOut)).setText(g.getTimeOut() + MINUTES_TEXT);
        ((TextView) view.findViewById(R.id.gameTitle)).setText(g.getTitle());

        return view;
    }

    Game getGame(int position) {
        return ((Game) getItem(position));
    }
}
