package com.eshop.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

/**
 * 鏂囦欢澶勭悊杈呭姪绫�
 *   @author TangYiFeng
 */
public class FileHelper {
	
	private static String filedir = "upload";
	public static final String DOT = ".";
	
	/**
	 * 涓婁紶鏂囦欢锛岄粯璁や笂浼犵洰褰�:/upload
	 * @param file
	 * @return
	 */
	public static Record uploadFile(UploadFile file) {
		File theFile = new File(filedir);
		// 濡傛灉鏂囦欢澶逛笉瀛樺湪鍒欏垱寤�
		if (!theFile.exists() && !theFile.isDirectory()) {
			theFile.mkdir();
		}
		
		return uploadFile(file, filedir);
	}
    
    /**
     * 涓婁紶鏂囦欢
     * @param file
     * @param uploadPath 涓婁紶璺緞(鐩稿璺緞,涓嶅寘鎷枃浠跺悕)
     * @return
     */
    public static Record uploadFile(UploadFile uploadFile, String uploadPath) {
    	Record result = new Record();
    	
    	String oFileName = uploadFile.getOriginalFileName();
		File file = uploadFile.getFile();
		String newFname = UUID.randomUUID().toString() + getExtension(oFileName);
		String filePath = PathKit.getWebRootPath() + "/" + uploadPath + "/" + newFname;
		
		File t = new File(filePath);

		try {
			t.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			result.set("error", 1);
			result.set("errmsg", "涓婁紶澶辫触");
			return result;
		}
		
		fileChannelCopy(file, t);
		file.delete();
		
		String path = uploadPath + "/"+ newFname;
		
		result.set("path", path);
		result.set("error", 0);
		result.set("errmsg", "涓婁紶鎴愬姛");
		return result;
    }
    
	/**
	 * 涓婁紶鏂囦欢锛岄粯璁や笂浼犵洰褰�:/upload
	 * @param file
	 * @return
	 */
	public static Record uploadFile(String img) {
		File theFile = new File(filedir);
		// 濡傛灉鏂囦欢澶逛笉瀛樺湪鍒欏垱寤�
		if (!theFile.exists() && !theFile.isDirectory()) {
			theFile.mkdir();
		}
		
		return uploadFile(img, filedir);
	}
    
	  /**
     * 涓婁紶鏂囦欢
     * @param file
     * @param uploadPath 涓婁紶璺緞(鐩稿璺緞,涓嶅寘鎷枃浠跺悕)
     * @return
     */
    public static Record uploadFile(String imgStr, String uploadPath) {
    	Record result = new Record();
    	
    	String oFileName = ".jpg";
		String newFname = UUID.randomUUID().toString() + getExtension(oFileName);
		String filePath = PathKit.getWebRootPath() + "/" + uploadPath + "/" + newFname;
		
		try {
			 boolean flag = base64(imgStr, filePath);
			 if(!flag) {
				result.set("error", 1);
				result.set("errmsg", "涓婁紶澶辫触");
				return result;
			 }
		} catch (Exception e) {
			e.printStackTrace();
			result.set("error", 1);
			result.set("errmsg", "涓婁紶澶辫触");
			return result;
		}
	
		String path = uploadPath + "/"+ newFname;
		
		result.set("path", path);
		result.set("error", 0);
		result.set("errmsg", "涓婁紶鎴愬姛");
		return result;
    }
    
    /**
     * 瀵瑰瓧鑺傛暟缁勫瓧绗︿覆杩涜Base64瑙ｇ爜骞剁敓鎴愬浘鐗� 
     * @param imgStr
     * @return
     */
    public static boolean base64(String imgStr, String imgFilePath) {
//		  {  //瀵瑰瓧鑺傛暟缁勫瓧绗︿覆杩涜Base64瑙ｇ爜骞剁敓鎴愬浘鐗� 
//		    if (imgStr == null) //鍥惧儚鏁版嵁涓虹┖ 
//		      return false; 
//		    BASE64Decoder decoder = new BASE64Decoder(); 
//		    try 
//		    { 
//		      //Base64瑙ｇ爜 
//		      byte[] b = decoder.decodeBuffer(imgStr); 
//		      for(int i=0;i<b.length;++i) 
//		      { 
//		        if(b[i]<0) 
//		        {//璋冩暣寮傚父鏁版嵁 
//		          b[i]+=256; 
//		        } 
//		      } 
//		      //鐢熸垚jpeg鍥剧墖 
//		     // String imgFilePath = "D:\\tupian\\new.jpg";//鏂扮敓鎴愮殑鍥剧墖 
//		      OutputStream out = new FileOutputStream(imgFilePath); 
//		      out.write(b); 
//		      out.flush(); 
//		      out.close(); 
//		      return true; 
//		    }  
//		    catch (Exception e)  
//		    { 
//		      return false; 
//		    } 
//		  } 
    	return false;

	}
	
