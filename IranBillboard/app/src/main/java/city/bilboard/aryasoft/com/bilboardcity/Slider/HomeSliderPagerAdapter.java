package city.bilboard.aryasoft.com.bilboardcity.Slider;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels.SliderModel;
import city.bilboard.aryasoft.com.bilboardcity.R;
import city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle.HelperModule;

public class HomeSliderPagerAdapter extends PagerAdapter
{
    Context context;
    ArrayList<SliderModel> ImageList;
    LayoutInflater mLayoutInflater;

    public HomeSliderPagerAdapter(Context context, ArrayList<SliderModel> ImageList)
    {
        this.context = context;
        mLayoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.ImageList = ImageList;
    }

    @Override
    public int getCount()
    {
        return ImageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        View itemView = mLayoutInflater.inflate(R.layout.home_image_slider_layout, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.img_slider);
        TextView txt1_slider = (TextView) itemView.findViewById(R.id.txt_slider);
        //------------------------
        //imageView.setImageDrawable(ImageList.get(position));
        //Drawable j=ImageList.get(position);
        Picasso.get().load(context.getString(R.string.SliderFolderName)+ImageList.get(position).ImageName).into(imageView);
        txt1_slider.setText(ImageList.get(position).SliderTitle1);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((RelativeLayout) object);
    }

}
