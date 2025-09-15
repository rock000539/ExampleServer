package test;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import jcifs.CIFSContext;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbFile;

public class SmbStandaloneTest {
	public static void main(String[] args)
	{
		String server = "localtest"; // just for log
		String url = "localhost";    // SMB host
		String share = "smbShare";   // Windows 上建立的 share 名稱
		String username = "parker.huang";
		String password = "1qaz2wsxBB";
		String fileName = "test_" + System.currentTimeMillis() + ".txt";
		String smbUrl = String.format("smb://%s/%s/%s", url, share, fileName);

		try
		{
			// 準備 CIFS Context
			NtlmPasswordAuthenticator auth = new NtlmPasswordAuthenticator(url, username, password);
			CIFSContext base = SingletonContext.getInstance();
			CIFSContext context = base.withCredentials(auth);

			System.out.println("Uploading to: " + smbUrl);

			try (SmbFile smbFile = new SmbFile(smbUrl, context);
					OutputStream os = smbFile.getOutputStream())
			{
				String content = "Hello SMB test at " + System.currentTimeMillis();
				os.write(content.getBytes(StandardCharsets.UTF_8));
				os.flush();
			}

			System.out.println("Upload succeeded!");
		}
		catch (Exception e)
		{
			System.err.println("Upload failed:");
			e.printStackTrace();
		}
	}
}
