package io.freefair.report_portfolio.report.generation;

import io.freefair.report_portfolio.report.DataSourceAccessor;
import io.freefair.report_portfolio.report.Entry;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dennis on 25.10.15
 * (c) by dennis
 */
public class PdfGenerator {
	private DataSourceAccessor accessor;

	public PdfGenerator(String dataSource) {
		accessor = new DataSourceAccessor(dataSource);
	}

	public void generate(String output, LocalDate fromDate, LocalDate toDate) {
		for(LocalDate date = fromDate; date.isBefore(toDate.plusDays(1)); date = date.plusDays(7)){
			generateDocument(output, buildMissing(date), date);
		}
	}

	private List<Entry> buildMissing(LocalDate start) {
		List<Entry> result = new ArrayList<>();

		for(int i = 0; i < 5; i++){
			List<Entry> byDate = accessor.getByDate(start.plusDays(i));
			if(byDate.size() == 0){
				Entry entry = new Entry();
				entry.setDate(start.plusDays(i).atStartOfDay());
				entry.setTime(0);
				result.add(entry);
			}
			else
				result.add(byDate.get(0));
		}

		return result;
	}

	private void generateDocument(String output, List<Entry> byRange, LocalDate of) {
		String filename = output + byRange.get(0).getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
		PdfFileGenerator generator = new PdfFileGenerator(filename, byRange, of);
		generator.generate();
	}
}
