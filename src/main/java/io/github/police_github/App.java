package io.github.police_github;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.github.police_github.pojo.Cop;

public class App {
    private static Logger logger = Logger.getLogger(App.class.getName());
    
	// accept all SSL certificates
	static {
		final TrustManager[] trustAllCertificates = new TrustManager[] { new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null; // Not relevant.
			}

			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
				// Do nothing. Just allow them all.
			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
				// Do nothing. Just allow them all.
			}
		} };

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCertificates, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (GeneralSecurityException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public App() {
		LogManager.getLogManager().reset();
		
		logger.setLevel(Level.ALL);
		
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new SimpleFormatter());
		logger.addHandler(handler);
	}

	private String getPdfContent(File file) {
		PDDocument doc = null;
		PDFTextStripper stripper = null;
		try {
			doc = PDDocument.load(file);
			stripper = new PDFTextStripper();
			stripper.setStartPage(1);
			stripper.setEndPage(doc.getNumberOfPages());

			return stripper.getText(doc);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (doc != null) {
					doc.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void run() {
		App app = new App();

		File source = new File("/tmp/source.json");
		Map<Integer, List<Cop>> map = new HashMap<Integer, List<Cop>>();
		if (source.exists() && source.isFile()) {
			try {
				map = new ObjectMapper().readValue(source, new TypeReference<HashMap<Integer, List<Cop>>>() {});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		map = map
				.entrySet()
				.stream()
				.sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		
		/*
		for (Entry<Integer, List<Cop>> entry : map.entrySet()) {
			logger.info(entry.getKey());
			for (Cop cop : entry.getValue()) {
				logger.info("\t" + cop.toString());
			}
		}
		*/
		
		/*
		List<String> urls = Arrays.asList(
				"https://www.gld.gov.hk/egazette/pdf/20010520/cgn200105202998.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20010520/cgn200105202999.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20010539/cgn200105395937.pdf",
				
				"https://www.gld.gov.hk/egazette/pdf/20020623/cgn200206233347.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20020623/cgn200206233348.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20020640/cgn200206406187.pdf",
				
				"https://www.gld.gov.hk/egazette/pdf/20030715/cgn200307152405.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20030715/cgn200307152406.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20030736/cgn200307365995.pdf",
				
				"https://www.gld.gov.hk/egazette/pdf/20040814/cgn200408142134.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20040814/cgn200408142135.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20040839/cgn200408396217.pdf",
				
				"https://www.gld.gov.hk/egazette/pdf/20050911/cgn200509111188.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20050911/cgn200509111189.pdf",
				
				"https://www.gld.gov.hk/egazette/pdf/20061017/cgn200610172570.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20061036/cgn200610365550.pdf",
				
				"https://www.gld.gov.hk/egazette/pdf/20071116/cgn200711162451.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20071116/cgn200711162452.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20071143/cgn200711436860.pdf",
				
				"https://www.gld.gov.hk/egazette/pdf/20081218/cgn200812182830.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20081218/cgn200812182831.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20081242/cgn200812427035.pdf",
				
				"https://www.gld.gov.hk/egazette/pdf/20091343/cgn200913436560.pdf",
				
				"https://www.gld.gov.hk/egazette/pdf/20101425/cgn201014253660.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20101425/cgn201014253661.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20101448/cgn201014487510.pdf",
				
				"https://www.gld.gov.hk/egazette/pdf/20111526/cgn201115264112.pdf",
				
				"https://www.gld.gov.hk/egazette/pdf/20121603/cgn20121603263.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20121632/cgn201216325323.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20121632/cgn201216325324.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20121652/cgn201216528251.pdf",
				
				"https://www.gld.gov.hk/egazette/pdf/20131732/cgn201317324576.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20131733/cgn201317334756.pdf",
				
				"https://www.gld.gov.hk/egazette/pdf/20141813/cgn201418131744.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20141832/cgn201418324511.pdf",
				"https://www.gld.gov.hk/egazette/pdf/20141832/cgn201418324512.pdf",
				
				"https://www.gld.gov.hk/egazette/pdf/20162001/cgn2016200125.pdf",
				
				"https://www.gld.gov.hk/egazette/pdf/20172126/cgn201721264331.pdf"
		);
		*/
		List<String> urls = Arrays.asList(
				"https://www.gld.gov.hk/egazette/pdf/20172126/cgn201721264331.pdf"
		);
		
		for (String url : urls) {
			logger.info("Handle: " + url);
			try {
				File file = File.createTempFile("police-github", ".pdf");
				FileUtils.copyURLToFile(new URL(url), file);
				if (file.exists()) {
					String[] lines = app.getPdfContent(file).split("\r\n");
					for (String line : lines) {
						line = line
								.replace(" (輔警 )", "(輔警)").replace(" (輔警)", "(輔警)").replace(" (輔警 ", "(輔警)")
								.replace(" (署任)", "(署任)").replace(" (署任 )", "(署任)").replace(" (署任 ", "(署任)");
						try {
							String[] words = line.split(Pattern.quote(" "));
							String name = null;
							Integer id = null;
							String position = null;
							if (words.length == 5 || words.length == 6) {
								if (words.length == 5) {
									if (words[2].contains("-") || words[2].contains("—") || words[2].contains("─")) {
										continue;
									}
									name = words[1];
									id = Integer.parseInt(words[2]);
									position = words[3];
								} else if (words.length == 6) {
									if (words[3].contains("-") || words[3].contains("—") || words[3].contains("─")) {
										continue;
									}
									name = words[1] + " " + words[2];
									id = Integer.parseInt(words[3]);
									position = words[4];
								}
								if (!map.containsKey(id)) {
									map.put(id, new ArrayList<Cop>());
								}
								map.get(id).add(new Cop(name, position, line.contains("輔警"), url));
							} else {
								// logger.info(line);
							}
						} catch (NumberFormatException e) {
							System.err.println(line);
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
			String json = new ObjectMapper().writeValueAsString(map);
			Files.write(Paths.get("/tmp/dest.json"), json.getBytes());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		App app = new App();
		app.run();
	}

}
