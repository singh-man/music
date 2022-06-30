package com.music;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AVCurses {

    public static void main(String[] args) throws IOException {

        String dir = "C:\\mani\\video\\x\\thePromisedNeverland";
        AVController controller = new AVController();
        List<String> libx265 = controller.encode(dir, 21, "libx265", "-1:-1");
//        libx265.forEach(System.out::println);

        // Setup terminal and screen layers
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        mainPanel.addComponent(panelFile(dir, controller).withBorder(Borders.singleLine("File List")));

        Panel rightPanel = new Panel();
        mainPanel.addComponent(rightPanel.withBorder(Borders.singleLine("Right Panel")));

        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
//        window.setComponent(panel);
        window.setComponent(mainPanel.withBorder(Borders.singleLine("Main Panel")));

        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(window);

    }

    private static Panel panelFile(String dir, AVController controller) {
        Panel leftPanel = new Panel();
        leftPanel.setLayoutManager(new GridLayout(2));
        List<String> files = controller.getFilteredFiles(dir, IAudioVideo::isValidVideoFileExtension).stream()
                .map(e -> e.getAbsolutePath()).collect(Collectors.toList());
        IntStream.rangeClosed(1, files.size()).forEach(i -> {
            Label l = new Label(i + "");
            l.setPreferredSize(new TerminalSize(5, 2));
            leftPanel.addComponent(l);
            Label l1 = new Label(files.get(i-1));
            l1.setPreferredSize(new TerminalSize(40, 2));
            leftPanel.addComponent(l1);
        });
        return leftPanel;

    }
}
