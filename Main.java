import syntaxtree.*;
import visitor.*;

public class Main {
   public static void main(String [] args) {
      System.out.println("Random Program Parser");
      try {
         Node root = new MiniJavaParser(System.in).Goal();
	 		System.out.println("Program parsed successfully");
         root.accept(new GJNoArguDepthFirst()); // Your assignment part is invoked here.
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
} 



