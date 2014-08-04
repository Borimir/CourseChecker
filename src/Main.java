import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Main {

	public static String version = "0.4";

	public static long previousPoll = System.currentTimeMillis();
	public static Document CISC360Page;
	public static Document CISC275Page;
	public static boolean exit = false;
	public static String url1 = "http://primus.nss.udel.edu/CoursesSearch/courseInfo?&courseid=006690&offernum=1&term=2148&session=1&section=010";
	public static String url2 = "http://primus.nss.udel.edu/CoursesSearch/courseInfo?&courseid=301144&offernum=1&term=2148&session=1&section=010";
	public static JFrame frame;
	public static JPanel panel;
	public static JPanel urlInputPanel;
	public static JPanel classAmountPanel;
	public static JLabel urlWarning;
	public static JLabel urlExample;
	public static JLabel urlName;
	public static JLabel urlAddress;
	public static JLabel classAmountLabel;
	public static JTextField classAmountField;
	public static JTextField urlInputField1;
	public static JButton classButton;
	public static JButton updateButton;
	public static boolean openingFound = false;
	public static boolean polling = false;
	public static ArrayList<String> urlList;
	public static ArrayList<String> classNameList;
	public static ArrayList<String> resultList;
	public static ArrayList<JTextField> urlInputFieldList;
	public static ArrayList<JTextField> classNameInputFieldList;
	public static int classAmount;
	public static GridBagConstraints c;
	public static int desiredOpenings;
	public static JLabel desiredOpeningsLabel;
	public static JTextField desiredOpeningsInputField;
	public static JButton loadButton;
	public static String notifyEmail = "";
	public static JLabel emailLabel;
	public static JTextField emailInputField;
	public static boolean email = false;

	public static void main(String[] args) {

		urlList = new ArrayList<String>();
		classNameList = new ArrayList<String>();
		urlInputFieldList = new ArrayList<JTextField>();
		classNameInputFieldList = new ArrayList<JTextField>();

		frame = new JFrame();
		frame.setTitle("OpenCourseGrabber" + "v" + version);
		panel = new JPanel();

		classAmountPanel = new JPanel(new GridLayout(4, 2));
		classAmountLabel = new JLabel();
		classAmountField = new JTextField(3);
		classAmountField.setText("1");
		classButton = new JButton("Okay");
		classAmountLabel
				.setText("<html>How many classes would you like to track?</html>");
		classAmountPanel.add(classAmountLabel);
		classAmountPanel.add(classAmountField);
		desiredOpeningsLabel = new JLabel();
		desiredOpeningsInputField = new JTextField();
		desiredOpeningsLabel.setText("How many openings are you looking for?");
		desiredOpeningsInputField.setText("1");
		classAmountPanel.add(desiredOpeningsLabel);
		classAmountPanel.add(desiredOpeningsInputField);
		classAmountPanel.add(classButton);

		emailLabel = new JLabel("Notify Email (optional)");
		emailInputField = new JTextField();
		classAmountPanel.add(emailLabel);
		classAmountPanel.add(emailInputField);

		classAmountPanel.add(classButton);

		loadButton = new JButton("Load");
		classAmountPanel.add(loadButton);

		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});

		classButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				classButtonClick();
			}
		});
		urlInputPanel = new JPanel(new GridBagLayout());

		urlWarning = new JLabel("Input course page URL here");
		urlExample = new JLabel(
				"Example: http://primus.nss.udel.edu/CoursesSearch/courseInfo?&courseid=006690&offernum=1&term=2148&session=1&section=010");
		urlName = new JLabel("Class name");
		urlAddress = new JLabel("Course address");

		updateButton = new JButton("Okay");

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		urlInputPanel.add(urlWarning, c);
		c.gridx = 1;
		urlInputPanel.add(urlExample, c);
		c.gridx = 0;
		c.gridy = 1;
		urlInputPanel.add(urlName, c);
		c.gridx = 1;
		urlInputPanel.add(urlAddress, c);

		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// grab url from input field, advance to update panel
				updateButtonClick();
			}
		});

		frame.add(classAmountPanel);
		frame.setVisible(true);
		panel.setVisible(true);

		JLabel label = new JLabel();
		label.setText("Checking...");
		label.setVisible(true);
		panel.add(label);
		JButton saveButton = new JButton("Save");
		panel.add(saveButton);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		while (!exit) {
			if (polling && System.currentTimeMillis() - previousPoll > 900000) {
				String pollResult = poll();
				label.setText("<html>" + pollResult + "</html>");
				System.out.print(pollResult);
				frame.pack();
				frame.repaint();
				previousPoll = System.currentTimeMillis();
			} else {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String poll() {
		String result = "";
		int urlIndex = 0;
		for (String url : urlList) {
			Document page = null;
			try {
				page = Jsoup.connect(url).get();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String seats = page.getElementById("openseats").text();
			// String seats275 = CISC275Page.getElementById("openseats").text();
			String[] parts = seats.split(" ");
			String part_0 = parts[0];
			System.out.println(part_0);
			if (Integer.parseInt(part_0) >= desiredOpenings && !openingFound) {
				openingFound = true;
				try {
					if (email) {
						GoogleMail.Send("udelcoursechecker", "grabmycourse",
								notifyEmail, "Notification from CourseChecker",
								"Opening Found!");

					}
				} catch (AddressException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Thread t = new Thread(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(null, "Opening found!");
					}
				});
				t.start();
			}
			Date date = new Date();
			result = result + date + " | " + classNameList.get(urlIndex) + ": "
					+ seats + "<br>";
			urlIndex++;
		}

		return result;

	}

	public static void updateButtonClick() {
		for (JTextField classNameField : classNameInputFieldList) {
			classNameList.add(classNameField.getText());
		}

		for (JTextField urlField : urlInputFieldList) {
			urlList.add(urlField.getText());
		}

		frame.remove(urlInputPanel);

		frame.add(panel);
		frame.pack();
		frame.revalidate();
		frame.repaint();
		polling = true;
	}

	public static void classButtonClick() {
		classAmount = Integer.parseInt(classAmountField.getText());
		desiredOpenings = Integer.parseInt(desiredOpeningsInputField.getText());
		notifyEmail = emailInputField.getText();
		if (notifyEmail != "") {
			email = true;
		}
		urlInputFieldList = new ArrayList<JTextField>();
		classNameInputFieldList = new ArrayList<JTextField>();
		c.fill = GridBagConstraints.HORIZONTAL;

		for (int i = 0; i < classAmount; i++) {
			JTextField urlField = new JTextField(80);
			JTextField nameField = new JTextField(8);
			c.gridx = 0;
			c.gridy++;
			classNameInputFieldList.add(nameField);
			urlInputPanel.add(nameField, c);
			urlInputFieldList.add(urlField);
			c.gridx = 1;
			urlInputPanel.add(urlField, c);

		}
		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 2;
		urlInputPanel.add(updateButton, c);

		frame.remove(classAmountPanel);
		frame.add(urlInputPanel);
		frame.pack();
		frame.revalidate();
		frame.repaint();
	}

	public static void save() {
		Class classes = new Class(classNameList, urlList, desiredOpenings,
				notifyEmail);
		try {
			FileOutputStream fos = new FileOutputStream("tempdata.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(classes);
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void load() {
		try {
			FileInputStream fis = new FileInputStream("tempdata.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			Class c = (Class) ois.readObject();
			classNameList = c.nameList;
			urlList = c.urlList;
			desiredOpenings = c.desiredSeats;
			notifyEmail = c.notifyEmail;
			email = c.email;
			ois.close();

			frame.remove(classAmountPanel);

			frame.add(panel);
			frame.pack();
			frame.revalidate();
			frame.repaint();
			polling = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}