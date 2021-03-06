package solver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class that reads the tool.config file in the default folder on the same level
 * as the .jar file.
 */
public class ConfigReader {

  // class fields
  public static String solverPath;
  public static String solverName;
  public static String filePath;
  public static String fileName;
  public static String platform;

  /**
   * Static method that reads the config file in the default location
   * and sets the values of the relevant file paths.
   * @return success
   */
  public static boolean readConfig() {
    // read the tool.config file in the default location
    Properties prop = new Properties();
    // InputStream in =
    // Config_Reader.class.getClassLoader().getResourceAsStream("/solver/tool.config");
    InputStream in;
    try {
      in = new FileInputStream("./configs/tool.config");
      prop.load(in);
      in.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
    // set the paths and the name for the .smt2 file
    solverPath = prop.getProperty("SOLVER_PATH");
    solverName = prop.getProperty("SOLVER_NAME");
    filePath = prop.getProperty("SMT2_PATH");
    fileName = prop.getProperty("DEFAULT_FILE_NAME");
    platform = prop.getProperty("PLATFORM");

    return true;
  }

}
