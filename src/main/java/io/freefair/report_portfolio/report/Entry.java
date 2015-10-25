package io.freefair.report_portfolio.report;

import lombok.Data;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
public class Entry
{
	private LocalDateTime date = LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0);
	private String data = "";
	private double time = 8;

	public String toCvsString(){
		return String.format("%s,\"%s\",%s", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), data.replace("\"", ""), NumberFormat.getNumberInstance(Locale.US).format(time));
	}
}