    /** 
     * 鑾峰彇鎵╁睍鍚� 
     * @param fileName 
     * @return 
     */  
    private static String getExtension(String fileName) {  
        if (StringUtils.INDEX_NOT_FOUND == StringUtils.indexOf(fileName, DOT)) {
			return StringUtils.EMPTY;
		}  
        String ext = StringUtils.substring(fileName, StringUtils.lastIndexOf(fileName, DOT));  
        return StringUtils.trimToEmpty(ext);  
    }  
    
    private static void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;

        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();// 寰楀埌瀵瑰簲鐨勬枃浠堕�氶亾
            out = fo.getChannel();// 寰楀埌瀵瑰簲鐨勬枃浠堕�氶亾
            in.transferTo(0, in.size(), out);// 杩炴帴涓や釜閫氶亾锛屽苟涓斾粠in閫氶亾璇诲彇锛岀劧鍚庡啓鍏ut閫氶亾
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 鏍规嵁璺緞鍒犻櫎鎸囧畾鐨勭洰褰曟垨鏂囦欢锛屾棤璁哄瓨鍦ㄤ笌鍚�
     * @param sPath  瑕佸垹闄ょ殑鐩綍鎴栨枃浠�
     * @return 鍒犻櫎鎴愬姛杩斿洖 true锛屽惁鍒欒繑鍥瀎alse銆�
     */
    public static boolean delete(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 鍒ゆ柇鐩綍鎴栨枃浠舵槸鍚﹀瓨鍦�
        if (!file.exists()) {  // 涓嶅瓨鍦ㄨ繑鍥� false
            return flag;
        } else {
            // 鍒ゆ柇鏄惁涓烘枃浠�
            if (file.isFile()) {  // 涓烘枃浠舵椂璋冪敤鍒犻櫎鏂囦欢鏂规硶
                return deleteFile(sPath);
            } else {  // 涓虹洰褰曟椂璋冪敤鍒犻櫎鐩綍鏂规硶
                return deleteDirectory(sPath);
            }
        }
    }
    
    /**
     * 鍒犻櫎鍗曚釜鏂囦欢
     * @param   sPath    琚垹闄ゆ枃浠剁殑鏂囦欢鍚�
     * @return 鍗曚釜鏂囦欢鍒犻櫎鎴愬姛杩斿洖true锛屽惁鍒欒繑鍥瀎alse
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 璺緞涓烘枃浠朵笖涓嶄负绌哄垯杩涜鍒犻櫎
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }
    
    /**
     * 鍒犻櫎鐩綍锛堟枃浠跺す锛変互鍙婄洰褰曚笅鐨勬枃浠�
     * @param   sPath 琚垹闄ょ洰褰曠殑鏂囦欢璺緞
     * @return  鐩綍鍒犻櫎鎴愬姛杩斿洖true锛屽惁鍒欒繑鍥瀎alse
     */
    public static boolean deleteDirectory(String sPath) {
        //濡傛灉sPath涓嶄互鏂囦欢鍒嗛殧绗︾粨灏撅紝鑷姩娣诲姞鏂囦欢鍒嗛殧绗�
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //濡傛灉dir瀵瑰簲鐨勬枃浠朵笉瀛樺湪锛屾垨鑰呬笉鏄竴涓洰褰曪紝鍒欓��鍑�
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //鍒犻櫎鏂囦欢澶逛笅鐨勬墍鏈夋枃浠�(鍖呮嫭瀛愮洰褰�)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //鍒犻櫎瀛愭枃浠�
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
					break;
				}
            } //鍒犻櫎瀛愮洰褰�
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
					break;
				}
            }
        }
        if (!flag) {
			return false;
		}
        //鍒犻櫎褰撳墠鐩綍
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

}