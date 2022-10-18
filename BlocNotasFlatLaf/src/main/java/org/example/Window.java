package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.io.IOException;

public class Window extends JFrame {
    Controller controller = new Controller();

    Boolean ajuste;
    Boolean isEnabled = false;
    //Bloc components
    JTextArea textArea;

    JLabel pos;
    //elements
    JMenuBar menu;
    JMenu file;
    JMenu edit;
    JMenu themes;
    JMenu ayuda;
    JMenuItem openOption, newFileOption, saveOption, exitOption, imprimir,
            undoOption, pasteOption, deleteOption, saveAsOption, redoOption,
            cutOption, copyOption, intelliJ, darcula, about, verAyuda, ajusteLinea;
    JScrollPane scrollPane;

    //System functions
    UndoManager undo;
    Clipboard clip;


    public Window() {
        initComponents();
    }

    private void initComponents() {
        ajuste = true;

        menu = new JMenuBar();
        undo = new UndoManager();
        undo.setLimit(1000);
        clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        textArea = new JTextArea();
        scrollPane = new JScrollPane(textArea);

        file = new JMenu("File");
        edit = new JMenu("Edit");
        themes = new JMenu("Modos");
        ayuda = new JMenu("Ayuda");

        file.add(openOption = new JMenuItem("open"));
        file.add(newFileOption = new JMenuItem("new file"));
        file.add(saveOption = new JMenuItem("save"));
        file.add(saveAsOption = new JMenuItem("save as"));
        file.add(imprimir = new JMenuItem("print"));
        file.add(exitOption = new JMenuItem("exit"));

        edit.add(undoOption = new JMenuItem("undo"));
        edit.add(redoOption = new JMenuItem("redo"));
        edit.add(pasteOption = new JMenuItem("paste"));
        edit.add(deleteOption = new JMenuItem("delete"));
        edit.add(cutOption = new JMenuItem("cut"));
        edit.add(copyOption = new JMenuItem("copy"));

        themes.add(intelliJ = new JMenuItem("Copiado de IntelliJ"));
        themes.add(darcula = new JMenuItem("Darcula copiado también"));

        ayuda.add(about = new JMenuItem("About"));
        ayuda.add(verAyuda = new JMenuItem("Ver ayuda"));
        ayuda.add(ajusteLinea = new JMenuItem("Ajuste linea"));

        menu.add(file);
        menu.add(edit);
        menu.add(themes);
        menu.add(ayuda);

        pos = new JLabel("rows: "+textArea.getRows() + " - cols: "+textArea.getColumns());

        intelliJ.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("esto funciona");
                try {
                    UIManager.setLookAndFeel(new FlatIntelliJLaf());
                } catch (UnsupportedLookAndFeelException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        darcula.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel(new FlatDarculaLaf());
                } catch (UnsupportedLookAndFeelException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.about();
            }
        });

        verAyuda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.seekHelp();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        openOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = controller.openFile();
                textArea.setText(text);
            }
        });

        newFileOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String oldText = textArea.getText();
                controller.newFile(oldText);
                textArea.setText("");
            }
        });

        saveOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textArea.getText();
                controller.save(text);
            }
        });

        saveAsOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textArea.getText();
                controller.saveAs(text);
            }
        });

        imprimir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    textArea.print();
                } catch (PrinterException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        exitOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String oldText = textArea.getText();
                if(controller.askIfSave(oldText) == 0) {
                    controller.save(oldText);
                }
                System.exit(0);
            }
        });

        undoOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(undo.canUndo()){
                    undo.undo();
                    undoOption.setEnabled(true);
                }
                undoOption.setEnabled(false);
            }
        });

        redoOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(undo.canRedo()){
                    redoOption.setEnabled(true);
                    undo.redo();
                }
                redoOption.setEnabled(false);
            }
        });

        textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undo.addEdit(e.getEdit());
            }
        });

        pasteOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Transferable t = clip.getContents(null);
                if(t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)){
                    pasteOption.setEnabled(true);
                    try{
                        textArea.replaceSelection(""+t.getTransferData(DataFlavor.stringFlavor));
                    }catch (IOException | UnsupportedFlavorException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                pasteOption.setEnabled(false);
            }
        });

        deleteOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(textArea.getSelectedText() != null){
                    deleteOption.setEnabled(true);
                    textArea.replaceSelection("");
                }
                deleteOption.setEnabled(false);
            }
        });

        cutOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(textArea.getSelectedText() != null) {
                    cutOption.setEnabled(true);
                    StringSelection select = new StringSelection("" +textArea.getSelectedText());
                    clip.setContents(select, select);
                    textArea.replaceSelection("");
                }
                cutOption.setEnabled(false);
            }
        });

        copyOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(textArea.getSelectedText() != null){
                    copyOption.setEnabled(true);
                    StringSelection select = new StringSelection("" +textArea.getSelectedText());
                    clip.setContents(select, select);
                    textArea.replaceSelection("");                    clip.setContents(select, select);
                }
                copyOption.setEnabled(false);
            }
        });

        textArea.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                char key1 = e.getKeyChar();
                if(key1 == KeyEvent.VK_TAB){
                    textArea.replaceSelection("    ");
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                char key1 = e.getKeyChar();
                char key2 = e.getKeyChar();
                if(key1 == KeyEvent.VK_CONTROL && key2 == KeyEvent.VK_G){
                    String text = textArea.getText();
                    controller.save(text);
                }else if(key1 == KeyEvent.VK_CONTROL && key2 == KeyEvent.VK_W){
                    String oldText = textArea.getText();
                    if(controller.askIfSave(oldText) == 0) {
                        controller.save(oldText);
                    }
                    System.exit(0);
                }else if(key1 == KeyEvent.VK_CONTROL && key2 == KeyEvent.VK_N){
                    String oldText = textArea.getText();
                    controller.newFile(oldText);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}


        });

        textArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                textArea = (JTextArea) e.getSource();
                int caretPos = textArea.getCaretPosition();
                try {
                     int linea = textArea.getLineOfOffset(caretPos);
                     int columna = textArea.getLineStartOffset(linea);


                    pos.setText("rows: "+linea+ "- cols: "+columna );
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        ajusteLinea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ajuste = !ajuste;
            }
        });
        textArea.setLineWrap(ajuste);
        textArea.setWrapStyleWord(ajuste);

        setTitle("Bloc de notas");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(500, 600);
        setJMenuBar(menu);
        add(pos, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);
        getContentPane().setBackground(Color.BLUE);
        // Si uso el pack no muestra el text area aunque es utilizable, así que prescindo de
        // él porque después de 2 horas es lo único que veo que funciona
//        pack();
    }


}