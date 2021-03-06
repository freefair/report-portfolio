package io.freefair.report_portfolio.gui;

import io.freefair.report_portfolio.report.DataSourceAccessor;
import io.freefair.report_portfolio.report.Entry;

import javax.swing.*;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.text.DateFormatter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MyFrame_old extends JFrame
{
	DataSourceAccessor accessor;

	public MyFrame_old(String dataSource){
		super("Report portfolio");

		accessor = new DataSourceAccessor(dataSource);

		initGui();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);

		updateData();
	}

	private void initGui() {
		JPanel panel = new JPanel(null);

		JButton btnNext = new JButton();
		btnNext.setText("Next");
		btnNext.setBounds(160, 10, 150, 25);
		btnNext.addActionListener(a -> next());
		panel.add(btnNext);

		JButton btnPrev = new JButton();
		btnPrev.setText("Previous");
		btnPrev.setBounds(10, 10, 150, 25);
		btnPrev.addActionListener(a -> prev());
		panel.add(btnPrev);

		JButton btnSave = new JButton();
		btnSave.setText("Save");
		btnSave.setBounds(310, 10, 150, 25);
		btnSave.addActionListener(a -> save());
		panel.add(btnSave);

		JButton btnToday = new JButton();
		btnToday.setText("Today");
		btnToday.setBounds(460, 10, 150, 25);
		btnToday.addActionListener(a -> today());
		panel.add(btnToday);

		buildInputArea(0, panel);
		buildInputArea(1, panel);
		buildInputArea(2, panel);
		buildInputArea(3, panel);
		buildInputArea(4, panel);

		add(panel);
		setSize(600, 600);
	}

	private void today() {
		currentMonday = LocalDate.now();
		while(currentMonday.getDayOfWeek() != DayOfWeek.MONDAY)
		{
			currentMonday = currentMonday.minusDays(1);
		}
		updateData();
	}

	private void save() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showSaveDialog(this);
		accessor.saveToFile(fileChooser.getSelectedFile().getAbsolutePath());
	}

	private void prev() {
		currentMonday = currentMonday.minusDays(7);
		updateData();
	}

	private void next() {
		currentMonday = currentMonday.plusDays(7);
		updateData();
	}

	private void updateData() {
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

	private LocalDate currentMonday = LocalDate.of(2013, 9, 2);
	private List<Entry> currentEntries = new ArrayList<>();

	private List<JTextField> dateFields = new ArrayList<>();
	private List<JTextArea> textAreas = new ArrayList<>();
	private List<JTextField> timeFields = new ArrayList<>();

	private void buildInputArea(int i, JPanel panel) {
		JTextField txtDate = new JTextField();
		txtDate.setBounds(10, 35 + (160 * i), 150, 25);
		panel.add(txtDate);
		dateFields.add(i, txtDate);

		JTextArea textArea = new JTextArea();
		textArea.setBounds(160, 35 + (160 * i), 600, 150);
		panel.add(textArea);
		textAreas.add(i, textArea);

		JTextField txtTime = new JTextField();
		txtTime.setBounds(760, 35 + (160 * i), 75, 25);
		panel.add(txtTime);
		timeFields.add(i, txtTime);
	}
}
