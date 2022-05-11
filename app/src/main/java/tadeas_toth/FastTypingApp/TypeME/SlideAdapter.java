package tadeas_toth.FastTypingApp.TypeME;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    // list of images
    public int[] lst_images = {
            R.drawable.music_white_bg,
            R.drawable.settings_white_bg,
            R.drawable.image_3,
            R.drawable.image_4
    };

    // list of titles
    public String[] lst_title = {
            "MUSIC",
            "SETTINGS",
            "RANDOM WORD",
            "TIME LEFT"
    };

    // list of descriptions
    public String[] lst_description = {
            "This button allows you to mute the music, simply press on it and the music stops, press again and the music starts.",
            "This button works as a pause button, via this button's menu, you can access this tutorial whenever you like, or simply just pause the game.",
            "Random Word that is displayed on your display, you're supposed to type this word as fast as you can, so you can move to the next word and higher level.",
            "Time which is left for this round, each successful word adds 1 second, every 10 levels you get 5 bonus seconds."
    };

    public SlideAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return lst_title.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide, container, false);

        final ConstraintLayout layoutslide = view.findViewById(R.id.slidelinearlayout);
        layoutslide.setVisibility(View.VISIBLE);

        ImageView imgslide = (ImageView) view.findViewById(R.id.slideimg);
        TextView txttitle = (TextView) view.findViewById(R.id.txttitle);
        TextView description = (TextView) view.findViewById(R.id.txtdescription);

        imgslide.setImageResource(lst_images[position]);
        txttitle.setText(lst_title[position]);
        description.setText(lst_description[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}