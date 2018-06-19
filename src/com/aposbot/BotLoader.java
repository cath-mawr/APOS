package com.aposbot;

import java.awt.Frame;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import com.aposbot._default.IClientInit;

public final class BotLoader {

	private static final String VERSION = "2.7";
	static final String PROPERTIES_FILE = "." + File.separator + "bot.properties";
	private String username;
	private String password;
	private int defaultOCR;
	private TextArea cTextArea;
	private Frame cFrame;
	private final IClientInit init;
	private String font;
	
	private final class Authenticator implements Runnable {

		private boolean invalid;
		private int failed_secs = 5;

		@Override
		public void run() {
			try {
				Thread.sleep(60 * 60 * 1000);
			} catch (InterruptedException ex) {
			}
			for (;;) {
				int sleep;
				int mins = 60 + (7 & Constants.RANDOM.nextInt());
				try {
					if (!authenticate()) {
						System.out.println("Auth invalid. Stopping, and retrying in " + mins + " minutes...");
						init.getScriptListener().setBanned(true);
						init.getAutoLogin().setBanned(true);
						init.getPaintListener().setBanned(true);
						invalid = true;
					} else {
						if (invalid) {
							System.out.println("Auth valid. Starting.");
							init.getScriptListener().setBanned(false);
							init.getAutoLogin().setBanned(false);
							init.getPaintListener().setBanned(false);
							invalid = false;
						}
					}
					sleep = mins * 60 * 1000;
					failed_secs = 5;
				} catch (IOException ex) {
					System.out.println("Error connecting: " + ex.toString());
					System.out.println("Note that the bot can only authenticate when the forums are online.");
					System.out.println("Retrying in " + failed_secs + " seconds...");
					init.getScriptListener().setBanned(true);
					init.getAutoLogin().setBanned(true);
					init.getPaintListener().setBanned(true);
					invalid = true;
					sleep = (failed_secs++) * 1000;
				}
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException ex) {
				}
			}
		}
	}

	public BotLoader(String[] argv, IClientInit init) {
		this.init = init;
		if (argv.length == 0) {
			System.out.println("To launch the bot without the built-in console, use at least one command-line argument.");

			final TextArea cTextArea = new TextArea(null, 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
			BotFrame.setColours(cTextArea, true);
			cTextArea.setEditable(false);
			this.cTextArea = cTextArea;

			final Frame cFrame = new Frame("Console");
			cFrame.addWindowListener(new StandardCloseHandler(cFrame, StandardCloseHandler.EXIT));
			cFrame.add(cTextArea);
			final Insets in = cFrame.getInsets();
			cFrame.setSize(in.right + in.left + 545, in.top + in.bottom + 320);
			cFrame.setIconImages(Constants.ICONS);
			cFrame.setLocationRelativeTo(null);
			cFrame.setVisible(true);
			cFrame.toFront();
			cFrame.requestFocus();
			this.cFrame = cFrame;

			final PrintStream ps = new PrintStream(new TextAreaOutputStream(cTextArea));
			System.setOut(ps);
			System.setErr(ps);
		}
		System.out.println("APOS - contact address: rsc@entering.space");
		Properties p = getProperties(PROPERTIES_FILE);
		if (p != null) {
			try {
				username = p.getProperty("auth_user");
				password = p.getProperty("auth_pass");
				font = p.getProperty("font");
				if (font != null && font.trim().isEmpty()) {
					font = null;
				}
				final String str = p.getProperty("default_ocr");
				defaultOCR = str == null ? 0 : Integer.parseInt(str);
			} catch (final Throwable t) {
				System.out.println("Settings error:");
				t.printStackTrace();
			}
		}
		if (username != null) {
			System.out.println("Attempting to authenticate...");
			try {
				if (authenticate()) {
					System.out.println("Auth valid.");
					new Thread(new Authenticator()).start();
					new EntryFrame(this).setVisible(true);
				} else {
					System.out.println("Auth invalid.");
				}
			} catch (IOException ex) {
				System.out.println("Error connecting: " + ex.toString());
				System.out.println("Note that the bot can only authenticate when the forums are online.");
			}
		} else {
			final AuthFrame authFrame = new AuthFrame("APOS", "Enter your forum login details.", null);
			authFrame.setIconImages(Constants.ICONS);
			authFrame.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Attempting to authenticate...");
					username = authFrame.getUsername();
					password = authFrame.getPassword();
					try {
						if (authenticate()) {
							storeProperties(new Properties());
							System.out.println("Auth valid.");
							authFrame.dispose();
							new Thread(new Authenticator()).start();
							new EntryFrame(BotLoader.this).setVisible(true);
						} else {
							System.out.println("Auth invalid. Try again.");
						}
					} catch (IOException ex) {
						System.out.println("Error connecting: " + ex.toString());
						System.out.println("Note that the bot can only authenticate when the forums are online.");
					}
				}
			});
			authFrame.setVisible(true);
		}
	}
	
	static Properties getProperties(String path) {
		Properties p = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(path);
			p.load(in);
			if (!p.containsKey("font")) {
				p.put("font", "");
			}
			return p;
		} catch (Throwable t) {
		} finally {
			try {
				in.close();
			} catch (Throwable t) {
			}
		}
		return null;
	}

	public void storeProperties(Properties p) {
		if (p == null) {
			p = getProperties(PROPERTIES_FILE);
		}
		p.put("auth_user", username);
		p.put("auth_pass", password);
		p.put("default_ocr", String.valueOf(defaultOCR));
		p.put("font", font == null ? "" : font);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(PROPERTIES_FILE);
			p.store(out, null);
		} catch (final Throwable t) {
			System.out.println("Error storing updated properties: " + t.toString());
		} finally {
			try {
				out.close();
			} catch (final Throwable t) {
			}
		}
	}
	
	public String getFont() {
		return font;
	}

	public void setDefaultOCR(int i) {
		defaultOCR = i;
	}

	public int getDefaultOCR() {
		return defaultOCR;
	}

	TextArea getConsoleTextArea() {
		return cTextArea;
	}

	void setConsoleFrameVisible(boolean b) {
		if (cFrame != null) {
			cFrame.setVisible(b);
		}
	}

	IClientInit getClientInit() {
		return init;
	}

	boolean authenticate() throws IOException {
		if (true) return true;
		if (username == null || password == null) {
			return false;
		}
		byte[] dumb_seed = new byte[16];
		Constants.RANDOM.nextBytes(dumb_seed);
		String seed_hex = hexString(dumb_seed);

		String server_response = new String(HTTPClient.load(
			"http://aposbot.com/newauth.php?user="
			+ username.trim().replace(" ", "%20") + "&pass="
			+ password.trim().replace(" ", "%20") + "&seed=" + seed_hex
			+ "&ver=" + VERSION, null, false), Constants.UTF_8);
		if (server_response.length() == 1) {
			switch (server_response.charAt(0)) {
			case '3':
				System.out.println("A new version of APOS has been released. Please update.");
				return false;
			case '5':
			case '7':
				System.out.println("You have been banned from using APOS.");
				return false;
			}
		}

		/*
		 * return md5($username.$password.$seed.$response);
		 */
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(username.getBytes(Constants.UTF_8));
			digest.update(password.getBytes(Constants.UTF_8));
			digest.update(seed_hex.getBytes(Constants.UTF_8));
			digest.update(new byte[] {
				'1'
			});
			if (hexString(digest.digest()).equals(server_response)) {
				return true;
			}
		} catch (NoSuchAlgorithmException ex) {
			throw new IOException("no such algorithm");
		}
		return false;
	}

	private static String hexString(byte[] in) {
		final char[] alphabet = "0123456789abcdef".toCharArray();
		char[] out = new char[in.length * 2];
		for (int i = 0; i < in.length; i++) {
			int v = in[i] & 0xFF;
			out[i * 2] = alphabet[v >>> 4];
			out[i * 2 + 1] = alphabet[v & 0x0F];
		}
		return new String(out);
	}
}
