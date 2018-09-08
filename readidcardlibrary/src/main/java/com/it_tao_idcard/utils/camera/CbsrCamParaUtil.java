package com.it_tao_idcard.utils.camera;

import android.hardware.Camera.Size;
import android.util.Log;

import com.it_tao_idcard.utils.LogUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CbsrCamParaUtil {
	 private static final String tag = "authen";  
	    private CameraSizeComparator sizeComparator = new CameraSizeComparator();  
	    private static CbsrCamParaUtil myCamPara = null;  
	    private CbsrCamParaUtil(){  
	          
	    }  
	    public static CbsrCamParaUtil getInstance(){  
	        if(myCamPara == null){  
	            myCamPara = new CbsrCamParaUtil();  
	            return myCamPara;  
	        }  
	        else{  
	            return myCamPara;  
	        }  
	    }  
	      
	    public Size getPictureSize(List<Size> list, int th){  
	        Collections.sort(list, sizeComparator);  
	          
	        int i = 0;  
	        for(Size s:list){
	        	i++;
	            if((s.width > th) && equalRate(s, 1.33f)){
//	                Log.i(tag, "最终设置图片尺寸:w = " + s.width + "h = " + s.height);
	                break;  
	            }
				Log.i(tag, "最终设置图片尺寸:w = " + s.width + "h = " + s.height);
	        }  
	  
	        return list.get(i - 1);  
	    }
	    
	    public  Size getPreviewSizeOfToCapture(List<Size> list, int th){
	        Collections.sort(list, sizeComparator);  
	          
	        int i = 0;  
	        for(Size s:list){ 
	        	i++; 
	            if((s.width > th) && equalRate(s, 1.58f)){
	                LogUtil.d(tag, "最终设置预览尺寸:w = " + s.width + "h = " + s.height);
	                break;  
	            }      
	        }  
	  
	        return list.get(i - 1);  
	    }
	public  Size getPreviewSize(List<Size> list, int th){
	        Collections.sort(list, sizeComparator);

	        int i = 0;
	        for(Size s:list){
	        	i++;
	            if((s.width > th) && equalRate(s, 1.33f)){
	                LogUtil.d(tag, "活体最终设置预览尺寸:w = " + s.width + "h = " + s.height);
	                break;
	            }
	        }

	        return list.get(i - 1);
	    }
	    
	    
	   /* public Size getPreviewSize(List<Camera.Size> list, int th, int  mh, float rate){  
	        Collections.sort(list, sizeComparator);  
	          
	        int i = 0;  
	        for(Size s:list){ 
	        	i++; 
	        	Log.i(tag, "支持预览尺寸:w = " + s.width + "h = " + s.height);  
	            if((s.width >= th) && equalRate(s, rate)){  
	                Log.i(tag, "最终设置预览尺寸:w = " + s.width + "h = " + s.height);  
	                break;  
	            }
	        }  
	        return list.get(i - 1);  
	        
	        for(Size s:list){ 
	        	Log.i(tag, "支持预览尺寸:w = " + s.width + "h = " + s.height);  
	            if((s.width <= th) && (s.width >= mh) && equalRate(s, rate)){  
	                Log.i(tag, "最终设置预览尺寸:w = " + s.width + "h = " + s.height);  
	        d        return s;
	            }
	        }
	        Size maxSize = list.get(0);
	        for(Size s:list){ 
	        	if(s.width <= th){
	        		if(maxSize.width < s.width && s.width%20 == 0 && s.height%20 == 0){
	        			maxSize = s;
	        		}
	        	}
	        }
	        return maxSize;
	    } */
	    
	    public Size getPictureSize(List<Size> list, int th, int  mh, float rate){  
	        Collections.sort(list, sizeComparator);  
	          
	       /* int i = 0;  
	        for(Size s:list){
	        	i++;
	            if((s.width >= th) && equalRate(s, rate)){  
	                Log.i(tag, "最终设置图片尺寸:w = " + s.width + "h = " + s.height);  
	                break;  
	            }         
	        }    
	        return list.get(i - 1); */ 
	        
	        for(Size s:list){ 
	        	Log.i(tag, "支持预览尺寸:w = " + s.width + "h = " + s.height);  
	            if((s.width <= th) && (s.width >= mh) && equalRate(s, rate)){  
	                Log.i(tag, "最终设置预览尺寸:w = " + s.width + "h = " + s.height);  
	                return s; 
	            }
	        }
	        Size maxSize = list.get(0);
	        for(Size s:list){ 
	        	if(s.width <= th){
	        		if(maxSize.width < s.width && s.width%20 == 0 && s.height%20 == 0){
	        			maxSize = s;
	        		}
	        	}
	        }
	        return maxSize;
	    }  
	    /**
	     *
	     * @param sizes 相机的尺寸集合
	     * @param w 屏幕的宽度
	     * @param h 屏幕的高度
	     * @return
	     */
	    public Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
	        final double ASPECT_TOLERANCE = 0.1;
	        double targetRatio = (double) w / h;
//				Toast.makeText(CCR_SDK_Camera.this,"比例："+targetRatio+";宽"+w+";高"+h,Toast.LENGTH_LONG).show();
//				double targetRatio = 1.33;
	        if (sizes == null)
	            return null;
	        Size optimalSize = null;
	        double minDiff = Double.MAX_VALUE;

	        int targetHeight = h;

	        // Try to find an size match aspect ratio and size
	        for (Size size : sizes) {
	            double ratio = (double) size.width / size.height;
	            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
	                continue;
	            if (Math.abs(size.height - targetHeight) < minDiff) {
	                optimalSize = size;
	                minDiff = Math.abs(size.height - targetHeight);
	            }
	        }

	        // Cannot find the one match the aspect ratio, ignore the
	        // requirement
	        if (optimalSize == null) {
	            minDiff = Double.MAX_VALUE;
	            for (Size size : sizes) {
	                if (Math.abs(size.height - targetHeight) < minDiff) {
	                    optimalSize = size;
	                    minDiff = Math.abs(size.height - targetHeight);
	                }
	            }
	        }
	        return optimalSize;
	    }
	    public boolean equalRate(Size s, float rate){  
	        float r = (float)(s.width)/(float)(s.height);  
	        if(Math.abs(r - rate) <= 0.2)
	        {  
	            return true;  
	        }  
	        else{  
	            return false;  
	        }  
	    } 
	      
	    public  class CameraSizeComparator implements Comparator<Size>{  
	        //按升序排列  
	        public int compare(Size lhs, Size rhs) {  
	            // TODO Auto-generated method stub  
	            if(lhs.width == rhs.width){  
	            return 0;  
	            }  
	            else if(lhs.width > rhs.width){  
	                return 1;  
	            }  
	            else{  
	                return -1;  
	            }  
	        }  
	          
	    }  
}
