package net.grabsalot.gui;

import net.grabsalot.business.Logger;
import net.grabsalot.gui.settings.LookAndFeelSettings;

import net.grabsalot.gui.components.JStatusBar;
import net.grabsalot.gui.SettingsFrame;
import net.grabsalot.gui.components.SettingsPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;

public class SettingsFrame extends JFrame {

	private static final long serialVersionUID = 6753602601252275272L;
	private JSplitPane splitter;
	private JTree tree;
	private SettingsPanel content;
	private JScrollPane spnTree;
	private JScrollPane spnContent;
	private JStatusBar statusBar;
	private JButton btnSave;
	private JButton btnSaveAndClose;

	public SettingsFrame() {
		this.setupComponents();

		this.pack();
		this.setLocationRelativeTo(null);
	}

	private void setupComponents() {
		tree = new JTree();

		spnTree = new JScrollPane(tree);

		content = new LookAndFeelSettings();

		spnContent = new JScrollPane(content);

		splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spnTree, spnContent);

		this.add(splitter, BorderLayout.CENTER);

		statusBar = new JStatusBar();
		statusBar.add(Box.createHorizontalGlue());

		btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SettingsFrame.this.saveSettings();
			}
		});
		statusBar.add(btnSave);

		btnSaveAndClose = new JButton("Save & Close");
		btnSaveAndClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SettingsFrame.this.saveSettings();
				SettingsFrame.this.dispose();
			}
		});
		statusBar.add(btnSaveAndClose);
		try {
			for (Class c : getClasses("grabsalot.gui.settings")) {
				//Logger._().info(c.getName());
				//c.getField("");
			}
		} catch (Exception ex) {
			//dsa
		}

		this.add(statusBar, BorderLayout.SOUTH);
	}

	private void saveSettings() {
		if (content.save()) {
			statusBar.setText("Settings saved successfully!");
		} else {
			statusBar.setText("Failed to save settings!");
		}
	}

	/**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private static List<Class> getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			String fileName = resource.getFile();
			String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
			dirs.add(new File(fileNameDecoded));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			String fileName = file.getName();
			if (file.isDirectory()) {
				assert !fileName.contains(".");
				classes.addAll(findClasses(file, packageName + "." + fileName));
			} else if (fileName.endsWith(".class") && !fileName.contains("$")) {
				Class _class;
				try {
					_class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
				} catch (ExceptionInInitializerError e) {
					// happen, for example, in classes, which depend on
					// Spring to inject some beans, and which fail,
					// if dependency is not fulfilled
					_class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6),
							false, Thread.currentThread().getContextClassLoader());
				}
				classes.add(_class);
			}
		}
		return classes;
	}
}
