// File:         Encoder.java
// Created:      8/10/2011
// Original Author:Jason Appelbaum Jason@Hak5.org 
// Author:       Dnucna
// Modified:     8/18/2012
// Modified:	 11/9/2013 midnitesnake "added COMMAND-OPTION"
// Modified:     1/3/2013 midnitesnake "added COMMAND"
// Modified:     1/3/2013 midnitesnake "added REPEAT X"
// Modified:	 2/5/2013 midnitesnake "added ALT-SHIFT"
// Modified:     4/18/2013 midnitesnake "added more user feedback"
// Modified:	 5/2/2013 midnitesnake "added skip over empty lines"
// Modified:     1/12/2014 Benthejunebug "added ALT-TAB"
// Modified:	 9/13/2016 rbeede "added STRING_DELAY n text"

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

import java.util.Properties;

public class Encoder {
        /* contains the keyboard configuration */
        private static Properties keyboardProps = new Properties();
        /* contains the language layout */
        private static Properties layoutProps = new Properties();
        private static String version = "2.6.4";
        private static Boolean debug=false;
    
        public static void main(String[] args) {
                String helpStr = "Hak5 Duck Encoder "+version+"\n\n"
                        + "Uso: duckencode -i [file ..]\t\t\tcodificar arquivo especificado\n"
                        + "   ou: duckencode -i [file ..] -o [file ..]\tcodificar arquivo especificado\n\n"
                        + "Argumentos:\n"
                        + "   -i [file ..] \t\tArquivo de entrada\n"
                        + "   -o [file ..] \t\tArquivo de saída\n"
                        + "   -l [file ..] \t\tEsquema do Teclado (us/fr/pt ou uma localização para um ficheiro de propriedades)\n\n"
                        + "Commandos Script:\n"
                        + "   ALT [key name] (ex: ALT F4, ALT SPACE)\n"
                        + "   CTRL | CONTROL [key name] (ex: CTRL ESC)\n"
                        + "   CTRL-ALT [key name] (ex: CTRL-ALT DEL)\n"
                        + "   CTRL-SHIFT [key name] (ex: CTRL-SHIFT ESC)\n"
                        + "   DEFAULT_DELAY | DEFAULTDELAY [Tempo em milissegundos] (alterar o atraso entre cada comando)\n"
                        + "   DELAY [Tempo em milissegundos] (usado para substituir temporariamente o atraso padrão)\n"
                        + "   GUI | WINDOWS [key name] (ex: GUI r, GUI l)\n"
                        + "   REM [anything] (usado para comentar seu código, sem obrigação :) )\n"
                        + "   ALT-SHIFT (swap language)\n"
                        + "   SHIFT [key name] (ex: SHIFT DEL)\n"
                        + "   STRING [qualquer personagem do seu layout]\n"
                        + "   STRING_DELAY [Number] [qualquer personagem do seu layout]	(O número é ms de atraso entre cada caractere)\n"
                        + "   REPEAT [Number] (Repita a última instrução N vezes)\n"
                        + "   [key name] (qualquer coisa em keyboard.properties)";                        

        String inputFile = null;
        String outputFile = null;
        String layoutFile = null;

        if (args.length == 0) {
                System.out.println(helpStr);
                System.exit(0);
        }

        for (int i = 0; i < args.length; i++) {
                if (args[i].equals("--gui") || args[i].equals("-g")) {
                        System.out.println("Launch GUI");
                } else if (args[i].equals("--help") || args[i].equals("-h")) {
                        System.out.println(helpStr);
                } else if (args[i].equals("-i")) {
                        // encode file
                        inputFile = args[++i];
                } else if (args[i].equals("-o")) {
                        // output file
                        outputFile = args[++i];
                } else if (args[i].equals("-l")) {
                        // output file
                        layoutFile = args[++i];
                } else if (args[i].equals("-d")) {
                    // output file
                    debug=true;
                } else {
                        System.out.println(helpStr);
                        break;
                }
        }
            
        System.out.println("Hak5 Duck Encoder "+version+"\n");
        
        if (inputFile != null) {
                String scriptStr = null;

                if (inputFile.contains(".rtf")) {
                        try {
                                FileInputStream stream = new FileInputStream(inputFile);
                                RTFEditorKit kit = new RTFEditorKit();
                                Document doc = kit.createDefaultDocument();
                                kit.read(stream, doc, 0);

                                scriptStr = doc.getText(0, doc.getLength());
                                System.out.println("Carregando RTF .....\t\t[ OK ]");
                        } catch (IOException e) {
                                System.out.println("Erro com arquivo de entrada!");
                        } catch (BadLocationException e) {
                                System.out.println("Erro com arquivo de entrada!");
                        }
                    
                } else {
                        DataInputStream in = null;
                        try {
                                File f = new File(inputFile);
                                byte[] buffer = new byte[(int) f.length()];
                                in = new DataInputStream(new FileInputStream(f));
                                in.readFully(buffer);
                                scriptStr = new String(buffer);
                                System.out.println("Carregando RTF .....\t\t[ OK ]");
                        } catch (IOException e) {
                                System.out.println("Erro com arquivo de entrada!");
                        } finally {
                                try {
                                        in.close();
                                } catch (IOException e) { /* ignore it */
                                }
                        }
                }
                loadProperties((layoutFile == null) ? "us" : layoutFile);
                
                encodeToFile(scriptStr, (outputFile == null) ? "inject.bin"
                                : outputFile);
                }
            
        }
        
