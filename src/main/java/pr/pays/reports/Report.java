package pr.pays.reports;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

public class Report {
	private static final Logger log = LoggerFactory.getLogger(Report.class);
	private HashMap<String, Object> params = new HashMap<String, Object>();

	public void setParameter(String name, Object value) {
		params.put(name, value);
	}
	
	public ByteArrayOutputStream create(String path, String name, String format) {
		if(!path.endsWith("/")) path += "/";
		path += name + ".jrxml";

		File template = getFileFromURL(path);
		if (template == null) {
			log.warn("source not found " + path);
			return null;
		}
		
		try(FileInputStream fis = new FileInputStream(template);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();) {

			JasperDesign design = JRXmlLoader.load(fis);
			JasperReport rep = JasperCompileManager.compileReport(design);
			JasperPrint jp = JasperFillManager.fillReport(rep, params);

			switch (format.toLowerCase()) {
			case "html":
				HtmlExporter exporter = new HtmlExporter();
				exporter.setExporterInput(new SimpleExporterInput(jp));
				exporter.setExporterOutput(new SimpleHtmlExporterOutput(bos));
				exporter.exportReport();
				break;
			case "xls":
				JRXlsExporter exporterXLS = new JRXlsExporter();
				exporterXLS.setExporterInput(new SimpleExporterInput(jp));
				exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(bos));
				SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
				//configuration.setOnePagePerSheet(true);
				configuration.setDetectCellType(true);
				configuration.setCollapseRowSpan(false);
				exporterXLS.setConfiguration(configuration);
				exporterXLS.exportReport();
				break;
			case "pdf":
				JRPdfExporter exporterPDF = new JRPdfExporter();
				exporterPDF.setExporterInput(new SimpleExporterInput(jp));
				exporterPDF.setExporterOutput(new SimpleOutputStreamExporterOutput(bos));
				exporterPDF.exportReport();
				break;
			}

			return bos;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private File getFileFromURL(String path) {
		URL url = this.getClass().getClassLoader().getResource(path);
		if(url == null) return null;
		
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			file = new File(url.getPath());
		}
		return file;
	}
}