package com.efsoft.hangmedia.image_slider;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class SliderIndicator implements ViewPager.OnPageChangeListener {

    private Context context;
    private LinearLayout container;
    private int drawable, spacing, size, pageCount;
    private ViewPager viewPager;
    private int initialPage = 0;

    public SliderIndicator(@NonNull Context context,
                           @NonNull LinearLayout container,
                           @NonNull ViewPager viewPager,
                           @DrawableRes int drawable){
        if (viewPager.getAdapter() == null){
            throw new IllegalArgumentException("ViewPager does not have an adapter set on it.");
        }

        this.context = context;
        this.container = container;
        this.drawable = drawable;
        this.viewPager = viewPager;
    }

    public void setPageCount(int pageCount){
        this.pageCount = pageCount;
    }

    public void setInitialPage(int initialPage) {
        this.initialPage = initialPage;
    }

    public void setDrawable(@DrawableRes int drawable) {
        this.drawable = drawable;
    }

    public void setSpacing(@DimenRes int spacing) {
        this.spacing = spacing;
    }

    public void setSize(@DimenRes int size) {
        this.size = size;
    }

    public void show(){
        initIndicators();
        setIndicatorAsSelected(initialPage);
        new Handler().postDelayed(() -> viewPager.setCurrentItem(1), 2500);
    }

    private void initIndicators(){

        viewPager.addOnPageChangeListener(this);
        Resources res = context.getResources();
        container.removeAllViews();

        for (int i = 0; i < pageCount; i++){
            View view = new View(context);
            int defaultSizeInDp = 12;
            int dimen = size != 0 ? res.getDimensionPixelSize(size) : ((int) res.getDisplayMetrics().density * defaultSizeInDp);
            int defaulSpacingInDp = 12;
            int margin = spacing != 0 ? res.getDimensionPixelSize(spacing) : ((int) res.getDisplayMetrics().density * defaulSpacingInDp);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dimen, dimen);
            layoutParams.setMargins(i == 0 ? 0 : margin, 0,0,0);
            view.setLayoutParams(layoutParams);
            view.setBackgroundResource(drawable);
            view.setSelected(i == 0);
            container.addView(view);
        }
    }

    private void setIndicatorAsSelected(int index){
        for (int i = 0; i < container.getChildCount(); i++){
            View view = container.getChildAt(i);
            view.setSelected(i == index);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int index = position % pageCount;
        setIndicatorAsSelected(index);

        final int moveTo = position + 1;
        try {
            new Handler().postDelayed(() -> viewPager.setCurrentItem(moveTo), 2500);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void cleanup(){
        viewPager.clearOnPageChangeListeners();
    }
}
