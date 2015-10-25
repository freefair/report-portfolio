package io.freefair.report_portfolio.report;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MissingEntryGenerator
{
	private String filename;

	private List<EntryTemplate> templates = new ArrayList<>();

	public MissingEntryGenerator(String filename){
		this.filename = filename;
		parse();
	}

	private void parse() {
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))){
			List<String> lines = reader.lines().collect(Collectors.toList());
			for(String line : lines) {
				String[] split = line.split(":");
				if(split.length < 3) throw new RuntimeException("Missing file: \"" + line + "\" not expected");
				EntryTemplate template = new EntryTemplate();
				template.setType(split[0].equalsIgnoreCase("STATIC") ? EntryType.STATIC : EntryType.WEEKLY);
				template.setDate(LocalDate.parse(split[1], DateTimeFormatter.ofPattern("dd.MM.yyyy")).atStartOfDay());
				template.setData(split[2]);
				template.setTime(Double.parseDouble(split[3]));
				templates.add(template);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Entry getByDate(LocalDate date){
		for(EntryTemplate template : templates){
			if(template.getType() == EntryType.WEEKLY){
				if(template.getDate().getDayOfWeek() == date.getDayOfWeek()) {
					return template.generateEntry(date.atStartOfDay());
				}
			}
			else if (template.getType() == EntryType.STATIC)
			{
				if(template.getDate().toLocalDate().equals(date)) {
					return template.generateEntry(date.atStartOfDay());
				}
			}
		}
		return null;
	}

	public List<Entry> getByRange(LocalDate from, LocalDate to){
		List<Entry> result = new ArrayList<>();
		for(LocalDate date = from; date.isBefore(to.plusDays(1)); date = date.plusDays(1)){
			Entry byDate = getByDate(date);
			if(byDate != null)
				result.add(byDate);
		}
		return result;
	}
}
