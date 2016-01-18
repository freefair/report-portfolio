package io.freefair.report_portfolio;

import io.freefair.report_portfolio.cli.Argparse;
import io.freefair.report_portfolio.cli.ArgumentList;
import io.freefair.report_portfolio.gui.MyFrameApplication;
import io.freefair.report_portfolio.report.DataSourceAccessor;
import io.freefair.report_portfolio.report.Entry;
import io.freefair.report_portfolio.report.EntryGenerator;
import io.freefair.report_portfolio.report.MissingEntryGenerator;
import io.freefair.report_portfolio.report.generation.PdfGenerator;
import lombok.val;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main
{
	public static void main(String[] args){
		val parse = new Argparse(args);
		ArgumentList arguments = parse.parse();

		String logfile = arguments.get("logfile");
		String from = arguments.get("from");
		String to = arguments.get("to");
		String missingfile = arguments.get("missingfile");
		String dataSource = arguments.get("datasource");

		String gui = arguments.get("gui");

		String generatePdf = arguments.get("pdf");

		if(gui != null && gui.equalsIgnoreCase("true")) {
			MyFrameApplication application = new MyFrameApplication();
			application.doLaunch(null);
			return;
		}

		LocalDate fromDate = LocalDate.parse(from, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		LocalDate toDate = LocalDate.parse(to, DateTimeFormatter.ofPattern("dd.MM.yyyy"));

		if(generatePdf != null && generatePdf.equalsIgnoreCase("true")){
			PdfGenerator pdfGenerator = new PdfGenerator(dataSource);
			pdfGenerator.generate(arguments.get("output"), fromDate, toDate);
			return;
		}

		EntryGenerator generator = new EntryGenerator(logfile);
		List<Entry> byRange = generator.getByRange(fromDate, toDate);

		MissingEntryGenerator missingEntryGenerator = new MissingEntryGenerator(missingfile);
		List<Entry> missingByRange = missingEntryGenerator.getByRange(fromDate, toDate);

		DataSourceAccessor accessor = new DataSourceAccessor(dataSource);

		accessor.addEntries(missingByRange);
		accessor.addEntries(byRange);
		accessor.save();
	}
}
