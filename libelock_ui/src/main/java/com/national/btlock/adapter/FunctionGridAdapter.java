package com.national.btlock.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import com.national.btlock.model.AppItem;
import com.national.btlock.ui.R;
//import com.national.ecode.entity.AppItemListEntity.AppItem;

import java.text.DecimalFormat;
import java.util.List;

public class FunctionGridAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater layoutInflater;
	
//	private LruCache<String,Bitmap=""> mLruCache;

//	private LruCache<String, Bitmap>mLruCache;
	private List<AppItem> mList;
	DecimalFormat decimalFormat ;
	public FunctionGridAdapter(Context context , List<AppItem> list) {
		mContext = context;
		mList =  list;
		layoutInflater = LayoutInflater.from(context);

		  decimalFormat = new DecimalFormat(".00");//
		  
		 //应用程序最大可用内存
//        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        //设置图片缓存大小为maxMemory的1/3
//        int cacheSize = maxMemory/3;

//        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
//            @Override
//            protected int sizeOf(String key, Bitmap bitmap) {
//                return bitmap.getRowBytes() * bitmap.getHeight();
//            }
//        };

	}

	@Override
	public int getCount() {
		return this.mList.size();
	}

	@Override
	public Object getItem(int i) {

		return null;
	}

	@Override
	public boolean isEnabled(int position) {
		AppItem item =  mList.get(position);
		if(TextUtils.isEmpty(item.getAppName())){
			return false;
		}
		return true;
//		return super.isEnabled(position);
	}

	
	@Override
	public long getItemId(int postion) {
		return postion;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Holder holder = null;
		
		
		if (null == convertView) {
			holder = new Holder();
			convertView = layoutInflater.inflate(R.layout.app_national_thirdpart_app_gridview_item, null);


			holder.imgvIcon = (ImageView) convertView.findViewById(R.id.id_gridview_item_icon);
			holder.tvAppItemName = (TextView)convertView.findViewById(R.id.id_gridview_room_name);
//			holder.tvAppItemName.setVisibility(View.VISIBLE);
//			holder.tvAppItemPrice = (TextView)convertView.findViewById(R.id.id_gridview_room_price);
//			holder.rlRoot = (RelativeLayout)convertView.findViewById(R.id.rl_gridview_room_root);
			
			
			convertView.setTag(holder);
			
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		AppItem item =  mList.get(position);//(File) getItem(position);
		


		holder.tvAppItemName.setText(item.getAppName());
		holder.imgvIcon.setBackgroundResource(item.getResId());


//		if(TextUtils.isEmpty(item.getAppName())){
//
//			 RelativeLayout.LayoutParams layoutParams =(RelativeLayout.LayoutParams) convertView.getLayoutParams();
//			if(layoutParams!=null){
//				layoutParams.height=0;
//				convertView.setLayoutParams(layoutParams);
//			}
//
//
//		}
		return convertView;
	}
	
//	   /**
//     * 将图片存储到LruCache
//     */
//    public void addBitmapToLruCache(String key, Bitmap bitmap) {
//        if (getBitmapFromLruCache(key) == null) {
//            mLruCache.put(key, bitmap);
//        }
//    }
//	
//	/**
//     * 从LruCache缓存获取图片
//     */
//    public Bitmap getBitmapFromLruCache(String key) {
//        return mLruCache.get(key);
//    }
    

//	private Integer[] mThumbIds = {// 显示的图片数组
//
//	R.drawable.ic_close_24px, R.drawable.ic_document_file_48px, R.drawable.ic_settings_24px,
//			 };
	
	class Holder {
		private ImageView imgvIcon;
		private TextView tvAppItemName;
//		private TextView tvAppItemPrice;
//		private RelativeLayout rlRoot;
		
		
		
		}

}