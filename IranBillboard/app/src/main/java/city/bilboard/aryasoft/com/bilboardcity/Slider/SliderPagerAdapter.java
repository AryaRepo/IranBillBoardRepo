package city.bilboard.aryasoft.com.bilboardcity.Slider;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;

public class SliderPagerAdapter extends PagerAdapter {
    private Context context ;
    private ArrayList<String> ImageList ;
    private LayoutInflater mLayoutInflater;

    public SliderPagerAdapter(Context context, ArrayList<String> ImageList) {
        this.context = context;
        mLayoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.ImageList = ImageList;
    }

    @Override
    public int getCount() {
        return ImageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.image_slider_layout, container, false);
        ImageView imageView = itemView.findViewById(R.id.img_slider);
        Picasso.get().load(context.getString(R.string.BoardImagesFolder)+ImageList.get(position)).into(imageView);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
