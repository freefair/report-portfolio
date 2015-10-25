package io.freefair.report_portfolio.report;

import lombok.val;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dennis on 25.10.15
 * (c) by dennis
 */
public class EntryGenerator {
	private String logfile;

	private List<Entry> entries = new ArrayList<>();


	public EntryGenerator(String logfile) {
		this.logfile = logfile;
		parse();
	}

	private void parse() {
		try (BufferedReader reader = new BufferedReader(new FileReader(logfile))){
			List<String> lines = reader.lines().collect(Collectors.toList());
			Entry current = null;
			for(String line : lines) {
				if(line.startsWith("DATE:")){
					String replace = line.replace("DATE:", "");
					current = new Entry();
					current.setDate(LocalDateTime.parse(replace.trim(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
					entries.add(current);
				}
				else if (line.startsWith("\t")) {
					if(current == null) throw new RuntimeException("line \"" + line + "\" not expected");
					current.setData(current.getData() + line.trim() + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Entry> getByDate(LocalDate date){
		return entries.stream().filter(e -> e.getDate().toLocalDate().isEqual(date)).collect(Collectors.toList());
	}

	public List<Entry> getByRange(LocalDate from, LocalDate to){
		return entries.stream().filter(e -> (e.getDate().toLocalDate().isEqual(from) || e.getDate().toLocalDate().isAfter(from)) &&
											(e.getDate().toLocalDate().isEqual(to) || e.getDate().toLocalDate().isBefore(to))).collect(Collectors.toList());
	}
}
