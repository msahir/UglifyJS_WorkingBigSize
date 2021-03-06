package com.til;

import java.io.IOException;
import java.io.InputStream;

import org.mozilla.javascript.RhinoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.isdc.wro.WroRuntimeException;
import ro.isdc.wro.extensions.script.RhinoScriptBuilder;
import ro.isdc.wro.extensions.script.RhinoUtils;
import ro.isdc.wro.util.StopWatch;
import ro.isdc.wro.util.WroUtil;


public class UglifyJSUtil {
	//comment1

	private final boolean uglify = true;
	private static final String DEFAULT_UGLIFY_JS = "/resources/uglify-1.0.6.min.js";
	private static final Logger logger = LoggerFactory.getLogger(UglifyJSUtil.class);

	public String processJS(final String code) throws IOException {
		try {
			final StopWatch watch = new StopWatch();
			watch.start("init");
			final RhinoScriptBuilder builder = initScriptBuilderJS();
			watch.stop();
			watch.start(uglify ? "uglify" : "beautify");

			final String originalCode = WroUtil.toJSMultiLineString(code);
			final StringBuffer sb = new StringBuffer("(function() {");
			sb.append("var orig_code = " + originalCode + ";");
			sb.append("var ast = jsp.parse(orig_code);");
			sb.append("ast = exports.ast_mangle(ast);");
			sb.append("ast = exports.ast_squeeze(ast);");
			// the second argument is true for uglify and false for beautify.
			sb.append("return exports.gen_code(ast, {beautify: " + !uglify
					+ " });");
			sb.append("})();");
			if (builder != null) {
				final Object result = builder.evaluate(sb.toString(),
						"uglifyIt");
				watch.stop();
				logger.debug(watch.prettyPrint());
				return String.valueOf(result);
			}
		} catch (final RhinoException e) {
			throw new WroRuntimeException(RhinoUtils.createExceptionMessage(e),
					e);
		} catch (Exception ex) {
			System.out.println(ex.getStackTrace() + ex.getMessage());
		}
		return "";
	}

	private RhinoScriptBuilder initScriptBuilderJS() {
		try {
			final String scriptInit = "var exports = {}; function require() {return exports;}; var process={version:0.1};";

			return RhinoScriptBuilder.newChain().addJSON()
					.evaluateChain(scriptInit, "initScript")
					.evaluateChain(getScriptAsStreamJS(), DEFAULT_UGLIFY_JS);
		} catch (final IOException ex) {
			throw new IllegalStateException("Failed initializing js", ex);
		} catch (Exception ex) {
			logger.error(ex.getStackTrace() + ex.getMessage());
		}
		return null;
	}

	protected InputStream getScriptAsStreamJS() {
		InputStream inputStream = null;
		try {
			inputStream = getClass().getResourceAsStream("/resources/uglify-1.0.6.min.js");
			//inputStream = getClass().getResourceAsStream("E:\\gs-maven-master\\complete\\src\\main\\java\\resources\\uglify-1.0.6.min.js");
			if (inputStream == null) {
				logger.debug("inputStream null");
				return inputStream;
			} 
		} catch (Exception ex) {
			logger.error(ex.getStackTrace() + ex.getMessage());
		}
		return inputStream;

	}
}