        private static void loadProperties (String lang){
                InputStream in;
                ClassLoader loader = ClassLoader.getSystemClassLoader ();
                try {
                        in = loader.getResourceAsStream("keyboard.properties");
                        if(in != null){
                                keyboardProps.load(in);
                                in.close();
                                System.out.println("Carregando arquivo de teclado.....\t[ OK ]");
                        }else{
                                System.out.println("Erro com keyboard.properties!");
                                System.exit(0);
                        }
                } catch (IOException e) {
                        System.out.println("Erro com keyboard.properties!");
                }
                        
                try {
                        in = loader.getResourceAsStream(lang + ".properties");
                        if(in != null){
                                layoutProps.load(in);
                                in.close();
                                System.out.println("Carregando arquivo de idioma.....\t[ OK ]");
                        }else{
                                if(new File(lang).isFile()){
                                        layoutProps.load(new FileInputStream(lang));
                                        System.out.println("Carregando arquivo de idioma .....\t[ OK ]");
                                } else{
                                        System.out.println("Layout.propriedades externas não encontradas!");
                                        System.exit(0);
                                }
                        }
                } catch (IOException e) {
                        System.out.println("Erro com layout.properties!");
                        System.exit(0);
                }

        }
        private static void encodeToFile(String inStr, String fileDest) {

                inStr = inStr.replaceAll("\\r", ""); // CRLF Fix
                String[] instructions = inStr.split("\n");
                String[] last_instruction = inStr.split("\n");
                List<Byte> file = new ArrayList<Byte>();
                int defaultDelay = 0;
                int loop =0;
                boolean repeat=false;
                System.out.println("Carregando DuckyScript .....\t[ OK ]");
                if(debug) System.out.println("\nComandos de análise:");
                for (int i = 0; i < instructions.length; i++) {
                        try {
                                boolean delayOverride = false;
                                String commentCheck = instructions[i].substring(0, 2);
                                if (commentCheck.equals("//"))
                                        continue;
				if (instructions[i].equals("\n"))
					continue;
                               String[] instruction = instructions[i].split(" ", 2);
                                
                                if(i>0){
                                		last_instruction=instructions[i-1].split(" ", 2);
                                		last_instruction[0].trim();
                                		if (last_instruction.length == 2) {
                                        		last_instruction[1].trim();
                                		}
                                }else{
                                		last_instruction=instructions[i].split(" ", 2);
                                		last_instruction[0].trim();
                                		if (last_instruction.length == 2) {
                                        		last_instruction[1].trim();
                                		}
                                }

                                instruction[0].trim();

                                if (instruction.length == 2) {
                                        instruction[1].trim();
                                }

								if (instruction[0].equals("REM")){
									continue;
								}
								if (instruction[0].equals("REPEAT")){
									loop=Integer.parseInt(instruction[1].trim());
									repeat=true;
								}else{
									repeat=false;
									loop=1;
								}
								while(loop>0){
									if (repeat){
										instruction=last_instruction;
										//System.out.println(Integer.toString(instruction.length));
									}
								if (debug) System.out.println(java.util.Arrays.toString(instruction));
                                	if (instruction[0].equals("DEFAULT_DELAY")
                                                || instruction[0].equals("DEFAULTDELAY")) {
                                      	  defaultDelay = Integer.parseInt(instruction[1].trim());
                                       	 delayOverride = true;
                                	} else if (instruction[0].equals("DELAY")) {
                                        int delay = Integer.parseInt(instruction[1].trim());
                                        while (delay > 0) {
                                                file.add((byte) 0x00);
                                                if (delay > 255) {
                                                        file.add((byte) 0xFF);
                                                        delay = delay - 255;
                                                } else {
                                                        file.add((byte) delay);
                                                        delay = 0;
                                                }
                                        }
                                        delayOverride = true;
                                	} else if (instruction[0].equals("STRING")) {
                                        for (int j = 0; j < instruction[1].length(); j++) {
                                                char c = instruction[1].charAt(j);
                                                addBytes(file,charToBytes(c));
                                        }
                                	} else if (instruction[0].equals("STRING_DELAY")) {
                                		final String[] twoOptions = instruction[1].split(" ", 2);
                                		final int delayMillis = Integer.parseInt(twoOptions[0].trim());
                                		final String userText = twoOptions[1].trim();
                                		
                                		if(debug)  System.out.println(delayMillis);
                                		if(debug)  System.out.println(userText);
                                		
                                        for (int j = 0; j < userText.length(); j++) {
                                                char c = userText.charAt(j);
                                                addBytes(file,charToBytes(c));
                                                
                                                // Now insert the delay before the next character (and after the last is provided)
                                                for(int counter = delayMillis; counter > 0; counter -= 0xFF) {
                                                	file.add((byte) 0x00);
                                                	if(counter > 0xFF) {
                                                		file.add((byte) 0xFF);
                                                	} else {
                                                		file.add((byte) counter);  // Last one
                                                	}
                                                }
                                        }
                                	} else if (instruction[0].equals("CONTROL")
                                                || instruction[0].equals("CTRL")) {
                                        if (instruction.length != 1){
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add(strToByte(keyboardProps.getProperty("MODIFIERKEY_CTRL")));
                                        } else {
                                                file.add(strToByte(keyboardProps.getProperty("KEY_LEFT_CTRL")));
                                                file.add((byte) 0x00);
                                        }                               
                                	} else if (instruction[0].equals("ALT")) {
                                        if (instruction.length != 1){
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add(strToByte(keyboardProps.getProperty("MODIFIERKEY_ALT")));
                                        } else {
                                                file.add(strToByte(keyboardProps.getProperty("KEY_LEFT_ALT")));
                                                file.add((byte) 0x00);
                                        }
                                	} else if (instruction[0].equals("SHIFT")) {
                                        if (instruction.length != 1) {
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add(strToByte(keyboardProps.getProperty("MODIFIERKEY_SHIFT")));
                                        } else {
                                                file.add(strToByte(keyboardProps.getProperty("KEY_LEFT_SHIFT")));
                                                file.add((byte) 0x00);
                                        }
                                	} else if (instruction[0].equals("CTRL-ALT")) {
                                        if (instruction.length != 1) {
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add((byte) (strToByte(keyboardProps.getProperty("MODIFIERKEY_CTRL"))
                                                                | strToByte(keyboardProps.getProperty("MODIFIERKEY_ALT"))));
                                        } else {
                                                continue;
                                        }
                                	} else if (instruction[0].equals("CTRL-SHIFT")) {
                                        if (instruction.length != 1) {
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add((byte) (strToByte(keyboardProps.getProperty("MODIFIERKEY_CTRL"))
                                                                | strToByte(keyboardProps.getProperty("MODIFIERKEY_SHIFT"))));
                                        } else {
                                                continue;
                                        }
                                    } else if (instruction[0].equals("COMMAND-OPTION")) {
                                        if (instruction.length != 1) {
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add((byte) (strToByte(keyboardProps.getProperty("MODIFIERKEY_KEY_LEFT_GUI"))
                                                                | strToByte(keyboardProps.getProperty("MODIFIERKEY_ALT"))));
                                        } else {
                                                continue;
                                        }
                                	} else if (instruction[0].equals("ALT-SHIFT")) {
                                        if (instruction.length != 1) {
                                            file.add(strInstrToByte(instruction[1]));
                                            file.add((byte) (strToByte(keyboardProps.getProperty("MODIFIERKEY_LEFT_ALT"))
                                                                | strToByte(keyboardProps.getProperty("MODIFIERKEY_SHIFT")))
                                                    );
                                        } else {
                                                file.add(strToByte(keyboardProps.getProperty("KEY_LEFT_ALT")));
                                                                                                file.add((byte) (strToByte(keyboardProps.getProperty("MODIFIERKEY_LEFT_ALT"))
                                                                | strToByte(keyboardProps.getProperty("MODIFIERKEY_SHIFT")))
                                                        );
                                        }
                                	} else if (instruction[0].equals("ALT-TAB")){
                                        if (instruction.length == 1) {
                                            file.add(strToByte(keyboardProps.getProperty("KEY_TAB")));
                                            file.add(strToByte(keyboardProps.getProperty("MODIFIERKEY_LEFT_ALT")));
                                        } else{
                                            // do something?
                                        }
                                    } else if (instruction[0].equals("REM")) {
                                        /* no default delay for the comments */
                                        delayOverride = true;
                                        continue;
                                	} else if (instruction[0].equals("WINDOWS")
                                                || instruction[0].equals("GUI")) {
                                        if (instruction.length == 1) {
                                                file.add(strToByte(keyboardProps.getProperty("MODIFIERKEY_LEFT_GUI")));
                                                file.add((byte) 0x00);
                                        } else {
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add(strToByte(keyboardProps.getProperty("MODIFIERKEY_LEFT_GUI")));
                                        }
                                	} else if (instruction[0].equals("COMMAND")){
                                        if (instruction.length == 1) {
                                                file.add(strToByte(keyboardProps.getProperty("KEY_COMMAND")));
                                                file.add((byte) 0x00);
                                        } else {
                                                file.add(strInstrToByte(instruction[1]));
                                                file.add(strToByte(keyboardProps.getProperty("MODIFIERKEY_LEFT_GUI")));
                                        }
                                	}else {
                                        /* treat anything else as a key */
                                        file.add(strInstrToByte(instruction[0]));
                                        file.add((byte) 0x00);
                                	}
                                	loop--;
								}
                                // Default delay
                                if (!delayOverride & defaultDelay > 0) {
                                        int delayCounter = defaultDelay;
                                        while (delayCounter > 0) {
                                                file.add((byte) 0x00);
                                                if (delayCounter > 255) {
                                                        file.add((byte) 0xFF);
                                                        delayCounter = delayCounter - 255;
                                                } else {
                                                        file.add((byte) delayCounter);
                                                        delayCounter = 0;
                                                }
                                        }
                                }
                        }catch (StringIndexOutOfBoundsException e){
				//do nothing
                        }
                        catch (Exception e) {
                                System.out.println("Erro na linha: " + (i + 1));
                                e.printStackTrace();
                        }
                }

                // Write byte array to file
                byte[] data = new byte[file.size()];
                for (int i = 0; i < file.size(); i++) {
                        data[i] = file.get(i);
                }
                try {
                        File someFile = new File(fileDest);
                        FileOutputStream fos = new FileOutputStream(someFile);
                        fos.write(data);
                        fos.flush();
                        fos.close();
                        System.out.println("DuckyScript Completo.....\t[ OK ]\n");
                } catch (Exception e) {
                        System.out.print("Falha ao gravar arquivo hexadecimal!");
                }
            
        }

