package imapServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread implements Runnable {
	private Socket socket;
	private int cmdId;

	public ServerThread(Socket socket, int cmdId) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.cmdId = cmdId;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

			String message = "";
			File file = new File("mail.txt");
			Boolean log = false; // FLAG KOJI PROVERAVA DA LI SMO ULOGAVNI

			while (!message.startsWith("logout")) {

				message = in.readLine();
				if (message.startsWith("login")) {
					out.println("cmdId" + cmdId + " OK LOGIN completed");
					log = true;
					cmdId++; // SVAKA KOMANDA IMA SVOJ ID
				}
				if (message.startsWith("select") && log) {

					out.println("*" + prebrojMejlove(file) + " new messages in inbox");
					out.println("cmdId" + cmdId + " OK SELECT completed");
					cmdId++;

				}
				if (message.startsWith("fetch") && log) { // UCITAVANJE MEJLOVA IZ FAJLA
					String token[] = message.split(" ");
					int kolicina = Integer.valueOf(token[1]);
					out.println("* " + kolicina + "fetch");
					String poruka = ucitajIzFajla(file, kolicina);
					out.println(poruka);
					out.println("cmdId" + cmdId + " OK FETCH completed");
					cmdId++;

				}
				if (message.startsWith("delete") && log) {
					String token[] = message.split(" ");
					int kolicina = Integer.valueOf(token[1]);
					removeLineFromFile(file, kolicina);
					out.println("* " + kolicina + "deleted");
					out.println("cmdId" + cmdId + " OK DELETE completed");
					cmdId++;

				}

				if (!log) {
					out.println("You have to login first");
				} else {
					out.println("cmdId" + cmdId + " BAD command (unrecognized command or command syntax error)");

				}
			}
			out.println("* BYE IMAP server terminating connection");
			out.println("cmdId" + cmdId + " OK LOGOUT completed");
			socket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String ucitajIzFajla(File file, int kolicina) {
		String buffer = "";
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null && kolicina > 0) {
				kolicina--;
				buffer = buffer.concat("\n" + line);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(buffer);
		return buffer;

	}

	private int prebrojMejlove(File file) {
		int linije = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {

				linije++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return linije;

	}

	public void removeLineFromFile(File inFile, int brisanje) {
		int linija = 0;
		String line = null;

		// Construct the new file that will later be renamed to the original filename.
		File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(inFile));
		} catch (FileNotFoundException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(tempFile));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		// Read from the original file and write to the new
		// unless content matches data to be removed.
		try {
			while ((line = br.readLine()) != null) {
				linija++;
				if (brisanje < linija) {
					pw.println(line);
					pw.flush();
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		pw.close();
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Delete the original file
		if (!inFile.delete()) {
			System.out.println("Could not delete file");
			return;
		}

		// Rename the new file to the filename the original file had.
		if (!tempFile.renameTo(inFile))
			System.out.println("Could not rename file");

	}

}
