package city.bilboard.aryasoft.com.bilboardcity.Activities;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import city.bilboard.aryasoft.com.bilboardcity.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InfoActivity extends AppCompatActivity
{
    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_info);
        TextView info_text = findViewById(R.id.info_text);
        if (getIntent().getIntExtra("info_type", -1) == 3)
        {
            //Owner_help
            info_text.setText(getString(R.string.Owner_help));
        }
        else if (getIntent().getIntExtra("info_type", -1) == 4)
        {
            //sp_user_help
            info_text.setText(getString(R.string.SpecialUser_help));
        }

        setFooterBgAnimation();
    }

    private void setFooterBgAnimation()
    {
        final ImageView img_about_cloud1 = findViewById(R.id.img_about_cloud1);
        final ImageView img_about_cloud2 = findViewById(R.id.img_about_cloud2);
        Picasso.get().load(R.drawable.about_cloud).into(img_about_cloud1);
        Picasso.get().load(R.drawable.about_cloud).into(img_about_cloud2);
        Handler AnimHandler = new Handler();
        Thread AnimThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
                animator.setRepeatCount(-1);
                animator.setInterpolator(new LinearInterpolator());
                animator.setDuration(25000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {

                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        float width = (float) img_about_cloud1.getWidth();
                        float translationX = width * ((Float) animation.getAnimatedValue()).floatValue();
                        img_about_cloud1.setTranslationX(-translationX);
                        img_about_cloud2.setTranslationX(width - translationX);
                    }
                });
                animator.start();
            }
        });
        AnimHandler.post(AnimThread);

    }
}