        private static void addBytes(List<Byte> file, byte[] byteTab){
                for(int i=0;i<byteTab.length;i++)
                        file.add(byteTab[i]);
                if(byteTab.length % 2 != 0){
                        file.add((byte) 0x00);
                }
        }
        
        private static byte[] charToBytes (char c){
                return codeToBytes(charToCode(c));
        }
        private static String charToCode (char c){
                String code;
                if(c<128){
                code = "ASCII_"+Integer.toHexString(c).toUpperCase();
            }else if (c<256){
                code = "ISO_8859_1_"+Integer.toHexString(c).toUpperCase();
            }else{
                code = "UNICODE_"+Integer.toHexString(c).toUpperCase();
            }
                return code;
        }
        
        private static byte[] codeToBytes (String str){
                if(layoutProps.getProperty(str) != null){
                        String keys[] = layoutProps.getProperty(str).split(",");
                        byte[] byteTab = new byte[keys.length];
                    for(int j=0;j<keys.length;j++){
                        String key = keys[j].trim();
                        if(keyboardProps.getProperty(key) != null){
                                byteTab[j] = strToByte(keyboardProps.getProperty(key).trim());
                        }else if(layoutProps.getProperty(key) != null){
                                byteTab[j] = strToByte(layoutProps.getProperty(key).trim());
                        }else{
                                System.out.println("Chave não encontrada:"+key);
                                byteTab[j] = (byte) 0x00;
                        }
                    }
                        return byteTab;
                }else{
                        System.out.println("Char não encontrado:"+str);
                        byte[] byteTab = new byte[1];
                        byteTab[0] = (byte) 0x00;
                        return byteTab;
                }
        }
        private static byte strToByte(String str) {
                if(str.startsWith("0x")){
                        return (byte)Integer.parseInt(str.substring(2),16);
                }else{
                        return (byte)Integer.parseInt(str);
                }
        }
        
