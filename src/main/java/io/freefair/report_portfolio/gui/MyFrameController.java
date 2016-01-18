package io.freefair.report_portfolio.gui;

import io.freefair.report_portfolio.report.DataSourceAccessor;
import io.freefair.report_portfolio.report.Entry;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MyFrameController
{
	@FXML
	public TextField txtDateMonday;
	@FXML
	public TextField txtDateTuesday;
	@FXML
	public TextField txtDateWednesday;
	@FXML
	public TextField txtDateThursday;
	@FXML
	public TextField txtDateFriday;

	@FXML
	public TextField txtTimeMonday;
	@FXML
	public TextField txtTimeTuesday;
	@FXML
	public TextField txtTimeWednesday;
	@FXML
	public TextField txtTimeThursday;
	@FXML
	public TextField txtTimeFriday;

	@FXML
	public TextArea txtContentMonday;
	@FXML
	public TextArea txtContentTuesday;
	@FXML
	public TextArea txtContentWednesday;
	@FXML
	public TextArea txtContentThursday;
	@FXML
	public TextArea txtContentFriday;

	public Stage mainScene;


	private LocalDate currentMonday = LocalDate.of(2013, 9, 2);
	private DataSourceAccessor accessor;

	@FXML
	public void next() {
		currentMonday = currentMonday.plusDays(7);
		updateData();
	}

	@FXML
	public void previous() {
		currentMonday = currentMonday.minusDays(7);
		updateData();
	}

	@FXML
	public void today() {
		currentMonday = LocalDate.now();
		while(currentMonday.getDayOfWeek() != DayOfWeek.MONDAY)
		{
			currentMonday = currentMonday.minusDays(1);
		}
		updateData();
	}

	@FXML
	public void save() {
		if(accessor != null) {
			writeDataToObjects();
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open portfolio file");
			File file = fileChooser.showSaveDialog(mainScene);
			if(file == null) return;
			accessor.saveToFile(file.getAbsolutePath());
		}
	}

	@FXML
	public void open() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open portfolio file");
		File file = fileChooser.showOpenDialog(mainScene);
		accessor = new DataSourceAccessor(file.getAbsolutePath());
		updateData();
	}

	private List<Entry> currentEntries = new ArrayList<>();

	private void updateData() {
		if(accessor == null) throw new RuntimeException("No file opend");
		writeDataToObjects();
		currentEntries.clear();
		for(int i = 0; i < 5; i++){
			List<Entry> byDate = accessor.getByDate(currentMonday.plusDays(i));
			Entry entry;
			if(byDate.size() == 0){
				entry = new Entry();
				entry.setDate(currentMonday.plusDays(i).atStartOfDay());
				entry.setTime(0);
				accessor.addEntry(entry);
				entry.setData("");
			}
			else
				entry = byDate.get(0);
			updateField(i, entry);
			currentEntries.add(entry);
		}
	}

	private void writeDataToObjects() {
		for(int i = 0; i < currentEntries.size(); i++){
			Entry entry = currentEntries.get(i);
			String text = textAreas.get(i).getText();
			entry.setData(text);
			entry.setTime(Double.parseDouble(timeFields.get(i).getText()));
			entry.setDate(LocalDate.from(DateTimeFormatter.ofPattern("dd.MM.yyyy").parse(dateFields.get(i).getText())).atStartOfDay());
		}
	}

	private void updateField(int i, Entry entry) {
		dateFields.get(i).setText(entry.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		textAreas.get(i).setText(entry.getData());
		timeFields.get(i).setText(Double.valueOf(entry.getTime()).toString());
	}

	private List<TextField> dateFields = new ArrayList<>();
	private List<TextArea> textAreas = new ArrayList<>();
	private List<TextField> timeFields = new ArrayList<>();

	public void init() {
		dateFields.add(txtDateMonday);
		dateFields.add(txtDateTuesday);
		dateFields.add(txtDateWednesday);
		dateFields.add(txtDateThursday);
		dateFields.add(txtDateFriday);

		textAreas.add(txtContentMonday);
		textAreas.add(txtContentTuesday);
		textAreas.add(txtContentWednesday);
		textAreas.add(txtContentThursday);
		textAreas.add(txtContentFriday);

		timeFields.add(txtTimeMonday);
		timeFields.add(txtTimeTuesday);
		timeFields.add(txtTimeWednesday);
		timeFields.add(txtTimeThursday);
		timeFields.add(txtTimeFriday);
	}
}
