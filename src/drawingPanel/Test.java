package drawingPanel;
import java.awt.*;
import javax.swing.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.universe.*;

public class Test extends JFrame implements Runnable {
   int fps = 30;
   Graphics screen;

   public Test() {
      setSize(400, 300);
      setResizable(false);
      setDefaultCloseOperation(EXIT_ON_CLOSE);

      GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
      Canvas3D canvas = new Canvas3D(gc);
      getContentPane().add(canvas);

      BranchGroup scene = new BranchGroup();
      scene.compile();

      SimpleUniverse su = new SimpleUniverse(canvas);
      su.addBranchGraph(scene);

      setVisible(true);

      screen = canvas.getGraphics();

      new Thread(this).start();
   }

   public void run() {
      while(true) {
         try {
            screen.setColor(Color.white);
            screen.setFont(new Font("TimesRoman", Font.ITALIC, 28));
            screen.drawString("abcdefeghijklmonopdkdkdkd",80, 80);

            Thread.currentThread().sleep(1000/fps);
         } catch(InterruptedException e){}
      }
   }

   public static void main(String args[]) {
      new Test();
   }
}