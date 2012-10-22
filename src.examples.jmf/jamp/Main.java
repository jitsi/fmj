package jamp;

import java.awt.Frame;

public class Main extends Frame {

    public Main(String [] args) {
        new MainWindow(this, args);
    }

    public static void main(String[] args) {
        Main main = new Main(args);
        main.invokedStandalone = true;
    }
    private boolean invokedStandalone = false;
} 
