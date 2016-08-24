package ss.bupt.fragmentbestpractice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by new on 2016/8/4.
 */
public class NewsAdapter extends ArrayAdapter {

    private int resourceId;

    public NewsAdapter(Context context, int textViewResourceId, List<News> object) {
        super(context, textViewResourceId, object);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News news = (News) getItem(position);
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        } else {
            view = convertView;
        }
        TextView newsTitleText = (TextView) view.findViewById(R.id.news_title);
        newsTitleText.setText(news.getTitle());
        return view;
    }
}
