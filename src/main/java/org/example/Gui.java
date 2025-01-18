package org.example;

import jdk.swing.interop.SwingInterOpUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.JSMLException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class Gui implements ActionListener{
    JButton button;
    File pdfFile;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public Gui(){
        JFrame frame = new JFrame();
        frame.setSize(1000,1000);
        frame.setTitle("Text to Speech");
        frame.setLayout(new FlowLayout());
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        button = new JButton("Click here to open file");
        button.addActionListener(this);
        button.setPreferredSize(new Dimension(400,100));
        button.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
        frame.setBackground(Color.BLACK);
        frame.add(button);
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //changes how filemanager looks when opened

        }catch (Exception e){
            System.out.println("Error");
        }
        frame.setVisible(true);

    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == button){
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            JFileChooser fileChooser = new JFileChooser();
            JList list = new JList();
            list.setLayoutOrientation(JList.VERTICAL);
            fileChooser.setPreferredSize(new Dimension(500,500));
            fileChooser.setFileFilter(filter);
            int response = fileChooser.showOpenDialog(null); //select file to open


            /*
            filechooser returns either 0 for choosing a file or
            1 for exiting, I want to check if the file is selected
            and if so load it into a file
             */

            if(response == JFileChooser.APPROVE_OPTION){

                pdfFile = new File(fileChooser.getSelectedFile().getAbsolutePath());

                try{
                    String text = getText(pdfFile);
                    sayPdfText(text);
                }catch (IOException f){
                    System.out.println("File failed to be read");
                }

            }
        }
    }

     void sayPdfText(String text){
        //sets freetts.voices as a key to  the path containing the voices in the KevinVoiceDirectory
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        try {
            Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
            javax.speech.synthesis.Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
            synthesizer.allocate();
            synthesizer.resume();
            synthesizer.speak(text, null);
            synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
            synthesizer.deallocate();
        } catch (EngineException | AudioException e) {
            System.out.println("An error occured...");
        } catch (InterruptedException | JSMLException e) {
            System.out.println("An error occured");
        }
    }

    static String getText(File pdfFile) throws IOException {
        PDDocument pdf = Loader.loadPDF(pdfFile); //load contents of pdf into empty pdf
        PDFTextStripper pdfText = new PDFTextStripper();
        String text = pdfText.getText(pdf); //filter out everything from the pdf aside from text and assign it to text
        pdf.close();
        return text;
    }
}
