package engine.board;

import engine.piece.Move;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PGNWriter {

    public static void saveFen(final String out, final String text, final Board board) throws IOException{
        new File("saves").mkdirs();
        final FileWriter fw = new FileWriter(out);
        for (final char c : text.toCharArray()) fw.append(c);
        fw.append(' ');
        fw.append(board.turn ? 'w' : 'b');
        fw.flush();
    }

    public static String readFen(final String in) throws IOException{
        final File file = new File(in);
        Scanner reader = new Scanner(file);
        String data = reader.nextLine();
        reader.close();
        return data;
    }
}