        private static byte strInstrToByte(String instruction){
                instruction = instruction.trim();
                if(keyboardProps.getProperty("KEY_"+instruction)!=null)
                        return strToByte(keyboardProps.getProperty("KEY_"+instruction));
                /* instruction different from the key name */
                if(instruction.equals("ESCAPE"))
                        return strInstrToByte("ESC");
                if(instruction.equals("DEL"))
                        return strInstrToByte("DELETE");
                if(instruction.equals("BREAK"))
                        return strInstrToByte("PAUSE");
                if(instruction.equals("CONTROL"))
                        return strInstrToByte("CTRL");
                if(instruction.equals("DOWNARROW"))
                        return strInstrToByte("DOWN");
                if(instruction.equals("UPARROW"))
                        return strInstrToByte("UP");
                if(instruction.equals("LEFTARROW"))
                        return strInstrToByte("LEFT");
                if(instruction.equals("RIGHTARROW"))
                        return strInstrToByte("RIGHT");
                if(instruction.equals("MENU"))
                        return strInstrToByte("APP");
                if(instruction.equals("WINDOWS"))
                        return strInstrToByte("GUI");
                if(instruction.equals("PLAY") || instruction.equals("PAUSE"))
                        return strInstrToByte("MEDIA_PLAY_PAUSE");
                if(instruction.equals("STOP"))
                        return strInstrToByte("MEDIA_STOP");
                if(instruction.equals("MUTE"))
                        return strInstrToByte("MEDIA_MUTE");
                if(instruction.equals("VOLUMEUP"))
                        return strInstrToByte("MEDIA_VOLUME_INC");
                if(instruction.equals("VOLUMEDOWN"))
                        return strInstrToByte("MEDIA_VOLUME_DEC");
                if(instruction.equals("SCROLLLOCK"))
                        return strInstrToByte("SCROLL_LOCK");
                if(instruction.equals("NUMLOCK"))
                        return strInstrToByte("NUM_LOCK");
                if(instruction.equals("CAPSLOCK"))
                        return strInstrToByte("CAPS_LOCK");
                /* else take first letter */
                return charToBytes(instruction.charAt(0))[0];
        }
}
