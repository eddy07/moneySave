package com.parse.app.utilities;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.parse.app.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ImageIndicatorView extends RelativeLayout {

	private ViewPager viewPager;

	private LinearLayout indicateLayout;
	
	private List<View> viewList = new ArrayList<View>();

	private Handler refreshHandler;

	private OnItemChangeListener onItemChangeListener;


	private OnItemClickListener onItemClickListener;

	private int totelCount = 0;
	
	private int currentIndex = 0;


	public static final int INDICATE_ARROW_ROUND_STYLE = 0;

	
	public static final int INDICATE_USERGUIDE_STYLE = 1;

	
	private int indicatorStyle = INDICATE_ARROW_ROUND_STYLE;

	
	private long refreshTime = 0l;

	
	public interface OnItemChangeListener {
		void onPosition(int position, int totalCount);
	}

	
	public interface OnItemClickListener {
		void OnItemClick(View view, int position);
	}

	public ImageIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context);
	}

	public ImageIndicatorView(Context context) {
		super(context);
		this.init(context);
	}

	/**
	 * @param context
	 */
	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.image_indicator_layout, this);
		this.viewPager = (ViewPager) findViewById(R.id.view_pager);
		this.indicateLayout = (LinearLayout) findViewById(R.id.indicater_layout);

		this.viewPager.setOnPageChangeListener(new PageChangeListener());

		this.refreshHandler = new ScrollIndicateHandler(ImageIndicatorView.this);
	}

	
	public ViewPager getViewPager() {
		return viewPager;
	}


	public int getCurrentIndex() {
		return this.currentIndex;
	}

	
	public int getTotalCount() {
		return this.totelCount;
	}

	
	public long getRefreshTime() {
		return this.refreshTime;
	}


	public void addViewItem(View view) {
		final int position = viewList.size();
		view.setOnClickListener(new ItemClickListener(position));
		this.viewList.add(view);
	}


	private class ItemClickListener implements OnClickListener {
		private int position = 0;

		public ItemClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View view) {
			if (onItemClickListener != null) {
				onItemClickListener.OnItemClick(view, position);
			}
		}
	}


	public void setupLayoutByDrawable(final Integer resArray[]) {
		if (resArray == null)
			throw new NullPointerException();

		this.setupLayoutByDrawable(Arrays.asList(resArray));
	}

	public void setupLayoutByDrawable(final List<Integer> resList) {
		if (resList == null)
			throw new NullPointerException();

		final int len = resList.size();
		if (len > 0) {
			for (int index = 0; index < len; index++) {
				final View pageItem = new ImageView(getContext());
				pageItem.setBackgroundResource(resList.get(index));
				addViewItem(pageItem);
			}
		}
	}


	public void setCurrentItem(int index) {
		this.currentIndex = index;
	}

	
	public void setIndicateStyle(int style) {
		this.indicatorStyle = style;
	}

	
	public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
		if (onItemChangeListener == null) {
			throw new NullPointerException();
		}
		this.onItemChangeListener = onItemChangeListener;
	}


	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	
	public void show() {
		this.totelCount = viewList.size();
		final LayoutParams params = (LayoutParams) indicateLayout.getLayoutParams();
		if (INDICATE_USERGUIDE_STYLE == this.indicatorStyle) {
			params.bottomMargin = 45;
		}
		this.indicateLayout.setLayoutParams(params);
		
		for (int index = 0; index < this.totelCount; index++) {
			final View indicater = new ImageView(getContext());
			this.indicateLayout.addView(indicater, index);
		}
		this.refreshHandler.sendEmptyMessage(currentIndex);
		
		this.viewPager.setAdapter(new MyPagerAdapter(this.viewList));
		this.viewPager.setCurrentItem(currentIndex, false);
	}


	private class PageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int index) {
			currentIndex = index;
			refreshHandler.sendEmptyMessage(index);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	}

	protected void refreshIndicateView() {
		this.refreshTime = System.currentTimeMillis();

		for (int index = 0; index < totelCount; index++) {
			final ImageView imageView = (ImageView) this.indicateLayout.getChildAt(index);
			if (this.currentIndex == index) {
				imageView.setBackgroundResource(R.drawable.image_indicator_focus);
			} else {
				imageView.setBackgroundResource(R.drawable.image_indicator);
			}
		}

		if (INDICATE_USERGUIDE_STYLE == this.indicatorStyle) {

		} else {
			
		}
		if (this.onItemChangeListener != null) {
			try {
				this.onItemChangeListener.onPosition(this.currentIndex, this.totelCount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ScrollIndicateHandler
	 */
	private static class ScrollIndicateHandler extends Handler {
		private final WeakReference<ImageIndicatorView> scrollIndicateViewRef;

		public ScrollIndicateHandler(ImageIndicatorView scrollIndicateView) {
			this.scrollIndicateViewRef = new WeakReference<ImageIndicatorView>(
					scrollIndicateView);

		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ImageIndicatorView scrollIndicateView = scrollIndicateViewRef.get();
			if (scrollIndicateView != null) {
				scrollIndicateView.refreshIndicateView();
			}
		}
	}

	private class MyPagerAdapter extends PagerAdapter {
		private List<View> pageViews = new ArrayList<View>();

		public MyPagerAdapter(List<View> pageViews) {
			this.pageViews = pageViews;
		}

		@Override
		public int getCount() {
			return pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(pageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(pageViews.get(arg1));
			return pageViews.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}

}
