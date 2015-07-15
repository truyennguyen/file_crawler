import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class crawler {

    private static ArrayList<File> arrTxtFiles = new ArrayList<File>();

    private static void findTxtFiles(String path){
        File dir = new File(path);
        File[] files = dir.listFiles();

        for(File file:files){
            String filename = file.getName();

            // If file is .txt type
            if(file.isFile() && filename.endsWith(".txt"))
                arrTxtFiles.add(file);

            // If file is .zip type, unzip & look for .txt files
            else if (file.isFile() && filename.endsWith(".zip")){
                String unzipFolderName = filename.substring(0, filename.lastIndexOf('.'));

                unzip(file.getAbsolutePath(), file.getParent());
                findTxtFiles(file.getParent() + '/' + unzipFolderName);
            }

            //recursive call
            else if(file.isDirectory())
                findTxtFiles(file.getPath());
        }
    }
    private static void unzip(String zipFile, String destinationFolder) {
        File directory = new File(destinationFolder);

        // if the output directory doesn't exist, create it
        if(!directory.exists())
            directory.mkdirs();

        // buffer for read and write data to file
        byte[] buffer = new byte[2048];

        try {
            FileInputStream fInput = new FileInputStream(zipFile);
            ZipInputStream zipInput = new ZipInputStream(fInput);

            ZipEntry entry = zipInput.getNextEntry();

            while(entry != null){
                String entryName = entry.getName();
                File file = new File(destinationFolder + File.separator + entryName);

                System.out.println("Unzip file " + entryName + " to " + file.getAbsolutePath());

                // create the directories of the zip directory
                if(entry.isDirectory()) {
                    File newDir = new File(file.getAbsolutePath());
                    if(!newDir.exists()) {
                        boolean success = newDir.mkdirs();
                        if(success == false) {
                            System.out.println("Problem creating Folder");
                        }
                    }
                }
                else {
                    FileOutputStream fOutput = new FileOutputStream(file);
                    int count = 0;
                    while ((count = zipInput.read(buffer)) > 0) {
                        // write 'count' bytes to the file output stream
                        fOutput.write(buffer, 0, count);
                    }
                    fOutput.close();
                }
                // close ZipEntry and take the next one
                zipInput.closeEntry();
                entry = zipInput.getNextEntry();
            }

            // close the last ZipEntry
            zipInput.closeEntry();

            zipInput.close();
            fInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        findTxtFiles("/home/nmt/Desktop/files2");
    }
}