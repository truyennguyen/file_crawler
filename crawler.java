import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class crawler {
    private static ArrayList<File> arrTextFiles;
    private static Map<String, Integer> hm;
    private static Map<String, Integer> sortedHm;

    /* Constructor */
    public crawler() {
        this.arrTextFiles = new ArrayList<File>();
        this.hm = new HashMap<String, Integer>();
        this. sortedHm = new LinkedHashMap<String, Integer>();
    }

    /* Find the .txt files in the path
     * return true if the path exists, otherwise return false */
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

    /* Unzip the .zip file */
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

    /* Read .txt files and count how many words for each words */
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

    /* print histogram pattern by '*'
     *  If isSorted is true, print sorted by word counts
     *  if isSorted is false, print unsorted by word counts*/
    private static void printHistogramUtil(boolean isSorted){
        Map<String, Integer> tempMap;
        if(isSorted)
            tempMap = sortedHm;
        else
            tempMap = hm;

        for(Map.Entry<String, Integer> element: tempMap.entrySet()){
            System.out.print(element.getKey() + ": ");
            for(int i = 0; i < element.getValue(); i++)
                System.out.print('*');
            System.out.println();
        }
    }

    /* Sort the word count map by values */
    private static void sortMapByValues() {
        List mapKeys = new ArrayList(hm.keySet());
        List mapValues = new ArrayList(hm.values());
        Collections.sort(mapValues, Collections.reverseOrder());
        Collections.sort(mapKeys, Collections.reverseOrder());

        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                String comp1 = hm.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)){
                    hm.remove(key);
                    mapKeys.remove(key);

                    sortedHm.put((String) key, (Integer) val);
                    break;
                }
            }
        }
    }

    /* Check words count by word, for testing purpose */
    private static int checkCount(String str){
        return hm.get(str);
    }

    /* Print histogram pattern based on the word count in the .txt files in the path
     * if isSorted is true, print sorted histogram pattern based on the word counts
     * if isSorted is false, print unsorted histogram pattern based on the word counts*/
    public static void printHistogram(String path, boolean isSorted){
        if(findTxtFiles(path)){
            readFiles();
            if(isSorted) {
                sortMapByValues();
                printHistogramUtil(true);
            }
            else
                printHistogramUtil(false);
        }
    }

    public static void main(String[] args) {
        crawler fileCrawler = new crawler();
        fileCrawler.printHistogram("/home/nmt/Desktop/file1", true);
    }
}