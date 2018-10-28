package camunda;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Classe per l'apertura dei file bpmn 
 * @author PROSLabTeam
 *
 */
public class BpmnFileOpener {
	
	/**
	 * Crea una finestra per la scelta del file a partire dalla directory corrente
	 * @return il path del file scelto
	 */
	public String openFile() {
		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("File BPMN", "bpmn");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
		   System.out.println("Hai scelto di aprire il file: " + chooser.getSelectedFile().getName());
		} else {
			System.exit(0);
		}
		return chooser.getSelectedFile().getName();
	}
}
