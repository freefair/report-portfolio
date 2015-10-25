package io.freefair.report_portfolio.report;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EntryTemplate
{
	private EntryType type;
	private LocalDateTime date;
	private String data;
	private double time;

	public Entry generateEntry(LocalDateTime date) {
		Entry result = new Entry();
		result.setDate(date);
		result.setData(data);
		result.setTime(time);
		return result;
	}
}
