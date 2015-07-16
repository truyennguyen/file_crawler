import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Scanner;

public class crawler {
    private static ArrayList<File> arrTextFiles = new ArrayList<File>();
    private static Map<String, Integer> hm = new HashMap<String, Integer>();

    private static boolean findTxtFiles(String path){
        File dir = new File(path);

        if(dir.exists()) {
            File[] files = dir.listFiles();

            for (File file : files) {
                String filename = file.getName();

                //If file is .txt type
                if (file.isFile() && filename.endsWith(".txt"))
                    arrTextFiles.add(file);

                //If file is .zip type, unzip & look for .txt files
                else if (file.isFile() && filename.endsWith(".zip")) {
                    String unzipFolderName = filename.substring(0, filename.lastIndexOf('.'));

                    unzip(file.getAbsolutePath(), file.getParent());
                    findTxtFiles(file.getParent() + '/' + unzipFolderName);
                }

                //recursive call
                else if (file.isDirectory())
                    findTxtFiles(file.getPath());
            }
            return true;
        }
        else
        {
            System.out.print("Directory not found - " + path);
            return false;
        }
    }

    private static void unzip(String zipFile, String destinationFolder) {
        File directory = new File(destinationFolder);

        //if the output directory doesn't exist, create it
        if(!directory.exists())
            directory.mkdirs();

        //buffer for read and write data to file
        byte[] buffer = new byte[2048];

        try {
            FileInputStream fInput = new FileInputStream(zipFile);
            ZipInputStream zipInput = new ZipInputStream(fInput);

            ZipEntry entry = zipInput.getNextEntry();

            while(entry != null){
                String entryName = entry.getName();
                File file = new File(destinationFolder + File.separator + entryName);

                System.out.println("Unzip file " + entryName + " to " + file.getAbsolutePath());

                //create the directories of the zip directory
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
                        //write 'count' bytes to the file output stream
                        fOutput.write(buffer, 0, count);
                    }
                    fOutput.close();
                }
                //close ZipEntry and take the next one
                zipInput.closeEntry();
                entry = zipInput.getNextEntry();
            }

            //close the last ZipEntry
            zipInput.closeEntry();

            zipInput.close();
            fInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readFiles() {
        try {
            for (File file : arrTextFiles) {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNext()) {
                    String word = scanner.next();

                    word = word.replaceAll("\\W(?<!')", "").toLowerCase();  //trim the word
                    //the word exists in hash map
                    if (hm.containsKey(word)) {
                        int tempCount = hm.get(word);
                        hm.put(word, tempCount + 1);
                    }
                    //the word not exists in hash map
                    else
                        hm.put(word, 1);
                }
            }
        } catch (Exception e) {
            System.out.print("Read file - Exception thrown is " + e);
        }
    }

    private static void printHistogramUtil(){
        for(Map.Entry<String, Integer> element:hm.entrySet()){
            System.out.print(element.getKey() + ": ");
            for(int i = 0; i < element.getValue(); i++)
                System.out.print('*');
            System.out.println();
        }
    }

    private static int checkCount(String str){
        return hm.get(str);
    }

    public static void printHistogram(String path){
        if(findTxtFiles(path)){
            readFiles();
            printHistogramUtil();
        }
    }

    public static void main(String[] args) {
        printHistogram("/home/nmt/Desktop/file1");
    }
}