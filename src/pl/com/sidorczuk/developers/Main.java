package pl.com.sidorczuk.developers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadBuilder;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

public class Main {
	private static String access_token = "";
	private static String download_path = "";
	static DbxClientV2 client;

	public static void main(String args[]) throws DbxException {
		if (args.length == 0) {
			System.out.println("You didn't provide path to download. Exiting...");
			System.exit(1);
		}
		if (args.length > 1) {
			System.out.println("Too many arguments. Exiting...");
			System.exit(1);
		}
		String fileSeparator = File.separator;
		if ((args[0].substring(args[0].length() - 1)).equals(fileSeparator)) {
			args[0] = args[0].substring(0, args[0].length() - 1);
		}
		download_path = args[0];
		String key_file_name = "access_key.txt";
		File access_key = new File(key_file_name);
		if (!access_key.exists()) {
			createAccessKey(key_file_name);
		}
		Scanner scanner;
		try {
			scanner = new Scanner(new File(key_file_name));
			while (scanner.hasNextLine()) {
				access_token = scanner.nextLine();
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Can't find " + key_file_name + " Exiting...");
			System.exit(1);
		}
		if (access_token.equals("")) {
			System.out.print("Please provide access token to dropbox: ");
			Scanner readFromConsole = new Scanner(System.in);
			access_token = readFromConsole.nextLine();
			readFromConsole.close();
			writeAccessKey(key_file_name, access_token);
		}
		// Create Dropbox client
		DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial");
		client = new DbxClientV2(config, access_token);
		FullAccount account = client.users().getCurrentAccount();
		System.out.println(account.getName().getDisplayName());
		ListFolderResult result = client.files().listFolderBuilder("").withIncludeDeleted(false).withRecursive(true).withIncludeMediaInfo(true)
                .start();
		while (true) {
			for (Metadata metadata : result.getEntries()) {
				if (metadata instanceof FileMetadata) {
					downloadSingleFile(metadata.getPathLower());
				} else if (metadata instanceof FolderMetadata) {
					String folder_dir = download_path + metadata.getPathLower().replace("/", File.separator);
					File dir = new File(folder_dir);
					System.out.println("Creating folder: " + folder_dir);
					dir.mkdir();
				}
			}

			if (!result.getHasMore()) {
				break;
			}

			result = client.files().listFolderContinue(result.getCursor());
		}

	}

	public static void downloadSingleFile(String file_path) throws DbxException {
		DownloadBuilder downloader = client.files().downloadBuilder(file_path);
		String full_file_path = download_path + file_path.replace("/", File.separator);
		try (OutputStream out = new FileOutputStream(full_file_path)) {
			System.out.println("Downloading: " + full_file_path);
			downloader.download(out);		
		} catch (FileNotFoundException e) {
			System.out.println("Can't write/create file: " + full_file_path);
		} catch (IOException e) {
			System.out.println("Can't write/create file: " + full_file_path);
		}
	}

	public static void createAccessKey(String key_file_name) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(key_file_name, "UTF-8");
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("Can't write or find a file: " + key_file_name + " Check and run program again");
			System.exit(1);
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported File Encoding. Exiting...");
			System.exit(1);
		}
	}

	public static void writeAccessKey(String key_file_name, String key) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(key_file_name, "UTF-8");
			writer.println(key);
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("Can't write or find a file: " + key_file_name + " Check and run program again");
			System.exit(1);
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported File Encoding. Exiting...");
			System.exit(1);
		}
	}
}
