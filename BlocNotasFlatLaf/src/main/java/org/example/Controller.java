package org.example;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.net.URI;

public class Controller extends Component {
    File file = null;
    String fileName = null;

    public String newFile(String text) {
        String result = "";
        int saveOrNot = askIfSave(text);
        if (saveOrNot == 0) {
            save(text);
        }
        return result;
    }

    public int askIfSave(String text) {
        Object[] options = new Object[] { "Si", "No"};
        return JOptionPane.showOptionDialog( null,"le gustaría guardar el texto actual?", "¿guardar progreso?",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, "Si");
    }

    public String openFile() {
        String result = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));
        chooser.setAcceptAllFileFilterUsed(false);
        int option = chooser.showOpenDialog(this);
        file = chooser.getSelectedFile();
        fileName = chooser.getName();
        if (option == JFileChooser.APPROVE_OPTION){
            StringBuilder sBuilder = new StringBuilder();
            try {
                FileReader fReader = new FileReader(file);
                BufferedReader br = new BufferedReader(fReader);
                String line = br.readLine();
                while (line != null) {
                    sBuilder.append(line + "\n");
                    line = br.readLine();
                }
                result = sBuilder.toString();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        return result;
    }

    public void saveAs(String text) {
        JFileChooser chooser = new JFileChooser("~");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));
        chooser.setAcceptAllFileFilterUsed(false);

        int option = chooser.showSaveDialog(this);
        file = chooser.getSelectedFile();

        if (option == JFileChooser.APPROVE_OPTION) {
            if (!file.getPath().endsWith(".txt")){
                file = new File(file.getPath() + ".txt" );
                fileName = file.getName();
            }
            if (file != null) {
                if (!file.exists()) {
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(text);
                        JOptionPane.showMessageDialog(null, "archivo guardado correctamente",
                                "Info", JOptionPane.INFORMATION_MESSAGE);
                    } catch(IOException ex) {
                        JOptionPane.showMessageDialog(null, "ha ocurrido un error durante el guardado del archivo",
                                "Error", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "'" + file.getName() + "' el archivo ya existe, cambie el nombre.",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    public void save(String text) {
        if (file != null) {
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else saveAs(text);
    }

    public void about() {
        JOptionPane.showMessageDialog(null, "Hecho por Iván Azagra Troya", "Sobre el autor: ",JOptionPane.PLAIN_MESSAGE, null);
    }

    public void seekHelp() throws IOException {
        Desktop.getDesktop().browse(URI.create("https://www.linkedin.com/in/iv%C3%A1n-azagra-troya-2a7599215/"));
    }
}
