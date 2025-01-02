package org.example;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import java.io.*;
import java.util.Locale;

public class Main {

    // todo: At new lines the speaking pauses, try finding out why and hopefully fix it
    public static void main(String[] args) throws IOException {
       String text = getText(new File("C:/Users/ompan/Text2Speech/src/main/java/org/example/Best Ways To Find Internships.pdf"));
       sayPdfText(text);
    }

    static void sayPdfText(String text){
        int index = 0; //used when splitting the text into an array
        //sets freetts.voices as a key to  the path containing the voices in the KevinVoiceDirectory
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        String [] words = text.split("\n");
        try {
            Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
            javax.speech.synthesis.Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
            synthesizer.allocate();
            synthesizer.resume();
            while (index < words.length) {
                synthesizer.speakPlainText(words[index], null);
                synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
                index++;
            }
            synthesizer.deallocate();
        } catch (EngineException | AudioException | InterruptedException e) {
            System.out.println("An error occured...");
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