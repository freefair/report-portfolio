package io.freefair.report_portfolio.report.generation;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import io.freefair.report_portfolio.report.Entry;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by dennis on 25.10.15
 * (c) by dennis
 */
public class PdfFileGenerator {
	private final String filename;
	private final List<Entry> byRange;
	private LocalDate from;

	private final BaseColor baseBackColor = new BaseColor(67, 153, 181);

	public PdfFileGenerator(String filename, List<Entry> byRange, LocalDate from) {
		this.filename = filename;
		this.byRange = byRange;
		this.from = from;
	}

	public void generate() {
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(filename));
			document.open();

			writeHeader(document);
			writePadding(document);
			writeContent(document);

			document.close();
		} catch (DocumentException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void writePadding(Document document) throws DocumentException {
		Paragraph paragraph = new Paragraph();
		document.add(paragraph);
	}

	private void writeContent(Document document) throws DocumentException {
		PdfPTable table = new PdfPTable(3);
		table.setWidths(new int[] { 1, 5, 1 });

		writeHeaderRow(table, 1, "Tag", "Beschreibung", "Stunden");

		byRange.sort((e, e1) -> e.getDate().compareTo(e1.getDate()));

		for(Entry e : byRange){
			writeEntry(table, e);
		}

		writeSummary(table, byRange.stream().collect(Collectors.summarizingDouble(Entry::getTime)));

		document.add(table);
	}

	private void writeSummary(PdfPTable table, DoubleSummaryStatistics collect) {
		writeEmptyCell(table);
		writeEmptyCell(table);

		Phrase elements = new Phrase(collect.getSum() + "h");
		Font font = elements.getFont();
		font.setSize(10);
		elements.setFont(font);
		PdfPCell c1 = new PdfPCell(elements);
		c1.setBackgroundColor(baseBackColor);
		c1.setMinimumHeight(30);
		table.addCell(c1);
	}

	private void writeEmptyCell(PdfPTable table) {
		PdfPCell c1 = new PdfPCell(new Phrase(""));
		c1.setBorder(0);
		table.addCell(c1);
	}

	private void writeEntry(PdfPTable table, Entry e) {
		Phrase elements = new Phrase(e.getDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN));
		Font font = elements.getFont();
		font.setSize(10);
		elements.setFont(font);
		PdfPCell c1 = new PdfPCell(elements);
		c1.setBackgroundColor(baseBackColor);
		c1.setMinimumHeight(30);
		table.addCell(c1);

		elements = new Phrase(e.getData());
		elements.setFont(font);
		c1 = new PdfPCell(elements);
		c1.setMinimumHeight(30);
		table.addCell(c1);

		elements = new Phrase(e.getTime() + "h");
		elements.setFont(font);
		c1 = new PdfPCell(elements);
		c1.setMinimumHeight(30);
		table.addCell(c1);
	}

	private void writeHeader(Document document) throws DocumentException {
		PdfPTable table = new PdfPTable(2);
		table.setWidths(new int[] { 1, 5 });

		PdfPCell c1 = new PdfPCell(new Phrase("Ausbildungsnachweis (täglich)"));
		c1.setBackgroundColor(baseBackColor);
		c1.setColspan(2);
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setFixedHeight(40);
		table.addCell(c1);

		table.setHeaderRows(1);

		writeHeaderRow(table, 0, "Name: ", "Dennis Fricke");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		writeHeaderRow(table, 0, "Datum: ", "von " + from.format(formatter) + " bis " + from.plusDays(4).format(formatter));
		writeHeaderRow(table, 0, "Ausbildungsjahr: ", (from.minusYears(2013).minusDays(1).minusMonths(8).getYear() + 1) + ". Jahr");

		document.add(table);
	}

	private void writeHeaderRow(PdfPTable table, int mode, String header, String... content) {
		PdfPCell c1;
		Phrase phrase = new Phrase(header);
		Font font = phrase.getFont();
		font.setSize(10);
		font.setStyle(Font.BOLD);
		phrase.setFont(font);
		c1 = new PdfPCell(phrase);
		c1.setBackgroundColor(baseBackColor);
		c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		c1.setFixedHeight(30);
		table.addCell(c1);

		for(String c : content){
			phrase = new Phrase(c);
			if(mode == 1){
				font = phrase.getFont();
				font.setStyle(Font.BOLD);
				font.setSize(10);
				phrase.setFont(font);
			}
			c1 = new PdfPCell(phrase);
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			if(mode == 1){
				c1.setBackgroundColor(baseBackColor);
			}
			c1.setFixedHeight(30);
			table.addCell(c1);
		}
	}
}
