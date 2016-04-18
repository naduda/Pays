package pr.security.rest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("Common4rest")
public class Common4rest {
	private static final Logger log = LoggerFactory.getLogger(Common4rest.class);
	public final String RESULT_OK = resultMessage("ok");
	public final String RESULT_BAD = resultMessage("bad");
	
	public String notNull(String s) {
		if(s == null) return ""; else return s;
	}
	
	public String resultMessage(String s) {
		return String.format("{\"result\": \"%s\"}", s);
	}
	
	public File getFileFromURL(Class<?> cls, String path) {
		URL url = cls.getClassLoader().getResource(path);
		File file = null;
		try {
			path = url.toURI().toString();
			if(path.startsWith("jar:")) {
				path = path.substring(4);
				file = new File(new URI(path));
			} else {
				file = new File(url.toURI());
			}
		} catch (URISyntaxException e) {
			file = new File(url.getPath());
		}
		return file;
	}
	
	public List<String> getListOfFilesInJar(URL jar, String dirPath) {
		List<String> ret = null;
		ZipInputStream zip;
		try {
			zip = new ZipInputStream(jar.openStream());
			while(true) {
				ZipEntry e = zip.getNextEntry();
				if (e == null) break;
				String name = e.getName();
				if (name.startsWith(dirPath)) {
					if(ret == null) ret = new ArrayList<>();
					ret.add(name);
				}
			}
		} catch (IOException e1) {
			log.error(ExceptionUtils.getFullStackTrace(e1));
		}
		return ret;
	}
}