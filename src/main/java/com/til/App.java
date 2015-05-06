package com.til;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final Logger logger = LoggerFactory
			.getLogger(App.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//testing

		String content = null;
		try {
			content = "  function func(a,b){  "+
					 "   return a * b \n }   ";
			//logger.error(minify(content));
			logger.info(minify((content)));
		} catch (Exception ex) {
			logger.error(ex.getStackTrace() + ex.getMessage());
		}
	}

	public static String minify(String content) {
		String compressedCode = null;
		try {
			UglifyJSUtil uglifyJSUtil = new UglifyJSUtil();
			compressedCode = uglifyJSUtil.processJS(content);
		} catch (Exception ex) {
			logger.debug(ex.getStackTrace() + ex.getMessage());
		}
		return compressedCode;
	}
}
