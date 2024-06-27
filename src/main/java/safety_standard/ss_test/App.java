package safety_standard.ss_test;

import safety_standard.ss_test.swing_frame.Frame;

import javax.swing.*;

public class App
{
    public static void main( String[] args )
    {
        // Запуск приложения
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Frame().setVisible(true);
            }
        });
    }

}
