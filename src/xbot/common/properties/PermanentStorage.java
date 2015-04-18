package xbot.common.properties;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.apache.commons.io.*;
import org.apache.log4j.Logger;

/**
 *
 * @author John
 */
public class PermanentStorage extends PermanentStorageProxy {

    private static Logger log = Logger.getLogger(PermanentStorage.class);
    
    private String fileName;

    public PermanentStorage() {
        super("/Properties.txt");
        fileName = "/Properties.txt";
    }
    
    public PermanentStorage(String destination) {
        super(destination);
        fileName = destination;
    }

    protected String readFromFile() {

        try {
            File c = new File(path);

            if (!c.exists()) {
                log.error("There was no properties file. Creating and continuing, "
                        + "but robot will be using default values. The only time you "
                        + "should see this error is right after the RoboRIO has been "
                        + "reimaged.");
                // we need to create the file if it doesn't exist already.
                c.createNewFile();
                return "";
            }

            String propertyString = FileUtils.readFileToString(c);
            
            if ((propertyString.isEmpty()) 
                    || (propertyString.length() == 0))
            {
                log.error("Properties file existed, but has no data. Robot operation will "
                        + "be severely compromised! Only default values will be available.");
            }
            return propertyString;
            
            

        } catch (IOException e) {
            log.error("IO Exception when reading the properties file at "
                    + path + ", Message to follow.");
            log.error(e.getMessage());
        }

        return "";
    }

    protected void writeToFile(String data) {
        
        if ((data.isEmpty()) || (data.length() == 0))
        {
            log.error("We are being asked to overwrite properties file with no data. "
                    + "Instead, save will be ignored, and we will keep old data.");
            return;
        }
        
        // General idea:
        //    1) Write to candidate file
        //    2) check candidate file
        //    3) If good, rename old file as backup, rename candidate file as primary
        //    4) If bad, error out and do nothing
        
        try {
            File primary = new File(path);
            if (!primary.exists()) {
                log.error("There was no properties file. " 
                        + "Barring a robot re-imaging, there should always be a properties file.");
            }
                
            log.info("Writing to candidate file");
            String candidateName = fileName + "Candidate";
            File candidate = new File(candidateName);
            String time = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
            File backup = new File(fileName + "Backup-"  + time + ".txt");
            
            // Write candidate file
            FileUtils.writeStringToFile(candidate, data);
            
            
            attemptToPromoteCandidate(primary, candidate, backup);
        } 
        catch (IOException e) {
            // TODO: Adjust for new logging system
            log.error("IO Exception when writing to the properties file at "
                    + path + ", Message to follow.");
            log.error(e.getMessage());
        }
    }

    private void attemptToPromoteCandidate(File primary, File candidate, File backup) throws IOException {
        
        log.info("Checking candidate file");
        
        long minBytes = 5;
        if (candidate.length() > minBytes) {
            log.info(String.format(
                            "Candidate file is %d bytes, greater than the minimum of %d bytes", 
                            candidate.length(), 
                            minBytes));
            
            createBackupCopy(primary, backup, minBytes);            
            
            promoteCandidate(primary, candidate);
            
            verifyCandidatePromotionSuccessful(primary, backup, minBytes);
            
        }
        else
        {
            log.error(String.format("Candidate file was below %d bytes", minBytes));
            log.info("Destroying candidate file, and bypassing save/backup operations.");
            candidate.delete();
        }
    }

    private void verifyCandidatePromotionSuccessful(File primary, File backup,
            long minBytes) throws IOException {
        if (primary.length() < minBytes) {
            log.error("Something has gone wrong with candidate->primary rename. "
                    + "New primary is too small. "
                    + "Attempting to recover from most recent backup.");
            
            primary.delete();
            FileUtils.copyFile(backup, primary);
            
            if (primary.length() < minBytes) {
                log.error("Unable to recover from backup. Properties lost.");
            }
            else {
                log.info("Backup recovery successful.");
            }
        }
    }

    private void promoteCandidate(File primary, File candidate) {
        log.info("Deleting old properties file");
        primary.delete();
        
        log.info("Renaming candidate to primary");
        candidate.renameTo(primary);
    }

    private void createBackupCopy(File primary, File backup, long minBytes)
            throws IOException {
        // save a copy for debugging purposes
        if (primary.exists())
        {
            log.info("Saving a copy of the current properties file...");
            FileUtils.copyFile(primary, backup);
            
            if (backup.length() < minBytes)
            {
                log.error("Something has gone wrong when saving backup file!");
                log.error("Will still attempt to save good primary file.");
                backup.delete();
            }
            else
            {
                log.info("File copied.");
            }    
        }
    }
}
