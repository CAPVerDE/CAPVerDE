package gui;

import solver.ConfigReader;

/**
 * The class that loads the GUI.
 * Also checks the platform and loads the corresponding SWT library.
 */
public class GuiLoader {


	/**
	 * The main function of the project.
	 * This calls the {@link solver.ConfigReader#readConfig() readConfig()} method
	 * and the {@link #checkPlatform() checkPlatform()} method. After having initialized,
	 * the method {@link Gui#Gui() Gui()} starts the GUI.
	 * 
	 * @param args
	 * 			the arguments for the main function (are currently ignored)
	 */
	public static void main(String[] args) {
		// start the program
		if (!ConfigReader.readConfig()) {
			// Could not find or read the config file
			System.err.println("Config file could not be read! Exiting...");
			System.exit(1);
		}
		new Gui();
	}

}
