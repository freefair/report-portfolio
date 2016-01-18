package io.freefair.report_portfolio.report;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dennis on 25.10.15
 * (c) by dennis
 */
public class DataSourceAccessor {
	private String dataSource;
	private List<Entry> entries = new ArrayList<>();

	public DataSourceAccessor(String dataSource) {
		this.dataSource = dataSource;
		parse();
	}

	private void parse() {
		try (BufferedReader reader = new BufferedReader(new FileReader(dataSource))) {
			List<String> lines = reader.lines().collect(Collectors.toList());
			Entry current = null;
			boolean append = false;
			for (String line : lines) {
				if(!append){
					if(current != null)
						prepareData(current);
					current = new Entry();
					String[] split = line.split(",");
					current.setDate(LocalDate.parse(split[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay());
					String data = String.join(",", getFullString(split, 1));
					if(data.contains("\"") && (!data.endsWith("\"") || data.length() == 1)) append = true;
					current.setData(data);
					if(!append) {
						try {
							current.setTime(Double.parseDouble(split[split.length - 1]));
						} catch (Exception ex){
							System.err.println(line);
							ex.printStackTrace();
						}
					}
					entries.add(current);
				}
				else {
					if(line.contains("\""))
						append = false;
					if(!append){
						String[] split = line.split(",");
						current.setTime(Double.parseDouble(split[split.length - 1]));
						line = getFullString(split, 0);
					}
					current.setData(current.getData() + "\n" + line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		sort();
	}

	private String getFullString(String[] split, int start) {
		String result = "";
		for(int i = start; i < split.length; i++){
			result += split[i];
			if(result.endsWith("\"")) break;
		}
		return result;
	}

	private void sort() {
		entries.sort((e, e2) -> e.getDate().compareTo(e2.getDate()));
	}

	private void prepareData(Entry current) {
		String text = current.getData();
		if(text.contains("<")) {
			Document parse = Jsoup.parse(text);
			Elements ul = parse.getElementsByTag("ul");
			for (Element element : ul) {
				Elements li = element.getElementsByTag("li");
				for (Element elem : li) {
					elem.replaceWith(new TextNode("- " + elem.text() + "\n", ""));
				}
				element.replaceWith(new TextNode(ul.text(), ""));
			}
			text = parse.text();
			text = text.replace(" - ", "\n - ");
			if (text.startsWith("\n"))
				text = text.substring(1);
		}
		current.setData(text.replaceAll("\"$", "").replaceAll("^\"", ""));
	}

	public void addEntries(List<Entry> entries) {
		this.entries.addAll(entries);
	}

	public void addEntry(Entry entry){
		this.entries.add(entry);
		sort();
	}

	public List<Entry> getByDate(LocalDate date) {
		return entries.stream().filter(e -> e.getDate().toLocalDate().isEqual(date)).collect(Collectors.toList());
	}

	public List<Entry> getByRange(LocalDate from, LocalDate to){
		return entries.stream().filter(e -> (e.getDate().toLocalDate().isEqual(from) || e.getDate().toLocalDate().isAfter(from)) &&
				(e.getDate().toLocalDate().isEqual(to) || e.getDate().toLocalDate().isBefore(to))).collect(Collectors.toList());
	}

	public void save(){
		List<Entry> entries = this.entries.stream().sorted((e,e2) -> e.getDate().compareTo(e2.getDate())).collect(Collectors.toList());
		for(Entry e : entries){
			System.out.println(e.toCvsString());
		}
	}

	public void saveToFile(String filename){
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))){
			List<Entry> entries = this.entries.stream().sorted((e,e2) -> e.getDate().compareTo(e2.getDate())).collect(Collectors.toList());
			for(Entry e : entries){
				String data = e.toCvsString();
				writer.write(data, 0, data.length());
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
